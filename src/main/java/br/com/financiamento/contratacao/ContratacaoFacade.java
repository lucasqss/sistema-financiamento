package br.com.financiamento.contratacao;

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
