package br.com.financiamento.originacao.entrypoint.api.web;

import br.com.financiamento.shared.validacao.atributo.CadeiaDeAtributos;
import jakarta.enterprise.context.ApplicationScoped;

import static br.com.financiamento.shared.validacao.atributo.ElosDeAtributo.*;

/**
 * Validação de atributos de {@link IniciarPropostaRequest} (UC-0001), em cadeia (ADR 0011): forma e formato
 * de cada campo, com todas as falhas de uma vez. As regras de negócio ficam na cadeia de domínio.
 */
@ApplicationScoped
class IniciarPropostaRequestValidador {

    private static final CadeiaDeAtributos<IniciarPropostaRequest> CADEIA = CadeiaDeAtributos.de(
            obrigatorio("tipoDeBem", IniciarPropostaRequest::tipoDeBem),
            formato("tipoDeBem", "[A-Za-z][A-Za-z_]*[A-Za-z]", "código do tipo de bem, ex.: CARRO_USADO", IniciarPropostaRequest::tipoDeBem));

    void validar(IniciarPropostaRequest requisicao) {
        CADEIA.validar(requisicao);
    }
}
