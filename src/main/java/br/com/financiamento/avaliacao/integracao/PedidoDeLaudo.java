package br.com.financiamento.avaliacao.integracao;

import java.math.BigDecimal;

/** O pedido no formato do sistema externo (camada anticorrupção, ADR 0013). */
record PedidoDeLaudo(String protocolo, String tipoDeBem, String identificacaoDoBem, BigDecimal valorDeCompra, String fonte, String urlDeRetorno) {
}
