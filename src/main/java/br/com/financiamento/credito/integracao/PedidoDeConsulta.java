package br.com.financiamento.credito.integracao;

import java.math.BigDecimal;
import java.util.List;

/** O pedido no formato do sistema externo (camada anticorrupção, ADR 0013). */
record PedidoDeConsulta(String protocolo, List<String> documentos, BigDecimal valorFinanciado, int prazoMeses, BigDecimal parcela, String urlDeRetorno) {
}
