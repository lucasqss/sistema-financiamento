package br.com.financiamento.originacao.entrypoint.api.web;

/** @param titular "bem" ou o CPF/CNPJ do participante */
public record DocumentoRequest(String titular, String tipo) {
}
