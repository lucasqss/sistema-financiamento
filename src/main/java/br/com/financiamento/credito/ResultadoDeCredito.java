package br.com.financiamento.credito;

/** A decisão do crédito (EVT-0013, EVT-0014, EVT-0015). */
public enum ResultadoDeCredito {
    APROVADO("CreditoAprovado"),
    APROVADO_COM_RESSALVA("CreditoAprovadoComRessalva"),
    REPROVADO("CreditoReprovado");

    private final String evento;

    ResultadoDeCredito(String evento) {
        this.evento = evento;
    }

    /** O evento de domínio que a decisão emite. */
    public String evento() {
        return evento;
    }
}
