package br.com.financiamento.originacao.entrypoint.api.web;

import br.com.financiamento.originacao.aplicacao.SimularCondicoesService.NovasCondicoes;

import java.math.BigDecimal;

/** @param parcela parcela da simulação financeira, que é externa ao modelo */
public record CondicoesRequest(BigDecimal entradaPropria, BigDecimal entradaFgts, int prazoMeses, BigDecimal parcela) {

    NovasCondicoes comando() {
        return new NovasCondicoes(entradaPropria, entradaFgts, prazoMeses, parcela);
    }
}
