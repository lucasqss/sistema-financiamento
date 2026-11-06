package br.com.financiamento.originacao.aplicacao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/** UC-0009: responder contraproposta. */
@ApplicationScoped
public class ResponderContrapropostaService {

    @Inject
    ElosDaProposta elos;

    @Inject
    Propostas propostas;

    /** Aceita: as novas condições são reenquadradas e a proposta segue para a decisão. */
    @Transactional
    public DetalheProposta aceitar(Long propostaId) {
        var proposta = elos.validar(propostaId, elos.aceita("ContrapropostaAceita"));
        proposta.aceitarContraproposta();
        propostas.solicitarAnalises(proposta);
        return propostas.salvar(proposta, propostas.decidir(proposta));
    }

    /** Recusa: a proposta é reprovada. */
    @Transactional
    public DetalheProposta recusar(Long propostaId) {
        var proposta = elos.validar(propostaId, elos.aceita("ContrapropostaRecusada"));
        proposta.recusarContraproposta(false);
        return propostas.salvar(proposta);
    }
}
