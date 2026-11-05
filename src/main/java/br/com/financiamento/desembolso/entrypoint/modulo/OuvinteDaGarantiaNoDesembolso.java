package br.com.financiamento.desembolso.entrypoint.modulo;

import br.com.financiamento.desembolso.aplicacao.LiberarRecursoService;
import br.com.financiamento.garantia.GarantiaConstituida;
import br.com.financiamento.garantia.OuvinteDaGarantia;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/** GarantiaRegistrada (EVT-0028) dispara a liberação (UC-0021), pela porta da Garantia (ADR 0009). */
@ApplicationScoped
class OuvinteDaGarantiaNoDesembolso implements OuvinteDaGarantia {

    @Inject
    LiberarRecursoService liberar;

    @Override
    public void garantiaConstituida(GarantiaConstituida garantia) {
        liberar.garantiaConstituida(garantia.contratoId());
    }
}
