package br.com.financiamento.shared.web;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

/** O mapeamento para o catálogo corporativo fica num arquivo só; sem linha, o código corporativo vem vazio. */
class CatalogoCorporativoTest {

    @Test
    void semMapeamentoOCodigoCorporativoEVazio() {
        assertNull(new CatalogoCorporativo().codigoCorporativo("ORI-0001"));
    }
}
