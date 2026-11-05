package br.com.financiamento.desembolso.entrypoint.modulo;

import br.com.financiamento.avaliacao.MedicaoRealizada;
import br.com.financiamento.avaliacao.OuvinteDaAvaliacao;
import br.com.financiamento.avaliacao.ResultadoDeAvaliacao;
import br.com.financiamento.desembolso.aplicacao.LiberarRecursoService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/** MedicaoRealizada (EVT-0020) dispara a parcela da obra (UC-0023), pela porta da Avaliação (ADR 0009). */
@ApplicationScoped
class OuvinteDaAvaliacaoNoDesembolso implements OuvinteDaAvaliacao {

    @Inject
    LiberarRecursoService liberar;

    /** A avaliação do bem é da proposta, não do desembolso. */
    @Override
    public void bemAvaliado(ResultadoDeAvaliacao resultado) {
    }

    @Override
    public void medicaoRealizada(MedicaoRealizada medicao) {
        liberar.medicaoRealizada(medicao.contratoId(), medicao.percentualAcumulado());
    }
}
