package br.com.financiamento.contratacao.entrypoint.api.web;

import br.com.financiamento.shared.validacao.atributo.CadeiaDeAtributos;
import jakarta.enterprise.context.ApplicationScoped;

import static br.com.financiamento.shared.validacao.atributo.ElosDeAtributo.*;

/**
 * Validação de atributos de {@link ProrrogacaoRequest} (UC-0017), em cadeia (ADR 0011): forma e formato
 * de cada campo, com todas as falhas de uma vez. As regras de negócio ficam na cadeia de domínio.
 */
@ApplicationScoped
class ProrrogacaoRequestValidador {

    private static final CadeiaDeAtributos<ProrrogacaoRequest> CADEIA = CadeiaDeAtributos.de(
            obrigatorio("novaData", ProrrogacaoRequest::novaData));

    void validar(ProrrogacaoRequest requisicao) {
        CADEIA.validar(requisicao);
    }
}
