package br.com.financiamento.desembolso.integracao;

import java.math.BigDecimal;

/** O pedido no formato do sistema externo (camada anticorrupção, ADR 0013). */
record PedidoDeTransferencia(String protocolo, BigDecimal valor, String favorecido, String urlDeRetorno) {
}
