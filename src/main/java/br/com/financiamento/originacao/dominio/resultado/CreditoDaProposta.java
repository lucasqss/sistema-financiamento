package br.com.financiamento.originacao.dominio.resultado;

import br.com.financiamento.credito.CondicoesSugeridas;
import br.com.financiamento.credito.ResultadoDeCredito;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.math.BigDecimal;

/**
 * O resultado da análise de crédito como chegou à proposta (AGR-0001), com a
 * versão das condições que foi analisada.
 */
@Embeddable
public class CreditoDaProposta {

    @Enumerated(EnumType.STRING)
    @Column(name = "credito_resultado")
    private ResultadoDeCredito resultado;

    @Column(name = "credito_condicoes_versao")
    private Integer condicoesVersao;

    @Column(name = "credito_ressalva")
    private String ressalva;

    @Column(name = "credito_sugerida_entrada_propria")
    private BigDecimal sugeridaEntradaPropria;

    @Column(name = "credito_sugerida_entrada_fgts")
    private BigDecimal sugeridaEntradaFgts;

    @Column(name = "credito_sugerido_prazo_meses")
    private Integer sugeridoPrazoMeses;

    @Column(name = "credito_sugerida_parcela")
    private BigDecimal sugeridaParcela;

    protected CreditoDaProposta() {
    }

    public CreditoDaProposta(ResultadoDeCredito resultado, int condicoesVersao, String ressalva, CondicoesSugeridas sugeridas) {
        this.resultado = resultado;
        this.condicoesVersao = condicoesVersao;
        this.ressalva = ressalva;
        if (sugeridas != null) {
            this.sugeridaEntradaPropria = sugeridas.entradaPropria();
            this.sugeridaEntradaFgts = sugeridas.entradaFgts();
            this.sugeridoPrazoMeses = sugeridas.prazoMeses();
            this.sugeridaParcela = sugeridas.parcela();
        }
    }

    /** As condições da contraproposta aceita já foram aprovadas pelo crédito: valem como aprovação da nova versão. */
    public CreditoDaProposta aprovadoNaVersao(int versao) {
        return new CreditoDaProposta(ResultadoDeCredito.APROVADO, versao, ressalva, null);
    }

    public CondicoesSugeridas sugeridas() {
        return sugeridoPrazoMeses == null ? null
                : new CondicoesSugeridas(sugeridaEntradaPropria, sugeridaEntradaFgts, sugeridoPrazoMeses, sugeridaParcela);
    }

    public ResultadoDeCredito resultado() { return resultado; }
    public int condicoesVersao() { return condicoesVersao; }
    public String ressalva() { return ressalva; }
}
