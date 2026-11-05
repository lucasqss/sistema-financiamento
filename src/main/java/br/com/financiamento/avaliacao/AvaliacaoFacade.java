package br.com.financiamento.avaliacao;

import br.com.financiamento.avaliacao.aplicacao.SolicitarAvaliacaoService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * API pública do módulo Avaliação (DOM-0003): o valor de avaliação do bem pela
 * fonte do seu tipo. O resultado volta por {@link OuvinteDaAvaliacao}.
 */
@ApplicationScoped
public class AvaliacaoFacade {

    @Inject
    SolicitarAvaliacaoService solicitar;

    /** UC-0012, passo 1. O avaliador é chamado pelo outbox depois do commit. */
    public Long solicitarAvaliacao(SolicitacaoDeAvaliacao solicitacao) {
        return solicitar.executar(solicitacao);
    }
}
