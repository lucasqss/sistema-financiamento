package br.com.financiamento.contratacao;

import br.com.financiamento.bem.TipoDeBem;

/** O contrato recebeu todas as assinaturas exigidas (EVT-0024), entregue a quem acompanha (ADR 0009). */
public record ContratoAssinado(Long contratoId, Long propostaId, TipoDeBem tipoDeBem, String identificacaoDoBem) {
}
