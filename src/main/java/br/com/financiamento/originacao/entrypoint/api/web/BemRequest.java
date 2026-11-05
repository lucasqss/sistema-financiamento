package br.com.financiamento.originacao.entrypoint.api.web;

import br.com.financiamento.originacao.aplicacao.InformarBemService.NovoBem;

import java.math.BigDecimal;

public record BemRequest(String identificacao, BigDecimal valorDeCompra, String documentoDoVendedor,
                         Integer anoModelo, Boolean construcaoAverbada) {

    NovoBem comando() {
        return new NovoBem(identificacao, valorDeCompra, documentoDoVendedor, anoModelo, construcaoAverbada);
    }
}
