package br.com.financiamento.originacao.dominio;

import br.com.financiamento.bem.TipoDeBem;
import br.com.financiamento.originacao.dominio.bem.BemDaProposta;
import br.com.financiamento.originacao.dominio.condicoes.Condicoes;
import br.com.financiamento.originacao.dominio.documento.DocumentoEntregue;
import br.com.financiamento.originacao.dominio.participante.Participante;
import br.com.financiamento.shared.dominio.RegraVioladaException;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Agregado Proposta (AGR-0001): fronteira de consistência da originação.
 *
 * Invariantes guardadas aqui:
 * 1. o tipo de bem nunca muda depois de iniciada;
 * 2. só o rascunho e a proposta com pendência aceitam alteração de dados;
 * 3. o estado só muda pelas transições do ciclo de vida CV-0001;
 * 5. para submeter, a proposta está completa e enquadrada (RN-0018).
 * As regras que precisam de dados de fora (plug-in do tipo, outras propostas)
 * são aplicadas pelo {@link Enquadrador}.
 */
@Entity
@Table(schema = "originacao", name = "proposta")
public class Proposta extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_de_bem", nullable = false, updatable = false)
    private String tipoDeBem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoProposta estado;

    @Column(name = "iniciada_em", nullable = false, updatable = false)
    private Instant iniciadaEm;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(schema = "originacao", name = "proposta_participante", joinColumns = @JoinColumn(name = "proposta_id"))
    @OrderColumn(name = "ordem")
    private List<Participante> participantes = new ArrayList<>();

    @Embedded
    private BemDaProposta bem;

    @Embedded
    private Condicoes condicoes;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(schema = "originacao", name = "proposta_documento", joinColumns = @JoinColumn(name = "proposta_id"))
    @OrderColumn(name = "ordem")
    private List<DocumentoEntregue> documentos = new ArrayList<>();

    @Version
    @Column(name = "versao_registro")
    private long versaoDoRegistro;

    @Transient
    private final List<EventoRegistrado> eventos = new ArrayList<>();

    protected Proposta() {
    }

    /** UC-0001: a proposta nasce em rascunho, com o tipo de bem fixado. Emite PropostaIniciada. */
    public static Proposta iniciar(TipoDeBem tipo, Instant agora) {
        var p = new Proposta();
        p.tipoDeBem = tipo.codigo();
        p.iniciadaEm = agora;
        p.estado = EstadoProposta.ESTADO_INICIAL;
        p.registrar(EstadoProposta.EVENTO_INICIAL, Map.of("tipoDeBem", tipo.codigo()));
        return p;
    }

    /** UC-0002. */
    public void informarParticipantes(List<Participante> novos) {
        exigirAlteravel();
        participantes.clear();
        participantes.addAll(novos);
    }

    /** UC-0003. */
    public void informarBem(BemDaProposta novo) {
        exigirAlteravel();
        this.bem = novo;
    }

    /** UC-0004: cada mudança de condições é uma nova versão (invariante 6: novo enquadramento). */
    public void informarCondicoes(BigDecimal entradaPropria, BigDecimal entradaFgts, int prazoMeses, BigDecimal parcela) {
        exigirAlteravel();
        int versao = condicoes == null ? 1 : condicoes.versao() + 1;
        this.condicoes = new Condicoes(entradaPropria, entradaFgts, prazoMeses, parcela, versao);
    }

    /** RN-0009: o mesmo documento não é entregue duas vezes para o mesmo titular. */
    public void registrarDocumento(DocumentoEntregue documento) {
        exigirAlteravel();
        if (documentos.contains(documento)) {
            throw new RegraVioladaException("RN-0009", "Documento já entregue: " + documento.tipo()
                    + " (" + documento.titular() + ").");
        }
        documentos.add(documento);
    }

    /** UC-0005: só submete enquadrada (RN-0018). Emite PropostaSubmetida. */
    public void submeter(Enquadramento enquadramento) {
        if (!enquadramento.enquadrada()) throw new RegraVioladaException(enquadramento.violacoes());
        transicionar(EventoDaProposta.PROPOSTA_SUBMETIDA, Map.of("condicoesVersao", condicoes.versao()));
    }

    /** UC-0010: o proponente desiste antes do contrato. Emite PropostaCancelada. */
    public void cancelar() {
        transicionar(EventoDaProposta.PROPOSTA_CANCELADA, Map.of());
    }

    private void transicionar(EventoDaProposta evento, Map<String, Object> dados) {
        this.estado = estado.apos(evento).orElseThrow(() -> new RegraVioladaException("CV-0001",
                "A proposta " + nomeDoEstado() + " não aceita " + evento.nomeNoModelo() + "."));
        registrar(evento, dados);
    }

    private void registrar(EventoDaProposta evento, Map<String, Object> dados) {
        var completos = new LinkedHashMap<String, Object>(dados);
        completos.put("estado", estado.name());
        eventos.add(new EventoRegistrado(evento.nomeNoModelo(), completos));
    }

    private void exigirAlteravel() {
        if (!estado.aceitaAlteracao()) {
            throw new RegraVioladaException("AGR-0001",
                    "Só o rascunho e a proposta com pendência aceitam alteração; esta está " + nomeDoEstado() + ".");
        }
    }

    private String nomeDoEstado() {
        return estado.name().toLowerCase().replace('_', ' ');
    }

    /** Os eventos emitidos desde a última leitura; quem lê os leva ao outbox. */
    public List<EventoRegistrado> retirarEventos() {
        var saida = List.copyOf(eventos);
        eventos.clear();
        return saida;
    }

    public Long id() { return id; }
    public TipoDeBem tipoDeBem() { return TipoDeBem.de(tipoDeBem); }
    public EstadoProposta estado() { return estado; }
    public Instant iniciadaEm() { return iniciadaEm; }
    public List<Participante> participantes() { return List.copyOf(participantes); }
    public BemDaProposta bem() { return bem; }
    public Condicoes condicoes() { return condicoes; }
    public List<DocumentoEntregue> documentos() { return List.copyOf(documentos); }

    public Participante proponente() {
        return participantes.stream().filter(Participante::proponente).findFirst().orElse(null);
    }
}
