package br.com.financiamento.originacao.entrypoint.api.web;

import br.com.financiamento.shared.validacao.atributo.CadeiaDeAtributos;
import jakarta.enterprise.context.ApplicationScoped;

import static br.com.financiamento.shared.validacao.atributo.ElosDeAtributo.*;

/**
 * Validação de atributos de {@link BemRequest} (UC-0003), em cadeia (ADR 0011): forma e formato
 * de cada campo, com todas as falhas de uma vez. As regras de negócio ficam na cadeia de domínio.
 */
@ApplicationScoped
class BemRequestValidador {

    private static final CadeiaDeAtributos<BemRequest> CADEIA = CadeiaDeAtributos.de(
            obrigatorio("identificacao", BemRequest::identificacao),
            tamanhoMaximo("identificacao", 80, BemRequest::identificacao),
            obrigatorio("valorDeCompra", BemRequest::valorDeCompra),
            maiorQueZero("valorDeCompra", BemRequest::valorDeCompra),
            obrigatorio("documentoDoVendedor", BemRequest::documentoDoVendedor),
            cpfOuCnpj("documentoDoVendedor", BemRequest::documentoDoVendedor),
            maiorQueZero("anoModelo", BemRequest::anoModelo));

    void validar(BemRequest requisicao) {
        CADEIA.validar(requisicao);
    }
}
