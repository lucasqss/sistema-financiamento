package br.com.financiamento.contratacao;

import br.com.financiamento.contratacao.aplicacao.LiberacaoDoContratoService;
import br.com.financiamento.contratacao.aplicacao.RegistroDoContratoService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * API pública da Contratação (DOM-0004). A Garantia move o contrato pelo
 * registro (CV-0002); os fatos do contrato chegam a ela por {@link OuvinteDoContrato}.
 */
@ApplicationScoped
public class ContratacaoFacade {

    @Inject
    RegistroDoContratoService registro;

    @Inject
    LiberacaoDoContratoService liberacao;

    /** O que o Desembolso precisa para liberar (UC-0021, UC-0023). */
    public ContratoParaDesembolso paraDesembolso(Long contratoId) {
        return liberacao.paraDesembolso(contratoId);
    }

    /** Quem participa do contrato: signatários, vendedor e quem participou da proposta (ADR 0012). */
    public java.util.List<String> documentosEnvolvidos(Long contratoId) {
        return liberacao.documentosEnvolvidos(contratoId);
    }

    /** EVT-0033: o contrato do bem pronto fica formalizado. */
    public void recursoLiberado(Long contratoId) {
        liberacao.recursoLiberado(contratoId);
    }

    /** EVT-0034: a última parcela formaliza o contrato; as outras o mantêm em obra. */
    public void parcelaDaObraLiberada(Long contratoId, boolean ultima) {
        liberacao.parcelaDaObraLiberada(contratoId, ultima);
    }

    /** EVT-0020 no contrato em obra. */
    public void medicaoRealizada(Long contratoId) {
        liberacao.medicaoRealizada(contratoId);
    }

    public void registroSolicitado(Long contratoId, String orgao, String protocolo) {
        registro.registroSolicitado(contratoId, orgao, protocolo);
    }

    public void exigenciaEmitida(Long contratoId, String exigencia) {
        registro.exigenciaEmitida(contratoId, exigencia);
    }

    public void exigenciaCumprida(Long contratoId, String protocolo) {
        registro.exigenciaCumprida(contratoId, protocolo);
    }

    public void registroRecusado(Long contratoId, String motivo) {
        registro.registroRecusado(contratoId, motivo);
    }

    public void garantiaRegistrada(Long contratoId, String protocolo) {
        registro.garantiaRegistrada(contratoId, protocolo);
    }

    /** UC-0016 pelo analista: registro recusado sem solução desfaz o contrato. */
    public void cancelarPorRegistroRecusado(Long contratoId, String motivo) {
        registro.cancelarPorRegistroRecusado(contratoId, motivo);
    }
}
