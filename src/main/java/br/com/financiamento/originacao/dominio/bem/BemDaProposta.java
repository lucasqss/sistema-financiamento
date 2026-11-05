package br.com.financiamento.originacao.dominio.bem;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

/**
 * O bem financiado (GLO-0006). O tipo de bem fica na proposta, fixado ao
 * iniciar; aqui ficam a identificação, os valores e o vendedor.
 */
@Embeddable
public class BemDaProposta {

    /** Identificação do bem (GLO-0012): chassi, renavam, matrícula. */
    @Column(name = "bem_identificacao")
    private String identificacao;

    /** Valor de compra (GLO-0013). */
    @Column(name = "bem_valor_compra")
    private BigDecimal valorDeCompra;

    /** Valor de avaliação (GLO-0014), quando a Avaliação já o informou. */
    @Column(name = "bem_valor_avaliacao")
    private BigDecimal valorDeAvaliacao;

    /** Vendedor por identificador (CPF ou CNPJ), nunca a entidade de outro módulo. */
    @Column(name = "vendedor_documento")
    private String documentoDoVendedor;

    /** Só veículo (GLO-0016). */
    @Column(name = "bem_ano_modelo")
    private Integer anoModelo;

    /** Só terreno (RN-0014). */
    @Column(name = "bem_construcao_averbada")
    private Boolean construcaoAverbada;

    protected BemDaProposta() {
    }

    public BemDaProposta(String identificacao, BigDecimal valorDeCompra, String documentoDoVendedor,
                         Integer anoModelo, Boolean construcaoAverbada) {
        this.identificacao = identificacao == null ? null : identificacao.trim();
        this.valorDeCompra = valorDeCompra;
        this.documentoDoVendedor = documentoDoVendedor == null ? null : documentoDoVendedor.replaceAll("\\D", "");
        this.anoModelo = anoModelo;
        this.construcaoAverbada = construcaoAverbada;
    }

    /** Valor de referência (GLO-0015): o menor entre compra e avaliação. */
    public BigDecimal valorDeReferencia() {
        if (valorDeAvaliacao == null) return valorDeCompra;
        return valorDeCompra.min(valorDeAvaliacao);
    }

    public BemDaProposta comAvaliacao(BigDecimal valor) {
        var b = new BemDaProposta(identificacao, valorDeCompra, documentoDoVendedor, anoModelo, construcaoAverbada);
        b.valorDeAvaliacao = valor;
        return b;
    }

    public String identificacao() { return identificacao; }
    public BigDecimal valorDeCompra() { return valorDeCompra; }
    public BigDecimal valorDeAvaliacao() { return valorDeAvaliacao; }
    public String documentoDoVendedor() { return documentoDoVendedor; }
    public Integer anoModelo() { return anoModelo; }
    public Boolean construcaoAverbada() { return construcaoAverbada; }
}
