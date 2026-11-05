package br.com.financiamento.contratacao;

/**
 * Porta de saída dos fatos do contrato (ADR 0009). Quem depende da Contratação
 * (a Garantia) a implementa; a Contratação chama todos os ouvintes na mesma
 * transação, sem conhecer quem são.
 */
public interface OuvinteDoContrato {

    /** ContratoAssinado dispara o registro da garantia (UC-0018). */
    void contratoAssinado(ContratoAssinado contrato);
}
