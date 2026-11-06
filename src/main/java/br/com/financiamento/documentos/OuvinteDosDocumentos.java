package br.com.financiamento.documentos;

/**
 * Porta de saída dos fatos dos Documentos (ADR 0009). Quem depende deste módulo
 * (a Originação) a implementa; os Documentos chamam todos os ouvintes na mesma
 * transação: se a proposta não aceita o envio ou a conferência, tudo é desfeito.
 */
public interface OuvinteDosDocumentos {

    void documentoRecebido(DocumentoRecebido documento);

    void documentoConferido(DocumentoConferido documento);
}
