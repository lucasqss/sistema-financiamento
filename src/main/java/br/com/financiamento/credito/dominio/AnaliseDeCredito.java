package br.com.financiamento.credito.dominio;

import br.com.financiamento.credito.CondicoesSugeridas;
import br.com.financiamento.credito.ResultadoDeCredito;
import br.com.financiamento.shared.validacao.ErroDeDominioException;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Uma análise de crédito (GLO-0035) de uma versão das condições de uma
 * proposta. Nasce solicitada e recebe uma única decisão: do bureau ou, fora da
 * alçada automática, do analista.
 */
@Entity
@Table(schema = "credito", name = "analise")
public class AnaliseDeCredito extends PanacheEntityBase {

    public enum Origem { BUREAU, ANALISTA }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "proposta_id", nullable = false, updatable = false)
    private Long propostaId;

    @Column(name = "condicoes_versao", nullable = false, updatable = false)
    private int condicoesVersao;

    @Column(name = "valor_financiado", nullable = false)
    private BigDecimal valorFinanciado;

    @Column(name = "prazo_meses", nullable = false)
    private int prazoMeses;

    @Column(nullable = false)
    private BigDecimal parcela;

    @Column(name = "renda_somada", nullable = false)
    private BigDecimal rendaSomada;

    @Column(name = "solicitada_em", nullable = false)
    private Instant solicitadaEm;

    @Enumerated(EnumType.STRING)
    private ResultadoDeCredito resultado;

    @Enumerated(EnumType.STRING)
    @Column(name = "decidida_por")
    private Origem decididaPor;

    @Column(name = "decidida_em")
    private Instant decididaEm;

    private String ressalva;

    @Column(name = "sugerida_entrada_propria")
    private BigDecimal sugeridaEntradaPropria;

    @Column(name = "sugerida_entrada_fgts")
    private BigDecimal sugeridaEntradaFgts;

    @Column(name = "sugerido_prazo_meses")
    private Integer sugeridoPrazoMeses;

    @Column(name = "sugerida_parcela")
    private BigDecimal sugeridaParcela;

    protected AnaliseDeCredito() {
    }

    public static AnaliseDeCredito solicitar(Long propostaId, int condicoesVersao, BigDecimal valorFinanciado,
                                             int prazoMeses, BigDecimal parcela, BigDecimal rendaSomada, Instant agora) {
        var a = new AnaliseDeCredito();
        a.propostaId = propostaId;
        a.condicoesVersao = condicoesVersao;
        a.valorFinanciado = valorFinanciado;
        a.prazoMeses = prazoMeses;
        a.parcela = parcela;
        a.rendaSomada = rendaSomada;
        a.solicitadaEm = agora;
        return a;
    }

    /**
     * Registra a decisão. Idempotente: a mesma decisão repetida não tem novo
     * efeito e devolve false; uma decisão diferente da já registrada é recusada.
     */
    public boolean decidir(ResultadoDeCredito resultado, String ressalva, CondicoesSugeridas sugeridas,
                           Origem origem, Instant agora) {
        if (this.resultado != null) {
            if (this.resultado == resultado) return false;
            throw new ErroDeDominioException(ErrosDoCredito.ANALISE_JA_DECIDIDA, id, this.resultado);
        }
        if (resultado == ResultadoDeCredito.APROVADO_COM_RESSALVA) {
            if (sugeridas == null || ressalva == null || ressalva.isBlank()) {
                throw new ErroDeDominioException(ErrosDoCredito.RESSALVA_INCOMPLETA);
            }
            this.ressalva = ressalva;
            this.sugeridaEntradaPropria = sugeridas.entradaPropria();
            this.sugeridaEntradaFgts = sugeridas.entradaFgts();
            this.sugeridoPrazoMeses = sugeridas.prazoMeses();
            this.sugeridaParcela = sugeridas.parcela();
        } else {
            this.ressalva = ressalva;
        }
        this.resultado = resultado;
        this.decididaPor = origem;
        this.decididaEm = agora;
        return true;
    }

    public CondicoesSugeridas sugeridas() {
        return sugeridoPrazoMeses == null ? null
                : new CondicoesSugeridas(sugeridaEntradaPropria, sugeridaEntradaFgts, sugeridoPrazoMeses, sugeridaParcela);
    }

    public Long id() { return id; }
    public Long propostaId() { return propostaId; }
    public int condicoesVersao() { return condicoesVersao; }
    public BigDecimal valorFinanciado() { return valorFinanciado; }
    public int prazoMeses() { return prazoMeses; }
    public BigDecimal parcela() { return parcela; }
    public BigDecimal rendaSomada() { return rendaSomada; }
    public ResultadoDeCredito resultado() { return resultado; }
    public Origem decididaPor() { return decididaPor; }
    public String ressalva() { return ressalva; }
}
