package br.com.financiamento.contratacao.dominio;

import br.com.financiamento.bem.TipoDeBem;
import br.com.financiamento.contratacao.dominio.assinatura.Signatario;
import br.com.financiamento.contratacao.dominio.condicoes.CondicoesFinais;
import br.com.financiamento.contratacao.dominio.seguro.Seguro;
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

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Agregado Contrato (AGR-0002): fronteira de consistência da formalização.
 * Referencia a proposta pelo identificador, sem alterá-la.
 *
 * Invariantes guardadas aqui:
 * 1. condições imutáveis após a emissão;
 * 2. só é emitido com os seguros exigidos contratados (RN-0025);
 * 3. só fica assinado com todas as assinaturas exigidas (RN-0026);
 * e o estado só muda pelas transições do ciclo CV-0002.
 */
@Entity
@Table(schema = "contratacao", name = "contrato")
public class Contrato extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "proposta_id", nullable = false, updatable = false, unique = true)
    private Long propostaId;

    @Column(name = "tipo_de_bem", nullable = false, updatable = false)
    private String tipoDeBem;

    @Column(name = "documento_do_vendedor", nullable = false, updatable = false)
    private String documentoDoVendedor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoContrato estado;

    @Column(name = "emitido_em", nullable = false, updatable = false)
    private Instant emitidoEm;

    @Embedded
    private CondicoesFinais condicoes;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(schema = "contratacao", name = "contrato_seguro", joinColumns = @JoinColumn(name = "contrato_id"))
    @OrderColumn(name = "ordem")
    private List<Seguro> seguros = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(schema = "contratacao", name = "contrato_signatario", joinColumns = @JoinColumn(name = "contrato_id"))
    @OrderColumn(name = "ordem")
    private List<Signatario> signatarios = new ArrayList<>();

    @Version
    @Column(name = "versao_registro")
    private long versaoDoRegistro;

    @Transient
    private final List<EventoRegistrado> eventos = new ArrayList<>();

    protected Contrato() {
    }

    /** UC-0014, passo 3. Emite ContratoEmitido; o contrato aguarda as assinaturas. */
    public static Contrato emitir(Long propostaId, TipoDeBem tipo, String documentoDoVendedor, CondicoesFinais condicoes,
                                  List<Seguro> seguros, List<Signatario> signatarios, Instant agora) {
        if (seguros.stream().filter(Seguro::obrigatorio).anyMatch(s -> !s.contratado())) {
            throw new RegraVioladaException("RN-0025", "O contrato só é emitido com os seguros exigidos contratados.");
        }
        if (signatarios.isEmpty()) throw new RegraVioladaException("RN-0026", "O contrato precisa de signatários.");
        var c = new Contrato();
        c.propostaId = propostaId;
        c.tipoDeBem = tipo.codigo();
        c.documentoDoVendedor = documentoDoVendedor;
        c.condicoes = condicoes;
        c.emitidoEm = agora;
        seguros.forEach(s -> c.seguros.add(s.copia()));
        c.signatarios.addAll(signatarios);
        c.estado = EstadoContrato.ESTADO_INICIAL;
        c.registrar(EstadoContrato.EVENTO_INICIAL, Map.of("propostaId", propostaId,
                "signatarios", signatarios.stream().map(Signatario::documento).toList()));
        return c;
    }

    /**
     * UC-0015: um signatário exigido assina. Repetida, a assinatura não tem novo
     * efeito e devolve false. Com a última, emite ContratoAssinado.
     */
    public boolean assinar(String documento, Instant agora) {
        exigirTransicao(EventoDoContrato.CONTRATO_ASSINADO);
        var digitos = documento == null ? "" : documento.replaceAll("\\D", "");
        var signatario = signatarios.stream().filter(s -> s.documento().equals(digitos)).findFirst()
                .orElseThrow(() -> new RegraVioladaException("RN-0026", "Quem não é signatário exigido não assina o contrato."));
        if (signatario.assinou()) return false;
        signatario.assinar(agora);
        if (signatarios.stream().allMatch(Signatario::assinou)) {
            transicionar(EventoDoContrato.CONTRATO_ASSINADO, Map.of("propostaId", propostaId));
        }
        return true;
    }

    /** UC-0016: desfeito antes de formalizado. Emite ContratoCancelado. */
    public void cancelar(String motivo) {
        transicionar(EventoDoContrato.CONTRATO_CANCELADO, Map.of("motivo", motivo));
    }

    private void exigirTransicao(EventoDoContrato evento) {
        if (estado.apos(evento).isEmpty()) {
            throw new RegraVioladaException("CV-0002",
                    "O contrato " + nomeDoEstado() + " não aceita " + evento.nomeNoModelo() + ".");
        }
    }

    private void transicionar(EventoDoContrato evento, Map<String, Object> dados) {
        exigirTransicao(evento);
        this.estado = estado.apos(evento).orElseThrow();
        registrar(evento, dados);
    }

    private void registrar(EventoDoContrato evento, Map<String, Object> dados) {
        var completos = new LinkedHashMap<String, Object>(dados);
        completos.put("estado", estado.name());
        eventos.add(new EventoRegistrado(evento.nomeNoModelo(), completos));
    }

    private String nomeDoEstado() {
        return estado.name().toLowerCase().replace('_', ' ');
    }

    public List<EventoRegistrado> retirarEventos() {
        var saida = List.copyOf(eventos);
        eventos.clear();
        return saida;
    }

    public Long id() { return id; }
    public Long propostaId() { return propostaId; }
    public TipoDeBem tipoDeBem() { return TipoDeBem.de(tipoDeBem); }
    public String documentoDoVendedor() { return documentoDoVendedor; }
    public EstadoContrato estado() { return estado; }
    public Instant emitidoEm() { return emitidoEm; }
    public CondicoesFinais condicoes() { return condicoes; }
    public List<Seguro> seguros() { return List.copyOf(seguros); }
    public List<Signatario> signatarios() { return List.copyOf(signatarios); }
}
