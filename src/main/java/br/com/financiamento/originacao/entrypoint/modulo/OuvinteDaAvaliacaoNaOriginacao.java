package br.com.financiamento.originacao.entrypoint.modulo;

import br.com.financiamento.avaliacao.OuvinteDaAvaliacao;
import br.com.financiamento.avaliacao.ResultadoDeAvaliacao;
import br.com.financiamento.originacao.aplicacao.DecidirPropostaService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/** Entrada dos resultados da Avaliação na Originação (ADR 0009). */
@ApplicationScoped
class OuvinteDaAvaliacaoNaOriginacao implements OuvinteDaAvaliacao {

    @Inject
    DecidirPropostaService decidir;

    @Override
    public void bemAvaliado(ResultadoDeAvaliacao resultado) {
        decidir.bemAvaliado(resultado);
    }
}
