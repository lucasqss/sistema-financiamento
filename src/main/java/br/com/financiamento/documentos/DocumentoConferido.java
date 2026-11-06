package br.com.financiamento.documentos;

/** A conferência de um documento (EVT-0039 aceito, EVT-0038 recusado), entregue a quem acompanha (ADR 0009). */
public record DocumentoConferido(Long documentoId, Long propostaId, String titular, String tipo, boolean aceito,
                                 String motivo) {
}
