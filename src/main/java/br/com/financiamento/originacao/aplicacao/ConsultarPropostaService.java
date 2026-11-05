package br.com.financiamento.originacao.aplicacao;

import br.com.financiamento.originacao.dominio.Enquadrador;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/** Acompanhar a proposta, com o enquadramento atual. */
@ApplicationScoped
public class ConsultarPropostaService {

    @Inject
    Propostas propostas;

    @Inject
    Enquadrador enquadrador;

    @Transactional
    public DetalheProposta executar(Long propostaId) {
        var proposta = propostas.buscar(propostaId);
        return DetalheProposta.de(proposta, enquadrador.enquadrar(proposta));
    }
}
