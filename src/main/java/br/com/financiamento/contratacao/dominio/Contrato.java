package br.com.financiamento.contratacao.dominio;

import br.com.financiamento.bem.TipoDeBem;
import br.com.financiamento.contratacao.dominio.assinatura.Signatario;
import br.com.financiamento.contratacao.dominio.condicoes.CondicoesFinais;
import br.com.financiamento.contratacao.dominio.seguro.Seguro;
import br.com.financiamento.shared.validacao.ErroDeDominioException;
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

    /** Resumo da garantia (AGR-0002): o processo de registro fica no módulo Garantia. */
    @Column(name = "garantia_orgao")
    private String orgaoDaGarantia;

    @Column(name = "garantia_protocolo")
    private String protocoloDaGarantia;

    @Column(name = "garantia_situacao")
    private String situacaoDaGarantia;

    /** Só no imóvel na planta (AGR-0002): prazo previsto e prorrogações. */
    @Embedded
    private CronogramaDaObra cronograma;

    @Version
    @Column(name = "versao_registro")
    private long versaoDoRegistro;

    @Transient
    private final List<EventoRegistrado> eventos = new ArrayList<>();

    protected Contrato() {
    }

    /** UC-0014, passo 3. Emite ContratoEmitido; o contrato aguarda as assinaturas. */
    public static Contrato emitir(Long propostaId, TipoDeBem tipo, String documentoDoVendedor, CondicoesFinais condicoes,
                                  List<Seguro> seguros, List<Signatario> signatarios, CronogramaDaObra cronograma,
                                  Instant agora) {
        if (seguros.stream().filter(Seguro::obrigatorio).anyMatch(s -> !s.contratado())) {
            throw new ErroDeDominioException(ErrosDaContratacao.SEGUROS_EXIGIDOS);
        }
        if (signatarios.isEmpty()) throw new ErroDeDominioException(ErrosDaContratacao.SEM_SIGNATARIOS);
        var c = new Contrato();
        c.propostaId = propostaId;
        c.tipoDeBem = tipo.codigo();
        c.documentoDoVendedor = documentoDoVendedor;
        c.condicoes = condicoes;
        c.emitidoEm = agora;
        seguros.forEach(s -> c.seguros.add(s.copia()));
        c.signatarios.addAll(signatarios);
        c.cronograma = cronograma;
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
                .orElseThrow(() -> new ErroDeDominioException(ErrosDaContratacao.NAO_E_SIGNATARIO));
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

    // --- Registro da garantia (UC-0018 a UC-0020): fatos da Garantia que movem o contrato ---

    /** O contrato assinado foi enviado ao órgão (EVT-0027); também o reenvio depois de uma recusa. */
    public void registroSolicitado(String orgao, String protocolo) {
        transicionar(EventoDoContrato.REGISTRO_SOLICITADO, Map.of());
        garantia(orgao, protocolo, "em registro");
    }

    /** Reenvio depois de cumprida a exigência (EVT-0030): volta a "em registro". */
    public void exigenciaCumprida(String protocolo) {
        transicionar(EventoDoContrato.EXIGENCIA_CUMPRIDA, Map.of());
        garantia(orgaoDaGarantia, protocolo, "em registro");
    }

    public void exigenciaEmitida(String exigencia) {
        transicionar(EventoDoContrato.EXIGENCIA_EMITIDA, Map.of());
        this.situacaoDaGarantia = "em exigência: " + exigencia;
    }

    public void registroRecusado(String motivo) {
        transicionar(EventoDoContrato.REGISTRO_RECUSADO, Map.of());
        this.situacaoDaGarantia = "recusado: " + motivo;
    }

    /** RN-0029: o órgão registrou o bem do contrato; a garantia está constituída. */
    public void garantiaRegistrada(String protocolo) {
        transicionar(EventoDoContrato.GARANTIA_REGISTRADA, Map.of());
        garantia(orgaoDaGarantia, protocolo, "constituída");
    }

    // --- Liberação (UC-0021, UC-0023): fatos do Desembolso que movem o contrato ---

    /** O vendedor recebeu o valor financiado (EVT-0033): o contrato do bem pronto está formalizado. */
    public void recursoLiberado() {
        transicionar(EventoDoContrato.RECURSO_LIBERADO, Map.of());
    }

    /**
     * Uma parcela da obra foi paga (EVT-0034). A primeira põe o contrato em obra;
     * a intermediária o mantém em obra; a última o formaliza.
     */
    public void parcelaDaObraLiberada(boolean ultima) {
        if (estado == EstadoContrato.GARANTIA_CONSTITUIDA) {
            transicionarPara(EventoDoContrato.PARCELA_DA_OBRA_LIBERADA, EstadoContrato.EM_OBRA, Map.of());
            if (!ultima) return;
        }
        transicionarPara(EventoDoContrato.PARCELA_DA_OBRA_LIBERADA,
                ultima ? EstadoContrato.FORMALIZADO : EstadoContrato.EM_OBRA, Map.of());
    }

    /**
     * O engenheiro mediu a obra (EVT-0020). Em obra, é a transição do CV-0002; com a
     * garantia recém-constituída, a primeira medição leva direto à primeira parcela.
     */
    public void medicaoRealizada() {
        if (estado == EstadoContrato.GARANTIA_CONSTITUIDA && cronograma != null) return;
        transicionar(EventoDoContrato.MEDICAO_REALIZADA, Map.of());
    }

    // --- Prorrogação do prazo da obra (UC-0017, RN-0027) ---

    /**
     * A incorporadora pede nova data. Dentro do limite de 180 dias somados além do
     * prazo previsto, aplica e emite PrazoDaObraProrrogado; acima, fica pendente
     * para o analista decidir. Devolve se foi aplicada.
     */
    public boolean pedirProrrogacao(java.time.LocalDate novaData) {
        exigirTransicao(EventoDoContrato.PRAZO_DA_OBRA_PRORROGADO);
        if (cronograma.dentroDoLimite(novaData)) {
            aplicarProrrogacao(novaData);
            return true;
        }
        cronograma.deixarPendente(novaData);
        return false;
    }

    /** RN-0027, acima do limite: o analista (ATR-0005) aprova ou nega a prorrogação pendente. */
    public void decidirProrrogacao(boolean aprovada) {
        var pendente = cronograma.pendente();
        if (pendente == null) throw new ErroDeDominioException(ErrosDaContratacao.SEM_PRORROGACAO_PENDENTE);
        if (aprovada) aplicarProrrogacao(pendente);
        else cronograma.negarPendente();
    }

    private void aplicarProrrogacao(java.time.LocalDate novaData) {
        var dias = cronograma.prorrogar(novaData);
        transicionar(EventoDoContrato.PRAZO_DA_OBRA_PRORROGADO, Map.of("novaPrevisao", novaData.toString(),
                "diasProrrogados", dias));
    }

    private void garantia(String orgao, String protocolo, String situacao) {
        this.orgaoDaGarantia = orgao;
        this.protocoloDaGarantia = protocolo;
        this.situacaoDaGarantia = situacao;
    }

    private void exigirTransicao(EventoDoContrato evento) {
        if (!estado.aceita(evento)) {
            throw new ErroDeDominioException(ErrosDaContratacao.TRANSICAO_INVALIDA, nomeDoEstado(), evento.nomeNoModelo());
        }
    }

    private void transicionar(EventoDoContrato evento, Map<String, Object> dados) {
        exigirTransicao(evento);
        this.estado = estado.apos(evento).orElseThrow();
        registrar(evento, dados);
    }

    private void transicionarPara(EventoDoContrato evento, EstadoContrato destino, Map<String, Object> dados) {
        if (!estado.aceita(evento, destino)) {
            throw new ErroDeDominioException(ErrosDaContratacao.DESTINO_INVALIDO, nomeDoEstado(), destino.name().toLowerCase().replace('_', ' '), evento.nomeNoModelo());
        }
        this.estado = destino;
        registrar(evento, dados);
    }

    private void registrar(EventoDoContrato evento, Map<String, Object> dados) {
        if (!evento.emitidoPeloContrato()) return;
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
    public CronogramaDaObra cronograma() { return cronograma; }
    public String orgaoDaGarantia() { return orgaoDaGarantia; }
    public String protocoloDaGarantia() { return protocoloDaGarantia; }
    public String situacaoDaGarantia() { return situacaoDaGarantia; }
}
