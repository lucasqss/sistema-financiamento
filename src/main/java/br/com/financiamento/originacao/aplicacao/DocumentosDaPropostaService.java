package br.com.financiamento.originacao.aplicacao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Os fatos do módulo Documentos na proposta: o envio (RN-0009) e a conferência
 * (UC-0024), que pode levar à pendência ou deixar a decisão seguir (UC-0006).
 */
@ApplicationScoped
public class DocumentosDaPropostaService {

    @Inject
    Propostas propostas;

    @Transactional
    public void recebido(Long propostaId, Long documentoId, String titular, String tipo) {
        var proposta = propostas.buscar(propostaId);
        proposta.documentoRecebido(documentoId, titular, tipo);
        propostas.salvar(proposta);
    }

    @Transactional
    public void conferido(Long propostaId, Long documentoId, boolean aceito, String motivo) {
        var proposta = propostas.buscar(propostaId);
        proposta.documentoConferido(documentoId, aceito, motivo);
        propostas.salvar(proposta, propostas.decidir(proposta));
    }
}
