package br.com.financiamento.garantia.entrypoint.modulo;

import br.com.financiamento.contratacao.ContratoAssinado;
import br.com.financiamento.contratacao.OuvinteDoContrato;
import br.com.financiamento.garantia.aplicacao.SolicitarRegistroService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/** ContratoAssinado (EVT-0024) dispara o registro da garantia (UC-0018), pela porta do contrato (ADR 0009). */
@ApplicationScoped
class OuvinteDoContratoNaGarantia implements OuvinteDoContrato {

    @Inject
    SolicitarRegistroService solicitar;

    @Override
    public void contratoAssinado(ContratoAssinado contrato) {
        solicitar.executar(contrato);
    }
}
