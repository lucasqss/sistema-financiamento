package br.com.financiamento.contratacao.dominio.condicoes;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

/** Condições finais: cópia congelada da proposta aprovada, imutável após a emissão (AGR-0002, invariante 1). */
@Embeddable
public class CondicoesFinais {

    @Column(name = "condicoes_versao", nullable = false, updatable = false)
    private int versao;

    @Column(name = "identificacao_do_bem", nullable = false, updatable = false)
    private String identificacaoDoBem;

    @Column(name = "valor_de_compra", nullable = false, updatable = false)
    private BigDecimal valorDeCompra;

    @Column(name = "valor_de_avaliacao", updatable = false)
    private BigDecimal valorDeAvaliacao;

    @Column(name = "entrada_propria", nullable = false, updatable = false)
    private BigDecimal entradaPropria;

    @Column(name = "entrada_fgts", nullable = false, updatable = false)
    private BigDecimal entradaFgts;

    @Column(name = "valor_financiado", nullable = false, updatable = false)
    private BigDecimal valorFinanciado;

    @Column(name = "prazo_meses", nullable = false, updatable = false)
    private int prazoMeses;

    @Column(nullable = false, updatable = false)
    private BigDecimal parcela;

    protected CondicoesFinais() {
    }

    public CondicoesFinais(int versao, String identificacaoDoBem, BigDecimal valorDeCompra, BigDecimal valorDeAvaliacao,
                           BigDecimal entradaPropria, BigDecimal entradaFgts, BigDecimal valorFinanciado, int prazoMeses,
                           BigDecimal parcela) {
        this.versao = versao;
        this.identificacaoDoBem = identificacaoDoBem;
        this.valorDeCompra = valorDeCompra;
        this.valorDeAvaliacao = valorDeAvaliacao;
        this.entradaPropria = entradaPropria;
        this.entradaFgts = entradaFgts;
        this.valorFinanciado = valorFinanciado;
        this.prazoMeses = prazoMeses;
        this.parcela = parcela;
    }

    public int versao() { return versao; }
    public String identificacaoDoBem() { return identificacaoDoBem; }
    public BigDecimal valorDeCompra() { return valorDeCompra; }
    public BigDecimal valorDeAvaliacao() { return valorDeAvaliacao; }
    public BigDecimal entradaPropria() { return entradaPropria; }
    public BigDecimal entradaFgts() { return entradaFgts; }
    public BigDecimal valorFinanciado() { return valorFinanciado; }
    public int prazoMeses() { return prazoMeses; }
    public BigDecimal parcela() { return parcela; }
}
