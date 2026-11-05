package br.com.financiamento.garantia;

/** O órgão confirmou o registro do bem do contrato (EVT-0028), entregue a quem acompanha (ADR 0009). */
public record GarantiaConstituida(Long contratoId, Long propostaId, String protocolo) {
}
