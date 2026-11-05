package br.com.financiamento.contratacao.dominio;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static br.com.financiamento.contratacao.dominio.EventoDoContrato.*;

/**
 * Estados do contrato e as transições do ciclo de vida CV-0002. "Em obra" só
 * existe no imóvel na planta. Um evento pode levar a mais de um estado, e quem
 * transiciona escolhe: a parcela da obra intermediária fica em obra, a última
 * formaliza. O teste CicloDeVidaDoContratoTest confere a tabela contra o
 * diagrama do modelo de negócio.
 */
public enum EstadoContrato {
    AGUARDANDO_ASSINATURA,
    ASSINADO,
    EM_REGISTRO,
    EM_EXIGENCIA,
    REGISTRO_RECUSADO,
    GARANTIA_CONSTITUIDA,
    EM_OBRA,
    FORMALIZADO,
    CANCELADO;

    private static final Map<EstadoContrato, Map<EventoDoContrato, Set<EstadoContrato>>> TRANSICOES =
            new EnumMap<>(EstadoContrato.class);

    static {
        transicao(AGUARDANDO_ASSINATURA, CONTRATO_ASSINADO, ASSINADO);
        transicao(AGUARDANDO_ASSINATURA, CONTRATO_CANCELADO, CANCELADO);
        transicao(ASSINADO, REGISTRO_SOLICITADO, EM_REGISTRO);
        transicao(EM_REGISTRO, EXIGENCIA_EMITIDA, EM_EXIGENCIA);
        transicao(EM_EXIGENCIA, EXIGENCIA_CUMPRIDA, EM_REGISTRO);
        transicao(EM_REGISTRO, EventoDoContrato.REGISTRO_RECUSADO, REGISTRO_RECUSADO);
        transicao(REGISTRO_RECUSADO, REGISTRO_SOLICITADO, EM_REGISTRO);
        transicao(REGISTRO_RECUSADO, CONTRATO_CANCELADO, CANCELADO);
        transicao(EM_REGISTRO, GARANTIA_REGISTRADA, GARANTIA_CONSTITUIDA);
        transicao(GARANTIA_CONSTITUIDA, RECURSO_LIBERADO, FORMALIZADO);
        transicao(GARANTIA_CONSTITUIDA, PARCELA_DA_OBRA_LIBERADA, EM_OBRA);
        transicao(EM_OBRA, MEDICAO_REALIZADA, EM_OBRA);
        transicao(EM_OBRA, PRAZO_DA_OBRA_PRORROGADO, EM_OBRA);
        transicao(EM_OBRA, PARCELA_DA_OBRA_LIBERADA, EM_OBRA);
        transicao(EM_OBRA, PARCELA_DA_OBRA_LIBERADA, FORMALIZADO);
    }

    private static void transicao(EstadoContrato de, EventoDoContrato evento, EstadoContrato para) {
        TRANSICOES.computeIfAbsent(de, e -> new EnumMap<>(EventoDoContrato.class))
                .computeIfAbsent(evento, e -> EnumSet.noneOf(EstadoContrato.class)).add(para);
    }

    static final EventoDoContrato EVENTO_INICIAL = CONTRATO_EMITIDO;
    static final EstadoContrato ESTADO_INICIAL = AGUARDANDO_ASSINATURA;

    /** O único destino do evento a partir deste estado; vazio se o evento não é aceito ou tem mais de um destino. */
    Optional<EstadoContrato> apos(EventoDoContrato evento) {
        var destinos = transicoes().getOrDefault(evento, Set.of());
        return destinos.size() == 1 ? Optional.of(destinos.iterator().next()) : Optional.empty();
    }

    boolean aceita(EventoDoContrato evento, EstadoContrato destino) {
        return transicoes().getOrDefault(evento, Set.of()).contains(destino);
    }

    boolean aceita(EventoDoContrato evento) {
        return transicoes().containsKey(evento);
    }

    Map<EventoDoContrato, Set<EstadoContrato>> transicoes() {
        return Collections.unmodifiableMap(TRANSICOES.getOrDefault(this, Map.of()));
    }

    boolean finalizado() {
        return transicoes().isEmpty();
    }
}
