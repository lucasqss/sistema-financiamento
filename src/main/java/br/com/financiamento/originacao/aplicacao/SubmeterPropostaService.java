package br.com.financiamento.originacao.aplicacao;

import br.com.financiamento.originacao.dominio.Enquadrador;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * UC-0005: submeter proposta. Aplica RN-0018; fora do enquadramento, a proposta
 * continua em rascunho e a recusa traz a lista do que falta. Submetida, pede a
 * análise de crédito e a avaliação do bem.
 */
@ApplicationScoped
public class SubmeterPropostaService {

    @Inject
    ElosDaProposta elos;

    @Inject
    Propostas propostas;

    @Inject
    Enquadrador enquadrador;

    @Transactional
    public DetalheProposta executar(Long propostaId) {
        var proposta = elos.validar(propostaId, elos.aceita("PropostaSubmetida"));
        var enquadramento = enquadrador.enquadrar(proposta);
        proposta.submeter(enquadramento);
        propostas.solicitarAnalises(proposta);
        return propostas.salvar(proposta, enquadramento);
    }
}
