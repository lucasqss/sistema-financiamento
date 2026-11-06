package br.com.financiamento.desembolso.entrypoint.retorno;

import br.com.financiamento.shared.validacao.atributo.CadeiaDeAtributos;
import jakarta.enterprise.context.ApplicationScoped;

import static br.com.financiamento.shared.validacao.atributo.ElosDeAtributo.*;

/**
 * Validação de atributos de {@link RetornoDoFgtsRequest} (UC-0021), em cadeia (ADR 0011): forma e formato
 * de cada campo, com todas as falhas de uma vez. As regras de negócio ficam na cadeia de domínio.
 */
@ApplicationScoped
class RetornoDoFgtsRequestValidador {

    private static final CadeiaDeAtributos<RetornoDoFgtsRequest> CADEIA = CadeiaDeAtributos.de(
            tamanhoMaximo("motivo", 300, RetornoDoFgtsRequest::motivo));

    void validar(RetornoDoFgtsRequest requisicao) {
        CADEIA.validar(requisicao);
    }
}
