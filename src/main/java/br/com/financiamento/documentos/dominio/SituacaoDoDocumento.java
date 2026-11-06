package br.com.financiamento.documentos.dominio;

public enum SituacaoDoDocumento {
    ENVIADO,
    ACEITO,
    RECUSADO,
    /** Um novo envio do mesmo documento tomou o lugar deste. */
    SUBSTITUIDO
}
