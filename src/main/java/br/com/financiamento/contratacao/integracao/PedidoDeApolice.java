package br.com.financiamento.contratacao.integracao;

/** O pedido no formato do sistema externo (camada anticorrupção, ADR 0013). */
record PedidoDeApolice(String contratacao, Long propostaId, String seguro, String urlDeRetorno) {
}
