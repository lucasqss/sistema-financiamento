package br.com.financiamento.garantia.integracao;

/** O pedido de registro da garantia, no formato comum ao cartório e ao órgão de trânsito. */
record PedidoDeRegistro(String protocolo, String identificacaoDoBem, int tentativa, String urlDeRetorno) {
}
