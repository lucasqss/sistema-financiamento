package br.com.financiamento.originacao.dominio;

import br.com.financiamento.bem.TipoDeBem;
import br.com.financiamento.originacao.dominio.bem.BemDaProposta;
import br.com.financiamento.originacao.dominio.condicoes.Condicoes;
import br.com.financiamento.originacao.dominio.documento.DocumentoEntregue;
import br.com.financiamento.originacao.dominio.participante.Participante;
import br.com.financiamento.originacao.dominio.resultado.AvaliacaoDaProposta;
import br.com.financiamento.originacao.dominio.resultado.Contraproposta;
import br.com.financiamento.originacao.dominio.resultado.CreditoDaProposta;
import br.com.financiamento.credito.ResultadoDeCredito;
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
import java.time.LocalDate;
import java.time.Period;
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
 * 5. para submeter, a proposta está completa e enquadrada (RN-0018);
 * 7. só é aprovada com avaliação válida e crédito aprovado, ambos posteriores
 *    à última versão das condições (RN-0019, RN-0022).
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

    @Embedded
    private CreditoDaProposta credito;

    @Embedded
    private AvaliacaoDaProposta avaliacao;

    @Embedded
    private Contraproposta contraproposta;

    /** A versão das condições cuja análise de crédito já foi pedida. */
    @Column(name = "credito_solicitado_versao")
    private Integer creditoSolicitadoNaVersao;

    @Column(name = "avaliacao_pendente", nullable = false)
    private boolean avaliacaoPendente;

    /** O que impede a proposta de seguir, quando está com pendência (GLO-0038). */
    private String pendencia;

    @Column(name = "aprovada_em")
    private LocalDate aprovadaEm;

    /** RN-0021: a proposta aprovada vale 30 dias para virar contrato. */
    @Column(name = "aprovacao_valida_ate")
    private LocalDate aprovacaoValidaAte;

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

    // --- Resultados do crédito e da avaliação (UC-0006) ---

    /** RN-0021: validade da proposta aprovada. */
    static final int VALIDADE_DA_APROVACAO_DIAS = 30;

    /**
     * Registra a decisão do crédito. Vale só para a versão atual das condições;
     * a decisão de uma versão anterior, ou chegada a uma proposta encerrada, é
     * guardada no Crédito e não muda a proposta.
     */
    public boolean registrarCredito(CreditoDaProposta resultado) {
        if (!estado.ativa() || condicoes == null || resultado.condicoesVersao() != condicoes.versao()) return false;
        this.credito = resultado;
        return true;
    }

    /** Registra a avaliação; aceita, o valor de avaliação entra no valor de referência (RN-0006). */
    public boolean registrarAvaliacao(AvaliacaoDaProposta resultado) {
        if (!estado.ativa()) return false;
        this.avaliacao = resultado;
        this.avaliacaoPendente = false;
        if (resultado.aceitoComoGarantia()) this.bem = bem.comAvaliacao(resultado.valor());
        return true;
    }

    /**
     * UC-0006: decide a proposta em análise com o que já chegou. Sem resultado
     * suficiente, espera.
     *
     * @param enquadramento o enquadramento com o valor de avaliação atual
     */
    public void decidir(Enquadramento enquadramento, LocalDate hoje, Period validadeDaContraproposta) {
        if (estado != EstadoProposta.EM_ANALISE) return;
        var creditoVigente = creditoVigente();
        if (creditoVigente != null && creditoVigente.resultado() == ResultadoDeCredito.REPROVADO) {
            transicionar(EventoDaProposta.PROPOSTA_REPROVADA, Map.of("motivo", "crédito reprovado"));
        } else if (avaliacao != null && !avaliacao.aceitoComoGarantia()) {
            transicionar(EventoDaProposta.PROPOSTA_REPROVADA, Map.of("motivo", "bem recusado como garantia"));
        } else if (!enquadramento.enquadrada()) {
            entrarEmPendencia(enquadramento.violacoes().stream().map(Object::toString)
                    .reduce((a, b) -> a + "; " + b).orElse(""));
        } else if (avaliacao != null && avaliacao.vencidaEm(hoje)) {
            entrarEmPendencia("RN-0022: avaliação vencida; o bem precisa ser reavaliado.");
        } else if (creditoVigente != null && creditoVigente.resultado() == ResultadoDeCredito.APROVADO_COM_RESSALVA) {
            this.contraproposta = new Contraproposta(creditoVigente.sugeridas(), creditoVigente.ressalva(),
                    hoje.plus(validadeDaContraproposta));
            transicionar(EventoDaProposta.CONTRAPROPOSTA_EMITIDA,
                    Map.of("validaAte", contraproposta.validaAte().toString(), "ressalva", creditoVigente.ressalva()));
        } else if (creditoVigente != null && creditoVigente.resultado() == ResultadoDeCredito.APROVADO
                && avaliacao != null) {
            this.aprovadaEm = hoje;
            this.aprovacaoValidaAte = hoje.plusDays(VALIDADE_DA_APROVACAO_DIAS);
            transicionar(EventoDaProposta.PROPOSTA_APROVADA, Map.of("validaAte", aprovacaoValidaAte.toString()));
        }
    }

    /** UC-0009: aceita, as condições da contraproposta viram a nova versão, já aprovada pelo crédito. */
    public void aceitarContraproposta() {
        transicionar(EventoDaProposta.CONTRAPROPOSTA_ACEITA, Map.of());
        int versao = condicoes.versao() + 1;
        this.condicoes = new Condicoes(contraproposta.entradaPropria(), contraproposta.entradaFgts(),
                contraproposta.prazoMeses(), contraproposta.parcela(), versao);
        this.credito = credito.aprovadoNaVersao(versao);
        this.creditoSolicitadoNaVersao = versao;
        this.contraproposta = null;
    }

    /** UC-0009, fluxo alternativo: recusada ou vencida, a proposta é reprovada. */
    public void recusarContraproposta(boolean vencida) {
        transicionar(EventoDaProposta.CONTRAPROPOSTA_RECUSADA, Map.of("vencida", vencida));
    }

    /** UC-0008: quem agiu resolveu; a proposta volta à análise, reenquadrada (RN-0018). */
    public void resolverPendencia(Enquadramento enquadramento) {
        exigirTransicao(EventoDaProposta.PENDENCIA_RESOLVIDA);
        if (!enquadramento.enquadrada()) throw new RegraVioladaException(enquadramento.violacoes());
        this.pendencia = null;
        transicionar(EventoDaProposta.PENDENCIA_RESOLVIDA, Map.of("condicoesVersao", condicoes.versao()));
    }

    /** UC-0007: a aprovada que passou da validade sem contrato expira (RN-0021). */
    public boolean expirarSeVencida(LocalDate hoje) {
        if (estado != EstadoProposta.APROVADA || !aprovacaoValidaAte.isBefore(hoje)) return false;
        transicionar(EventoDaProposta.PROPOSTA_EXPIRADA, Map.of("validaAte", aprovacaoValidaAte.toString()));
        return true;
    }

    /** RN-0022: avaliação vencida antes do contrato põe a proposta em pendência, para reavaliar o bem. */
    public boolean pendenciaSeAvaliacaoVencida(LocalDate hoje) {
        if (estado != EstadoProposta.APROVADA || avaliacao == null || !avaliacao.vencidaEm(hoje)) return false;
        this.pendencia = "RN-0022: avaliação vencida; o bem precisa ser reavaliado.";
        transicionar(EventoDaProposta.AVALIACAO_VENCIDA, Map.of("validaAte", avaliacao.validaAte().toString()));
        return true;
    }

    /** RN-0020: a contraproposta vencida conta como recusada. */
    public boolean recusarContrapropostaSeVencida(LocalDate hoje) {
        if (estado != EstadoProposta.EM_CONTRAPROPOSTA || !contraproposta.vencidaEm(hoje)) return false;
        recusarContraproposta(true);
        return true;
    }

    /** O crédito precisa analisar a versão atual das condições, e ainda não foi pedido? */
    public boolean precisaDeAnaliseDeCredito() {
        return condicoes != null && !Integer.valueOf(condicoes.versao()).equals(creditoSolicitadoNaVersao);
    }

    /** O bem precisa de (nova) avaliação, e ainda não foi pedida? */
    public boolean precisaDeAvaliacao(LocalDate hoje) {
        return !avaliacaoPendente && (avaliacao == null || avaliacao.vencidaEm(hoje));
    }

    public void analiseDeCreditoSolicitada() {
        this.creditoSolicitadoNaVersao = condicoes.versao();
    }

    public void avaliacaoSolicitada() {
        this.avaliacaoPendente = true;
    }

    private CreditoDaProposta creditoVigente() {
        return credito != null && condicoes != null && credito.condicoesVersao() == condicoes.versao() ? credito : null;
    }

    private void entrarEmPendencia(String motivo) {
        this.pendencia = motivo;
        transicionar(EventoDaProposta.PROPOSTA_COM_PENDENCIA, Map.of("pendencia", motivo));
    }

    private void exigirTransicao(EventoDaProposta evento) {
        if (estado.apos(evento).isEmpty()) {
            throw new RegraVioladaException("CV-0001",
                    "A proposta " + nomeDoEstado() + " não aceita " + evento.nomeNoModelo() + ".");
        }
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

    public CreditoDaProposta credito() { return credito; }
    public AvaliacaoDaProposta avaliacao() { return avaliacao; }
    public Contraproposta contraproposta() { return contraproposta; }
    public String pendencia() { return pendencia; }
    public LocalDate aprovacaoValidaAte() { return aprovacaoValidaAte; }

    public Participante proponente() {
        return participantes.stream().filter(Participante::proponente).findFirst().orElse(null);
    }
}
