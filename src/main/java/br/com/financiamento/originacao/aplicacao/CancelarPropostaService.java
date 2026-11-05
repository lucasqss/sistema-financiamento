package br.com.financiamento.originacao.aplicacao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/** UC-0010: cancelar proposta. */
@ApplicationScoped
public class CancelarPropostaService {

    @Inject
    Propostas propostas;

    @Transactional
    public DetalheProposta executar(Long propostaId) {
        var proposta = propostas.buscar(propostaId);
        proposta.cancelar();
        return propostas.salvar(proposta);
    }
}
