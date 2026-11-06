package br.com.financiamento.originacao.entrypoint.modulo;

import br.com.financiamento.documentos.DocumentoConferido;
import br.com.financiamento.documentos.DocumentoRecebido;
import br.com.financiamento.documentos.OuvinteDosDocumentos;
import br.com.financiamento.originacao.aplicacao.DocumentosDaPropostaService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/** Entrada dos fatos dos Documentos na Originação (ADR 0009): envio e conferência. */
@ApplicationScoped
class OuvinteDosDocumentosNaOriginacao implements OuvinteDosDocumentos {

    @Inject
    DocumentosDaPropostaService documentos;

    @Override
    public void envioPedido(Long propostaId) {
        documentos.envioPedido(propostaId);
    }

    @Override
    public void documentoRecebido(DocumentoRecebido d) {
        documentos.recebido(d.propostaId(), d.documentoId(), d.titular(), d.tipo());
    }

    @Override
    public void documentoConferido(DocumentoConferido d) {
        documentos.conferido(d.propostaId(), d.documentoId(), d.aceito(), d.motivo());
    }
}
