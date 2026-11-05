package br.com.financiamento.contratacao;

import br.com.financiamento.bem.FormaDeLiberacao;
import br.com.financiamento.bem.TipoDeBem;

import java.math.BigDecimal;

/**
 * O que o Desembolso precisa do contrato: quanto, a quem e como liberar.
 *
 * @param garantiaConstituida RN-0030: a garantia já foi registrada e confirmada?
 */
public record ContratoParaDesembolso(
        Long contratoId,
        Long propostaId,
        TipoDeBem tipoDeBem,
        BigDecimal valorFinanciado,
        BigDecimal entradaFgts,
        String documentoDoVendedor,
        FormaDeLiberacao liberacao,
        boolean garantiaConstituida) {
}
