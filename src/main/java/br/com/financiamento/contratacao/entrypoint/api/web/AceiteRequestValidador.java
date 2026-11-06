package br.com.financiamento.contratacao.entrypoint.api.web;

import br.com.financiamento.contratacao.aplicacao.AceitarCondicoesService.Conjuge;
import br.com.financiamento.shared.validacao.atributo.CadeiaDeAtributos;
import jakarta.enterprise.context.ApplicationScoped;

import static br.com.financiamento.shared.validacao.atributo.ElosDeAtributo.*;

/**
 * Validação de atributos de {@link AceiteRequest} (UC-0014), em cadeia (ADR 0011): forma e formato
 * de cada campo, com todas as falhas de uma vez. As regras de negócio ficam na cadeia de domínio.
 */
@ApplicationScoped
class AceiteRequestValidador {

    /** Cada cônjuge que assina (RN-0026): CPF válido e nome. */
    private static final CadeiaDeAtributos<Conjuge> CONJUGE = CadeiaDeAtributos.de(
            obrigatorio("documento", Conjuge::documento),
            cpf("documento", Conjuge::documento),
            obrigatorio("nome", Conjuge::nome),
            tamanhoMaximo("nome", 200, Conjuge::nome));

    private static final CadeiaDeAtributos<AceiteRequest> CADEIA = CadeiaDeAtributos.de(
            obrigatorio("propostaId", AceiteRequest::propostaId),
            cadaItem("conjuges", AceiteRequest::conjuges, CONJUGE));

    void validar(AceiteRequest requisicao) {
        CADEIA.validar(requisicao);
    }
}
