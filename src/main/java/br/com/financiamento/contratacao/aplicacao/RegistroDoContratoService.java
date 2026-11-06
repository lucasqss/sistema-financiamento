package br.com.financiamento.contratacao.aplicacao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/** Os fatos do registro da garantia que movem o contrato (CV-0002), vindos da Garantia. */
@ApplicationScoped
public class RegistroDoContratoService {

    @Inject
    Contratacoes contratacoes;

    @Transactional
    public void registroSolicitado(Long contratoId, String orgao, String protocolo) {
        var c = contratacoes.contrato(contratoId, Contratacoes.aceita("RegistroSolicitado"));
        c.registroSolicitado(orgao, protocolo);
        contratacoes.salvar(c);
    }

    @Transactional
    public void exigenciaEmitida(Long contratoId, String exigencia) {
        var c = contratacoes.contrato(contratoId, Contratacoes.aceita("ExigenciaEmitida"));
        c.exigenciaEmitida(exigencia);
        contratacoes.salvar(c);
    }

    @Transactional
    public void exigenciaCumprida(Long contratoId, String protocolo) {
        var c = contratacoes.contrato(contratoId, Contratacoes.aceita("ExigenciaCumprida"));
        c.exigenciaCumprida(protocolo);
        contratacoes.salvar(c);
    }

    @Transactional
    public void registroRecusado(Long contratoId, String motivo) {
        var c = contratacoes.contrato(contratoId, Contratacoes.aceita("RegistroRecusado"));
        c.registroRecusado(motivo);
        contratacoes.salvar(c);
    }

    @Transactional
    public void garantiaRegistrada(Long contratoId, String protocolo) {
        var c = contratacoes.contrato(contratoId, Contratacoes.aceita("GarantiaRegistrada"));
        c.garantiaRegistrada(protocolo);
        contratacoes.salvar(c);
    }

    @Transactional
    public void cancelarPorRegistroRecusado(Long contratoId, String motivo) {
        var c = contratacoes.contrato(contratoId, Contratacoes.aceita("ContratoCancelado"));
        c.cancelar("registro recusado sem solução: " + motivo);
        contratacoes.salvar(c);
    }
}
