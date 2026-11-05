package br.com.financiamento.garantia;

/**
 * Porta de saída dos fatos da Garantia (ADR 0009). Quem depende da Garantia (o
 * Desembolso) a implementa; a Garantia chama todos os ouvintes na mesma
 * transação, sem conhecer quem são.
 */
public interface OuvinteDaGarantia {

    /** GarantiaRegistrada dispara a liberação (UC-0021), que só existe com a garantia confirmada (RN-0030). */
    void garantiaConstituida(GarantiaConstituida garantia);
}
