package br.com.financiamento.contratacao.entrypoint.api.web;

import br.com.financiamento.shared.validacao.atributo.CadeiaDeAtributos;
import jakarta.enterprise.context.ApplicationScoped;

import static br.com.financiamento.shared.validacao.atributo.ElosDeAtributo.*;

/**
 * Validação de atributos de {@link AssinaturaRequest} (UC-0015), em cadeia (ADR 0011): forma e formato
 * de cada campo, com todas as falhas de uma vez. As regras de negócio ficam na cadeia de domínio.
 */
@ApplicationScoped
class AssinaturaRequestValidador {

    private static final CadeiaDeAtributos<AssinaturaRequest> CADEIA = CadeiaDeAtributos.de(
            obrigatorio("documento", AssinaturaRequest::documento),
            cpfOuCnpj("documento", AssinaturaRequest::documento));

    void validar(AssinaturaRequest requisicao) {
        CADEIA.validar(requisicao);
    }
}
