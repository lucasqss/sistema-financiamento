package br.com.financiamento.contratacao.aplicacao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * UC-0016: desistência antes de assinar. O cancelamento por registro recusado
 * sem solução vem com o módulo Garantia.
 */
@ApplicationScoped
public class CancelarContratoService {

    @Inject
    Contratacoes contratacoes;

    @Transactional
    public DetalheDoContrato executar(Long contratoId, String motivo) {
        var contrato = contratacoes.contrato(contratoId);
        contrato.cancelar(motivo == null || motivo.isBlank() ? "desistência" : motivo);
        contratacoes.salvar(contrato);
        return DetalheDoContrato.de(contrato);
    }
}
