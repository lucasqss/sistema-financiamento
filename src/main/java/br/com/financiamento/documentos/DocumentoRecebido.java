package br.com.financiamento.documentos;

/** Um arquivo foi enviado para a proposta, entregue a quem acompanha os documentos (ADR 0009). */
public record DocumentoRecebido(Long documentoId, Long propostaId, String titular, String tipo) {
}
