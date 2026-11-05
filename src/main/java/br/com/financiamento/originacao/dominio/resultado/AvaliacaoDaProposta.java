package br.com.financiamento.originacao.dominio.resultado;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;
import java.time.LocalDate;

/** A avaliação do bem como chegou à proposta (AGR-0001): valor, fonte, data e validade. */
@Embeddable
public class AvaliacaoDaProposta {

    @Column(name = "avaliacao_aceito")
    private Boolean aceitoComoGarantia;

    @Column(name = "avaliacao_valor")
    private BigDecimal valor;

    @Column(name = "avaliacao_fonte")
    private String fonte;

    @Column(name = "avaliacao_data")
    private LocalDate avaliadaEm;

    @Column(name = "avaliacao_valida_ate")
    private LocalDate validaAte;

    protected AvaliacaoDaProposta() {
    }

    public AvaliacaoDaProposta(boolean aceitoComoGarantia, BigDecimal valor, String fonte, LocalDate avaliadaEm,
                               LocalDate validaAte) {
        this.aceitoComoGarantia = aceitoComoGarantia;
        this.valor = valor;
        this.fonte = fonte;
        this.avaliadaEm = avaliadaEm;
        this.validaAte = validaAte;
    }

    /** RN-0022 e GLO-0020: passou da validade do seu tipo de bem. */
    public boolean vencidaEm(LocalDate hoje) {
        return validaAte != null && validaAte.isBefore(hoje);
    }

    public boolean aceitoComoGarantia() { return Boolean.TRUE.equals(aceitoComoGarantia); }
    public BigDecimal valor() { return valor; }
    public String fonte() { return fonte; }
    public LocalDate avaliadaEm() { return avaliadaEm; }
    public LocalDate validaAte() { return validaAte; }
}
