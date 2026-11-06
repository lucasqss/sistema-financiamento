package br.com.financiamento.desembolso.entrypoint.retorno;

import br.com.financiamento.shared.validacao.atributo.CadeiaDeAtributos;
import jakarta.enterprise.context.ApplicationScoped;

import static br.com.financiamento.shared.validacao.atributo.ElosDeAtributo.*;

/**
 * Validação de atributos de {@link PagamentoRequest} (UC-0021), em cadeia (ADR 0011): forma e formato
 * de cada campo, com todas as falhas de uma vez. As regras de negócio ficam na cadeia de domínio.
 */
@ApplicationScoped
class PagamentoRequestValidador {

    private static final CadeiaDeAtributos<PagamentoRequest> CADEIA = CadeiaDeAtributos.de(
            obrigatorio("protocolo", PagamentoRequest::protocolo),
            tamanhoMaximo("protocolo", 40, PagamentoRequest::protocolo));

    void validar(PagamentoRequest requisicao) {
        CADEIA.validar(requisicao);
    }
}
