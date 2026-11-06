package br.com.financiamento.garantia.entrypoint.api.web;

import br.com.financiamento.shared.validacao.atributo.CadeiaDeAtributos;
import jakarta.enterprise.context.ApplicationScoped;

import static br.com.financiamento.shared.validacao.atributo.ElosDeAtributo.*;

/**
 * Validação de atributos de {@link DesfazimentoRequest} (UC-0019), em cadeia (ADR 0011): forma e formato
 * de cada campo, com todas as falhas de uma vez. As regras de negócio ficam na cadeia de domínio.
 */
@ApplicationScoped
class DesfazimentoRequestValidador {

    private static final CadeiaDeAtributos<DesfazimentoRequest> CADEIA = CadeiaDeAtributos.de(
            tamanhoMaximo("motivo", 300, DesfazimentoRequest::motivo));

    void validar(DesfazimentoRequest requisicao) {
        if (requisicao != null) CADEIA.validar(requisicao); // corpo opcional
    }
}
