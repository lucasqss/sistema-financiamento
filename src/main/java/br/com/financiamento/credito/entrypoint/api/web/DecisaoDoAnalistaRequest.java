package br.com.financiamento.credito.entrypoint.api.web;

import br.com.financiamento.credito.CondicoesSugeridas;
import br.com.financiamento.credito.ResultadoDeCredito;

/** Decisão do analista nos casos fora da alçada automática (UC-0011, fluxo alternativo). */
public record DecisaoDoAnalistaRequest(ResultadoDeCredito resultado, String ressalva, CondicoesSugeridas sugeridas) {
}
