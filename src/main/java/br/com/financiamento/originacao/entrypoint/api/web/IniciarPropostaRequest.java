package br.com.financiamento.originacao.entrypoint.api.web;

/** @param tipoDeBem código do tipo de bem, ex.: CARRO_USADO, RESIDENCIAL_NA_PLANTA */
public record IniciarPropostaRequest(String tipoDeBem) {
}
