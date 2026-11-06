package br.com.financiamento.originacao.entrypoint.api.web;

import br.com.financiamento.shared.validacao.atributo.CadeiaDeAtributos;
import jakarta.enterprise.context.ApplicationScoped;

import static br.com.financiamento.shared.validacao.atributo.ElosDeAtributo.*;

/**
 * Validação de atributos de {@link CondicoesRequest} (UC-0004), em cadeia (ADR 0011): forma e formato
 * de cada campo, com todas as falhas de uma vez. As regras de negócio ficam na cadeia de domínio.
 */
@ApplicationScoped
class CondicoesRequestValidador {

    private static final CadeiaDeAtributos<CondicoesRequest> CADEIA = CadeiaDeAtributos.de(
            naoNegativo("entradaPropria", CondicoesRequest::entradaPropria),
            naoNegativo("entradaFgts", CondicoesRequest::entradaFgts),
            maiorQueZero("prazoMeses", CondicoesRequest::prazoMeses),
            obrigatorio("parcela", CondicoesRequest::parcela),
            maiorQueZero("parcela", CondicoesRequest::parcela));

    void validar(CondicoesRequest requisicao) {
        CADEIA.validar(requisicao);
    }
}
