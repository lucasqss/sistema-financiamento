package br.com.financiamento.contratacao.entrypoint.retorno;

import br.com.financiamento.shared.validacao.atributo.CadeiaDeAtributos;
import jakarta.enterprise.context.ApplicationScoped;

import static br.com.financiamento.shared.validacao.atributo.ElosDeAtributo.*;

/**
 * Validação de atributos de {@link ApoliceRequest} (UC-0014), em cadeia (ADR 0011): forma e formato
 * de cada campo, com todas as falhas de uma vez. As regras de negócio ficam na cadeia de domínio.
 */
@ApplicationScoped
class ApoliceRequestValidador {

    private static final CadeiaDeAtributos<ApoliceRequest> CADEIA = CadeiaDeAtributos.de(
            obrigatorio("seguro", ApoliceRequest::seguro),
            tamanhoMaximo("seguro", 60, ApoliceRequest::seguro),
            obrigatorio("apolice", ApoliceRequest::apolice),
            tamanhoMaximo("apolice", 60, ApoliceRequest::apolice));

    void validar(ApoliceRequest requisicao) {
        CADEIA.validar(requisicao);
    }
}
