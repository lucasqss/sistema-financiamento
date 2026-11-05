package br.com.financiamento.desembolso.dominio;

import br.com.financiamento.bem.FormaDeLiberacao;
import br.com.financiamento.desembolso.dominio.liberacao.Liberacao;
import br.com.financiamento.shared.dominio.RegraVioladaException;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * O desembolso de um contrato (DOM-0006). Só existe com a garantia constituída
 * (RN-0030): nasce quando a Garantia a confirma. Paga o vendedor de uma vez, no
 * bem pronto (UC-0021), ou por medição, no imóvel na planta (UC-0023, RN-0032),
 * sem nunca passar do valor financiado (RN-0031). Com FGTS na entrada, a
 * liberação espera o FGTS ou a entrada recomposta (UC-0022).
 */
@Entity
@Table(schema = "desembolso", name = "desembolso")
public class Desembolso extends PanacheEntityBase {

    static final BigDecimal CEM = new BigDecimal("100");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contrato_id", nullable = false, updatable = false, unique = true)
    private Long contratoId;

    @Column(name = "proposta_id", nullable = false, updatable = false)
    private Long propostaId;

    @Column(name = "valor_financiado", nullable = false, updatable = false)
    private BigDecimal valorFinanciado;

    @Column(name = "entrada_fgts", nullable = false, updatable = false)
    private BigDecimal entradaFgts;

    /** O vendedor da proposta: o único favorecido (RN-0031). */
    @Column(nullable = false, updatable = false)
    private String favorecido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private FormaDeLiberacao forma;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SituacaoDoFgts fgts;

    /** Na obra: o último percentual acumulado medido e o já coberto por parcelas pedidas. */
    @Column(name = "percentual_medido", nullable = false)
    private BigDecimal percentualMedido = BigDecimal.ZERO;

    @Column(name = "percentual_liberado", nullable = false)
    private BigDecimal percentualLiberado = BigDecimal.ZERO;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(schema = "desembolso", name = "liberacao", joinColumns = @JoinColumn(name = "desembolso_id"))
    @OrderColumn(name = "ordem")
    private List<Liberacao> liberacoes = new ArrayList<>();

    @Version
    @Column(name = "versao_registro")
    private long versaoDoRegistro;

    @Transient
    private final List<EventoRegistrado> eventos = new ArrayList<>();

    protected Desembolso() {
    }

    /**
     * Garantia constituída: abre o desembolso. Com FGTS, pede-o primeiro (EVT-0035);
     * sem FGTS, o bem pronto já tem a liberação pedida (EVT-0032).
     */
    public static Desembolso abrir(Long contratoId, Long propostaId, BigDecimal valorFinanciado, BigDecimal entradaFgts,
                                   String favorecido, FormaDeLiberacao forma, boolean garantiaConstituida, Instant agora) {
        if (!garantiaConstituida) {
            throw new RegraVioladaException("RN-0030", "Nenhuma liberação antes de a garantia estar registrada e confirmada.");
        }
        var d = new Desembolso();
        d.contratoId = contratoId;
        d.propostaId = propostaId;
        d.valorFinanciado = valorFinanciado;
        d.entradaFgts = entradaFgts == null ? BigDecimal.ZERO : entradaFgts;
        d.favorecido = favorecido;
        d.forma = forma;
        if (d.entradaFgts.signum() > 0) {
            d.fgts = SituacaoDoFgts.SOLICITADO;
            d.registrar("FgtsSolicitado", Map.of("valor", d.entradaFgts, "favorecido", favorecido));
        } else {
            d.fgts = SituacaoDoFgts.NAO_SE_APLICA;
            d.seguir(agora);
        }
        return d;
    }

    /** Retorno do sistema do FGTS (ATR-0012). Repetido, não tem novo efeito. */
    public void fgtsRespondido(boolean liberado, String motivo, Instant agora) {
        if (fgts != SituacaoDoFgts.SOLICITADO) return;
        if (liberado) {
            fgts = SituacaoDoFgts.LIBERADO;
            registrar("FgtsLiberado", Map.of("valor", entradaFgts, "favorecido", favorecido));
            seguir(agora);
        } else {
            fgts = SituacaoDoFgts.NEGADO;
            registrar("FgtsNegado", Map.of("motivo", motivo == null ? "" : motivo));
        }
    }

    /** UC-0022: com o FGTS negado, o proponente paga essa parte com recurso próprio; a liberação segue. */
    public void entradaRecomposta(Instant agora) {
        if (fgts != SituacaoDoFgts.NEGADO) {
            throw new RegraVioladaException("UC-0022", "Só se recompõe a entrada com o FGTS negado.");
        }
        fgts = SituacaoDoFgts.RECOMPOSTO;
        seguir(agora);
    }

