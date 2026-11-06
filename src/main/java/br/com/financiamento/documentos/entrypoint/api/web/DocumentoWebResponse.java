package br.com.financiamento.documentos.entrypoint.api.web;

import br.com.financiamento.documentos.dominio.Documento;

import java.time.Instant;

public record DocumentoWebResponse(Long id, Long propostaId, String titular, String tipo, String nomeDoArquivo,
                                   String tipoDeConteudo, long tamanho, String sha256, Instant enviadoEm,
                                   String situacao, String motivo, Instant conferidoEm) {

    public static DocumentoWebResponse de(Documento d) {
        return new DocumentoWebResponse(d.id(), d.propostaId(), d.titular(), d.tipo(), d.nomeDoArquivo(),
                d.tipoDeConteudo(), d.tamanho(), d.sha256(), d.enviadoEm(), d.situacao().name(), d.motivoDescrito(),
                d.conferidoEm());
    }
}
