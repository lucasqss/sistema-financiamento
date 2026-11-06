package br.com.financiamento.documentos.entrypoint.api.web;

/** Os campos do envio multipart de um documento, para a validação de atributos. */
record EnvioDeDocumentoRequest(Long propostaId, String titular, String tipo, String nomeDoArquivo,
                               String tipoDeConteudo, Long tamanho) {
}