    /** UC-0023: nova medição da obra (RN-0024); com a entrada resolvida, pede a parcela (RN-0032). */
    public void medicaoRealizada(BigDecimal percentualAcumulado, Instant agora) {
        if (forma != FormaDeLiberacao.POR_MEDICAO) {
            throw new RegraVioladaException("UC-0013", "Medição de obra só no imóvel na planta.");
        }
        if (percentualAcumulado.compareTo(percentualMedido) > 0) percentualMedido = percentualAcumulado;
        seguir(agora);
    }

    /**
     * Confirmação do pagamento ao vendedor, pelo protocolo. Idempotente: a mesma
     * confirmação repetida devolve vazio. Paga, devolve a liberação, e emite
     * RecursoLiberado (bem pronto) ou ParcelaDaObraLiberada (na planta).
     */
    public Optional<Liberacao> pagamentoConfirmado(String protocolo, Instant agora) {
        var liberacao = liberacoes.stream().filter(l -> l.protocolo().equals(protocolo)).findFirst()
                .orElseThrow(() -> new RegraVioladaException("RN-0031", "Protocolo de liberação desconhecido: " + protocolo + "."));
        if (liberacao.paga()) return Optional.empty();
        liberacao.pagar(agora);
        var dados = Map.<String, Object>of("protocolo", protocolo, "valor", liberacao.valor(), "favorecido", favorecido,
                "totalLiberado", totalLiberado());
        registrar(forma == FormaDeLiberacao.UNICA ? "RecursoLiberado" : "ParcelaDaObraLiberada", dados);
        return Optional.of(liberacao);
    }

    /** Pede a próxima liberação, se a entrada está resolvida e há o que liberar. */
    private void seguir(Instant agora) {
        if (!fgts.resolvido()) return;
        if (forma == FormaDeLiberacao.UNICA) {
            if (liberacoes.isEmpty()) pedir(valorFinanciado, null, true, agora);
            return;
        }
        var aLiberar = percentualMedido.subtract(percentualLiberado);
        if (aLiberar.signum() <= 0) return;
        // RN-0032: parcela = valor financiado × (acumulado medido − já liberado).
        var ultima = percentualMedido.compareTo(CEM) == 0;
        var valor = ultima
                ? valorFinanciado.subtract(totalPedido())
                : valorFinanciado.multiply(aLiberar).divide(CEM, 2, RoundingMode.HALF_UP);
        pedir(valor, percentualMedido, ultima, agora);
        percentualLiberado = percentualMedido;
    }

    private void pedir(BigDecimal valor, BigDecimal percentualAte, boolean ultima, Instant agora) {
        // RN-0031: a soma das liberações nunca passa do valor financiado.
        if (totalPedido().add(valor).compareTo(valorFinanciado) > 0) {
            throw new RegraVioladaException("RN-0031", "A soma das liberações passaria do valor financiado.");
        }
        var protocolo = "LIB-" + contratoId + "-" + (liberacoes.size() + 1);
        liberacoes.add(new Liberacao(protocolo, valor, percentualAte, ultima, agora));
        var dados = new LinkedHashMap<String, Object>(Map.of("protocolo", protocolo, "valor", valor, "favorecido", favorecido));
        if (percentualAte != null) dados.put("percentualAcumulado", percentualAte);
        registrar("LiberacaoSolicitada", dados);
    }

    private BigDecimal totalPedido() {
        return liberacoes.stream().map(Liberacao::valor).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal totalLiberado() {
        return liberacoes.stream().filter(Liberacao::paga).map(Liberacao::valor).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void registrar(String evento, Map<String, Object> dados) {
        var completos = new LinkedHashMap<String, Object>(dados);
        completos.put("contratoId", contratoId);
        eventos.add(new EventoRegistrado(evento, completos));
    }

    public List<EventoRegistrado> retirarEventos() {
        var saida = List.copyOf(eventos);
        eventos.clear();
        return saida;
    }

    /** O contrato de um protocolo de liberação ("LIB-<contrato>-<n>"). */
    public static Optional<Long> contratoDoProtocolo(String protocolo) {
        var m = java.util.regex.Pattern.compile("LIB-(\\d+)-\\d+").matcher(protocolo == null ? "" : protocolo.trim());
        return m.matches() ? Optional.of(Long.valueOf(m.group(1))) : Optional.empty();
    }

    public Long id() { return id; }
    public Long contratoId() { return contratoId; }
    public Long propostaId() { return propostaId; }
    public BigDecimal valorFinanciado() { return valorFinanciado; }
    public BigDecimal entradaFgts() { return entradaFgts; }
    public String favorecido() { return favorecido; }
    public FormaDeLiberacao forma() { return forma; }
    public SituacaoDoFgts fgts() { return fgts; }
    public BigDecimal percentualMedido() { return percentualMedido; }
    public BigDecimal percentualLiberado() { return percentualLiberado; }
    public List<Liberacao> liberacoes() { return List.copyOf(liberacoes); }
}
