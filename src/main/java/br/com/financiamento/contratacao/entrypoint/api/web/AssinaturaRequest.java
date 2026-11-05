package br.com.financiamento.contratacao.entrypoint.api.web;

/** @param documento CPF ou CNPJ de quem assina */
public record AssinaturaRequest(String documento) {
}
