package br.com.financiamento.credito;

import br.com.financiamento.credito.aplicacao.SolicitarAnaliseService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * API pública do módulo Crédito (DOM-0002): análise de crédito dos
 * participantes da proposta. A decisão volta por {@link OuvinteDoCredito}.
 */
@ApplicationScoped
public class CreditoFacade {

    @Inject
    SolicitarAnaliseService solicitar;

    /** UC-0011, passo 1: pede a análise; o bureau é chamado pelo outbox depois do commit. */
    public Long solicitarAnalise(SolicitacaoDeAnalise solicitacao) {
        return solicitar.executar(solicitacao);
    }
}
