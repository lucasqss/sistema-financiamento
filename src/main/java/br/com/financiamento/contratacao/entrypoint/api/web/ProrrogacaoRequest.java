package br.com.financiamento.contratacao.entrypoint.api.web;

import java.time.LocalDate;

public record ProrrogacaoRequest(LocalDate novaData) {
}
