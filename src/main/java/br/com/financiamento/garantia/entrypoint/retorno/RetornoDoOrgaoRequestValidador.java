package br.com.financiamento.garantia.entrypoint.retorno;

import br.com.financiamento.shared.validacao.atributo.CadeiaDeAtributos;
import jakarta.enterprise.context.ApplicationScoped;

import static br.com.financiamento.shared.validacao.atributo.ElosDeAtributo.*;

/**
 * Validação de atributos de {@link RetornoDoOrgaoRequest} (UC-0018), em cadeia (ADR 0011): forma e formato
 * de cada campo, com todas as falhas de uma vez. As regras de negócio ficam na cadeia de domínio.
 */
@ApplicationScoped
class RetornoDoOrgaoRequestValidador {

    private static final CadeiaDeAtributos<RetornoDoOrgaoRequest> CADEIA = CadeiaDeAtributos.de(
            obrigatorio("protocolo", RetornoDoOrgaoRequest::protocolo),
            tamanhoMaximo("protocolo", 40, RetornoDoOrgaoRequest::protocolo),
            obrigatorio("resultado", RetornoDoOrgaoRequest::resultado),
            tamanhoMaximo("identificacaoRegistrada", 80, RetornoDoOrgaoRequest::identificacaoRegistrada),
            tamanhoMaximo("texto", 600, RetornoDoOrgaoRequest::texto));

    void validar(RetornoDoOrgaoRequest requisicao) {
        CADEIA.validar(requisicao);
    }
}
