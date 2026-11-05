package br.com.financiamento.avaliacao;

import br.com.financiamento.bem.TipoDeBem;

import java.math.BigDecimal;

/** O que a Originação manda à Avaliação na submissão (UC-0012). */
public record SolicitacaoDeAvaliacao(Long propostaId, TipoDeBem tipoDeBem, String identificacaoDoBem,
                                     BigDecimal valorDeCompra) {
}
