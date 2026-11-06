package br.com.financiamento.credito.entrypoint.retorno;

import br.com.financiamento.shared.validacao.atributo.CadeiaDeAtributos;
import jakarta.enterprise.context.ApplicationScoped;

import static br.com.financiamento.shared.validacao.atributo.ElosDeAtributo.*;

/**
 * Validação de atributos de {@link RetornoDoBureauRequest} (UC-0011), em cadeia (ADR 0011): forma e formato
 * de cada campo, com todas as falhas de uma vez. As regras de negócio ficam na cadeia de domínio.
 */
@ApplicationScoped
class RetornoDoBureauRequestValidador {

    private static final CadeiaDeAtributos<RetornoDoBureauRequest> CADEIA = CadeiaDeAtributos.de(
            obrigatorio("resultado", RetornoDoBureauRequest::resultado),
            tamanhoMaximo("ressalva", 500, RetornoDoBureauRequest::ressalva),
            maiorQueZero("sugeridas.prazoMeses", r -> r.sugeridas() == null ? null : r.sugeridas().prazoMeses()),
            maiorQueZero("sugeridas.parcela", r -> r.sugeridas() == null ? null : r.sugeridas().parcela()),
            naoNegativo("sugeridas.entradaPropria", r -> r.sugeridas() == null ? null : r.sugeridas().entradaPropria()));

    void validar(RetornoDoBureauRequest requisicao) {
        CADEIA.validar(requisicao);
    }
}
