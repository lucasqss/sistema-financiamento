package br.com.financiamento.contratacao.entrypoint.api.web;

import br.com.financiamento.contratacao.aplicacao.AceitarCondicoesService.Conjuge;

import java.time.LocalDate;
import java.util.List;

/** UC-0014, passo 1: o proponente aceita as condições finais da proposta aprovada. */
/** @param previsaoDeEntregaDaObra só no imóvel na planta: a data do cronograma da incorporadora */
public record AceiteRequest(Long propostaId, List<String> segurosOpcionais, List<Conjuge> conjuges,
                            LocalDate previsaoDeEntregaDaObra) {
}
