package br.com.financiamento.avaliacao.entrypoint.retorno;

import br.com.financiamento.shared.validacao.atributo.CadeiaDeAtributos;
import jakarta.enterprise.context.ApplicationScoped;

import static br.com.financiamento.shared.validacao.atributo.ElosDeAtributo.*;

/**
 * Validação de atributos de {@link LaudoRequest} (UC-0012), em cadeia (ADR 0011): forma e formato
 * de cada campo, com todas as falhas de uma vez. As regras de negócio ficam na cadeia de domínio.
 */
@ApplicationScoped
class LaudoRequestValidador {

    private static final CadeiaDeAtributos<LaudoRequest> CADEIA = CadeiaDeAtributos.de(
            obrigatorio("avaliadaEm", LaudoRequest::avaliadaEm),
            maiorQueZero("valor", LaudoRequest::valor));

    void validar(LaudoRequest requisicao) {
        CADEIA.validar(requisicao);
    }
}
