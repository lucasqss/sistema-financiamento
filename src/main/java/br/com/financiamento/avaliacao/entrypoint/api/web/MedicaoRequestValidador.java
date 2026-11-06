package br.com.financiamento.avaliacao.entrypoint.api.web;

import br.com.financiamento.shared.validacao.atributo.CadeiaDeAtributos;
import jakarta.enterprise.context.ApplicationScoped;

import static br.com.financiamento.shared.validacao.atributo.ElosDeAtributo.*;

/**
 * Validação de atributos de {@link MedicaoRequest} (UC-0013), em cadeia (ADR 0011): forma e formato
 * de cada campo, com todas as falhas de uma vez. As regras de negócio ficam na cadeia de domínio.
 */
@ApplicationScoped
class MedicaoRequestValidador {

    private static final CadeiaDeAtributos<MedicaoRequest> CADEIA = CadeiaDeAtributos.de(
            obrigatorio("contratoId", MedicaoRequest::contratoId),
            obrigatorio("percentualAcumulado", MedicaoRequest::percentualAcumulado),
            maiorQueZero("percentualAcumulado", MedicaoRequest::percentualAcumulado),
            noMaximo("percentualAcumulado", 100, MedicaoRequest::percentualAcumulado),
            obrigatorio("medidaEm", MedicaoRequest::medidaEm));

    void validar(MedicaoRequest requisicao) {
        CADEIA.validar(requisicao);
    }
}
