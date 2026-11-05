package br.com.financiamento.originacao.aplicacao;

import br.com.financiamento.originacao.dominio.Enquadrador;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * UC-0005: submeter proposta. Aplica RN-0018; fora do enquadramento, a proposta
 * continua em rascunho e a recusa traz a lista do que falta.
 */
@ApplicationScoped
public class SubmeterPropostaService {

    @Inject
    Propostas propostas;

    @Inject
    Enquadrador enquadrador;

    @Transactional
    public DetalheProposta executar(Long propostaId) {
        var proposta = propostas.buscar(propostaId);
        var enquadramento = enquadrador.enquadrar(proposta);
        proposta.submeter(enquadramento);
        return propostas.salvar(proposta, enquadramento);
    }
}
