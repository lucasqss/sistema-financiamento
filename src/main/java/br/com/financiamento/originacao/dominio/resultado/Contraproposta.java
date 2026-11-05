package br.com.financiamento.originacao.dominio.resultado;

import br.com.financiamento.credito.CondicoesSugeridas;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Contraproposta (GLO-0037): as condições com que o crédito aprova, com validade (RN-0020). */
@Embeddable
public class Contraproposta {

    @Column(name = "contraproposta_entrada_propria")
    private BigDecimal entradaPropria;

    @Column(name = "contraproposta_entrada_fgts")
    private BigDecimal entradaFgts;

    @Column(name = "contraproposta_prazo_meses")
    private Integer prazoMeses;

    @Column(name = "contraproposta_parcela")
    private BigDecimal parcela;

    @Column(name = "contraproposta_ressalva")
    private String ressalva;

    @Column(name = "contraproposta_valida_ate")
    private LocalDate validaAte;

    protected Contraproposta() {
    }

    public Contraproposta(CondicoesSugeridas c, String ressalva, LocalDate validaAte) {
        this.entradaPropria = c.entradaPropria();
        this.entradaFgts = c.entradaFgts();
        this.prazoMeses = c.prazoMeses();
        this.parcela = c.parcela();
        this.ressalva = ressalva;
        this.validaAte = validaAte;
    }

    public boolean vencidaEm(LocalDate hoje) {
        return validaAte.isBefore(hoje);
    }

    public BigDecimal entradaPropria() { return entradaPropria; }
    public BigDecimal entradaFgts() { return entradaFgts; }
    public int prazoMeses() { return prazoMeses; }
    public BigDecimal parcela() { return parcela; }
    public String ressalva() { return ressalva; }
    public LocalDate validaAte() { return validaAte; }
}
