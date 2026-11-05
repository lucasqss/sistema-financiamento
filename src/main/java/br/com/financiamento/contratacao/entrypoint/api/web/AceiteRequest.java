package br.com.financiamento.contratacao.entrypoint.api.web;

import br.com.financiamento.contratacao.aplicacao.AceitarCondicoesService.Conjuge;

import java.util.List;

/** UC-0014, passo 1: o proponente aceita as condições finais da proposta aprovada. */
public record AceiteRequest(Long propostaId, List<String> segurosOpcionais, List<Conjuge> conjuges) {
}
