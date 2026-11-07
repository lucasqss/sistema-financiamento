package br.com.financiamento.shared.outbox;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.jboss.logging.Logger;

import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Relay do outbox (ADR 0005, ADR 0013): depois do commit, entrega cada evento às rotas de saída dos
 * sistemas externos, em ordem.
 * <ul>
 *   <li>Cada mensagem é reivindicada numa transação curta, com {@code FOR UPDATE SKIP LOCKED}: mais de uma
 *   instância pode rodar o relay sem enviar a mesma mensagem duas vezes ao mesmo tempo.</li>
 *   <li>A chamada ao externo fica fora da transação do banco.</li>
 *   <li>A ordem vale por agregado: uma mensagem só sai depois das anteriores do mesmo agregado. Um externo fora do
 *   ar não trava os pedidos dos outros.</li>
 *   <li>Falha temporária: nova tentativa com recuo exponencial; as seguintes do mesmo agregado esperam.</li>
 *   <li>Recusa do externo: o relay desiste da mensagem, registra o motivo e conta na métrica.</li>
 *   <li>Evento sem rota é interno: só é marcado como enviado.</li>
 * </ul>
 */
@ApplicationScoped
class RelayDoOutbox {

    private static final Logger LOG = Logger.getLogger(RelayDoOutbox.class);
    static final Duration RECUO_MAXIMO = Duration.ofMinutes(15);
    static final int LOTE = 100;

    @Inject
    EntityManager em;

    @Inject
    @Any
    Instance<RotaDeSaida> rotas;

    @Inject
    Clock relogio;

    @Inject
    MeterRegistry metricas;

    @Scheduled(every = "${outbox.relay.intervalo:5s}", concurrentExecution = Scheduled.ConcurrentExecution.SKIP,
            identity = "relay-do-outbox")
    void agendado() {
        enviarPendentes();
        QuarkusTransaction.requiringNew().run(this::medir);
    }

    private final AtomicLong pendentes = new AtomicLong(), maisAntigoSegundos = new AtomicLong(), desistidos = new AtomicLong();

    @PostConstruct
    void registrarMedidores() {
        metricas.gauge("financiamento.outbox.pendentes", pendentes);
        metricas.gauge("financiamento.outbox.pendente.mais.antigo.segundos", maisAntigoSegundos);
        metricas.gauge("financiamento.outbox.desistidos", desistidos);
    }

    /** Medidores do alerta de outbox parado (ADR 0005): quantos pendentes, há quanto tempo e quantos desistidos. */
    void medir() {
        var r = (Object[]) em.createNativeQuery("""
                select count(*) filter (where publicado_em is null and desistido_em is null),
                       min(ocorrido_em) filter (where publicado_em is null and desistido_em is null),
                       count(*) filter (where desistido_em is not null)
                from shared.outbox""").getSingleResult();
        pendentes.set(((Number) r[0]).longValue());
        maisAntigoSegundos.set(r[1] == null ? 0 : Duration.between(paraInstant(r[1]), relogio.instant()).toSeconds());
        desistidos.set(((Number) r[2]).longValue());
    }

    private static java.time.Instant paraInstant(Object valor) {
        if (valor instanceof java.time.Instant i) return i;
        if (valor instanceof java.time.OffsetDateTime o) return o.toInstant();
        if (valor instanceof java.sql.Timestamp t) return t.toInstant();
        throw new IllegalStateException("data inesperada: " + valor.getClass());
    }

    /** Envia os pendentes que já podem ir. Devolve quantos foram entregues (eventos internos incluídos). */
    public int enviarPendentes() {
        int entregues = 0;
        for (int i = 0; i < LOTE; i++) {
            var proximo = QuarkusTransaction.requiringNew().call(this::reivindicar);
            if (proximo.isEmpty()) break;
            var evento = proximo.get();
            var envio = enviar(evento);
            QuarkusTransaction.requiringNew().run(() -> registrar(evento.id(), envio));
            if (envio.resultado() == Envio.Resultado.ENTREGUE) entregues++;
        }
        return entregues;
    }

    private Optional<EventoDoOutbox> reivindicar() {
        @SuppressWarnings("unchecked")
        List<MensagemOutbox> lista = em.createNativeQuery("""
                        select * from shared.outbox o
                        where o.publicado_em is null and o.desistido_em is null
                          and (o.proxima_tentativa is null or o.proxima_tentativa <= ?1)
                          and not exists (select 1 from shared.outbox a
                                          where a.agregado = o.agregado and a.agregado_id = o.agregado_id and a.id < o.id
                                            and a.publicado_em is null and a.desistido_em is null)
                        order by o.id limit 1 for update of o skip locked""", MensagemOutbox.class)
                .setParameter(1, relogio.instant()).getResultList();
        return lista.stream().findFirst().map(MensagemOutbox::evento);
    }

    private Envio enviar(EventoDoOutbox evento) {
        for (var rota : rotas) {
            if (!rota.atende(evento)) continue;
            Envio envio;
            try {
                envio = rota.enviar(evento);
            } catch (RuntimeException e) {
                envio = Envio.temporaria(e.getClass().getSimpleName() + ": " + e.getMessage());
            }
            if (envio.resultado() != Envio.Resultado.ENTREGUE) {
                LOG.warnf("Envio de %s %s ao %s: %s (%s)", evento.evento(), evento.agregadoId(), rota.externo(),
                        envio.resultado(), envio.motivo());
                metricas.counter("financiamento.outbox.falhas", "externo", rota.externo(),
                        "resultado", envio.resultado().name()).increment();
                return envio;
            }
        }
        return Envio.entregue();
    }

    private void registrar(Long id, Envio envio) {
        var m = em.find(MensagemOutbox.class, id);
        var agora = relogio.instant();
        switch (envio.resultado()) {
            case ENTREGUE -> m.publicadoEm = agora;
            case RECUSADA -> {
                m.tentativas++;
                m.desistidoEm = agora;
                m.ultimoErro = envio.motivo();
            }
            case TEMPORARIA -> {
                m.tentativas++;
                m.ultimoErro = envio.motivo();
                m.proximaTentativa = agora.plus(recuo(m.tentativas));
            }
        }
    }

    /** Recuo exponencial: 2, 4, 8… segundos, até 15 minutos. */
    static Duration recuo(int tentativas) {
        var segundos = 1L << Math.min(tentativas, 20);
        return segundos >= RECUO_MAXIMO.toSeconds() ? RECUO_MAXIMO : Duration.ofSeconds(segundos);
    }
}
