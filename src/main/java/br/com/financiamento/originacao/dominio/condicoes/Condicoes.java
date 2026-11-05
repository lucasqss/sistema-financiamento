package br.com.financiamento.originacao.dominio.condicoes;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

/**
 * Condições do financiamento (GLO-0028), versionadas: cada alteração ou
 * contraproposta é uma nova versão. A parcela é dado da simulação: o cálculo
 * financeiro está fora do escopo do modelo.
 */
@Embeddable
public class Condicoes {

    @Column(name = "entrada_propria")
    private BigDecimal entradaPropria;

    /** Recurso do FGTS na entrada (GLO-0022, RN-0015). */
    @Column(name = "entrada_fgts")
    private BigDecimal entradaFgts;

    @Column(name = "prazo_meses")
    private Integer prazoMeses;

    private BigDecimal parcela;

    @Column(name = "condicoes_versao")
    private Integer versao;

    protected Condicoes() {
    }

    public Condicoes(BigDecimal entradaPropria, BigDecimal entradaFgts, int prazoMeses, BigDecimal parcela, int versao) {
        this.entradaPropria = entradaPropria == null ? BigDecimal.ZERO : entradaPropria;
        this.entradaFgts = entradaFgts == null ? BigDecimal.ZERO : entradaFgts;
        this.prazoMeses = prazoMeses;
        this.parcela = parcela;
        this.versao = versao;
    }

    /** Entrada (GLO-0021): recurso próprio mais FGTS. */
    public BigDecimal entrada() {
        return entradaPropria.add(entradaFgts);
    }

    /** RN-0005: valor financiado = valor de compra − entrada. */
    public BigDecimal valorFinanciado(BigDecimal valorDeCompra) {
        return valorDeCompra.subtract(entrada());
    }

    public BigDecimal entradaPropria() { return entradaPropria; }
    public BigDecimal entradaFgts() { return entradaFgts; }
    public int prazoMeses() { return prazoMeses; }
    public BigDecimal parcela() { return parcela; }
    public int versao() { return versao; }
}
