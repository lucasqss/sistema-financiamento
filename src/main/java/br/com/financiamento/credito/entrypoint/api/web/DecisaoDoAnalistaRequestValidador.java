package br.com.financiamento.credito.entrypoint.api.web;

import br.com.financiamento.shared.validacao.atributo.CadeiaDeAtributos;
import jakarta.enterprise.context.ApplicationScoped;

import static br.com.financiamento.shared.validacao.atributo.ElosDeAtributo.*;

/**
 * Validação de atributos de {@link DecisaoDoAnalistaRequest} (UC-0011), em cadeia (ADR 0011): forma e formato
 * de cada campo, com todas as falhas de uma vez. As regras de negócio ficam na cadeia de domínio.
 */
@ApplicationScoped
class DecisaoDoAnalistaRequestValidador {

    private static final CadeiaDeAtributos<DecisaoDoAnalistaRequest> CADEIA = CadeiaDeAtributos.de(
            obrigatorio("resultado", DecisaoDoAnalistaRequest::resultado),
            tamanhoMaximo("ressalva", 500, DecisaoDoAnalistaRequest::ressalva),
            maiorQueZero("sugeridas.prazoMeses", r -> r.sugeridas() == null ? null : r.sugeridas().prazoMeses()),
            maiorQueZero("sugeridas.parcela", r -> r.sugeridas() == null ? null : r.sugeridas().parcela()),
            naoNegativo("sugeridas.entradaPropria", r -> r.sugeridas() == null ? null : r.sugeridas().entradaPropria()));

    void validar(DecisaoDoAnalistaRequest requisicao) {
        CADEIA.validar(requisicao);
    }
}
