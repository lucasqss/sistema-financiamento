package br.com.financiamento.originacao.aplicacao;

import br.com.financiamento.originacao.dominio.Enquadrador;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * UC-0008: resolver pendência. Depois de o ator agir (reenviar documento,
 * recompor a entrada), a proposta é reenquadrada e volta à análise. Condições
 * novas pedem nova análise de crédito; avaliação vencida pede nova avaliação.
 */
@ApplicationScoped
public class ResolverPendenciaService {

    @Inject
    Propostas propostas;

    @Inject
    Enquadrador enquadrador;

    @Transactional
    public DetalheProposta executar(Long propostaId) {
        var proposta = propostas.buscar(propostaId);
        proposta.resolverPendencia(enquadrador.enquadrar(proposta));
        propostas.solicitarAnalises(proposta);
        return propostas.salvar(proposta, propostas.decidir(proposta));
    }
}
