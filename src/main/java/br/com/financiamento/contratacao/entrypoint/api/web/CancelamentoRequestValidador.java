package br.com.financiamento.contratacao.entrypoint.api.web;

import br.com.financiamento.shared.validacao.atributo.CadeiaDeAtributos;
import jakarta.enterprise.context.ApplicationScoped;

import static br.com.financiamento.shared.validacao.atributo.ElosDeAtributo.*;

/**
 * Validação de atributos de {@link CancelamentoRequest} (UC-0016), em cadeia (ADR 0011): forma e formato
 * de cada campo, com todas as falhas de uma vez. As regras de negócio ficam na cadeia de domínio.
 */
@ApplicationScoped
class CancelamentoRequestValidador {

    private static final CadeiaDeAtributos<CancelamentoRequest> CADEIA = CadeiaDeAtributos.de(
            tamanhoMaximo("motivo", 300, CancelamentoRequest::motivo));

    void validar(CancelamentoRequest requisicao) {
        if (requisicao != null) CADEIA.validar(requisicao); // corpo opcional
    }
}
