package br.com.financiamento.originacao.entrypoint.modulo;

import br.com.financiamento.credito.DecisaoDeCredito;
import br.com.financiamento.credito.OuvinteDoCredito;
import br.com.financiamento.originacao.aplicacao.DecidirPropostaService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/** Entrada das decisões do Crédito na Originação (ADR 0009). */
@ApplicationScoped
class OuvinteDoCreditoNaOriginacao implements OuvinteDoCredito {

    @Inject
    DecidirPropostaService decidir;

    @Override
    public void creditoDecidido(DecisaoDeCredito decisao) {
        decidir.creditoDecidido(decisao);
    }
}
