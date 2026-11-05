package br.com.financiamento.credito.entrypoint.retorno;

import br.com.financiamento.credito.CondicoesSugeridas;
import br.com.financiamento.credito.ResultadoDeCredito;

public record RetornoDoBureauRequest(ResultadoDeCredito resultado, String ressalva, CondicoesSugeridas sugeridas) {
}
