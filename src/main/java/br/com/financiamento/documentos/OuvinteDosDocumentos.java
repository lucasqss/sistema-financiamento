package br.com.financiamento.documentos;

/**
 * Porta de saída dos fatos dos Documentos (ADR 0009). Quem depende deste módulo
 * (a Originação) a implementa; os Documentos chamam todos os ouvintes na mesma
 * transação: se a proposta não aceita o envio ou a conferência, tudo é desfeito.
 */
public interface OuvinteDosDocumentos {

    /**
     * Antes de guardar o arquivo: quem acompanha a proposta confere que ela existe e que quem envia
     * participa dela (ADR 0012). Vem antes de qualquer regra dos Documentos, para quem não participa
     * não saber nada da proposta alheia.
     */
    void envioPedido(Long propostaId);

    void documentoRecebido(DocumentoRecebido documento);

    void documentoConferido(DocumentoConferido documento);
}
