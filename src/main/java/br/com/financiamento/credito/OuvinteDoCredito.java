package br.com.financiamento.credito;

/**
 * Porta de saída das decisões do Crédito (ADR 0009). Quem depende do Crédito
 * (a Originação) a implementa; o Crédito chama todos os ouvintes na mesma
 * transação em que registra a decisão, sem conhecer quem são.
 */
public interface OuvinteDoCredito {

    void creditoDecidido(DecisaoDeCredito decisao);
}
