package br.com.financiamento.originacao.entrypoint.api.web;

import br.com.financiamento.shared.validacao.atributo.CadeiaDeAtributos;
import jakarta.enterprise.context.ApplicationScoped;

import static br.com.financiamento.shared.validacao.atributo.ElosDeAtributo.*;

/**
 * Validação de atributos de {@link ParticipantesRequest} (UC-0002), em cadeia (ADR 0011): forma e formato
 * de cada campo, com todas as falhas de uma vez. As regras de negócio ficam na cadeia de domínio.
 */
@ApplicationScoped
class ParticipantesRequestValidador {

    /** Cada participante: papel, forma, CPF/CNPJ válido, nome e renda. */
    private static final CadeiaDeAtributos<ParticipantesRequest.Item> ITEM = CadeiaDeAtributos.de(
            obrigatorio("papel", ParticipantesRequest.Item::papel),
            obrigatorio("forma", ParticipantesRequest.Item::forma),
            obrigatorio("documento", ParticipantesRequest.Item::documento),
            cpfOuCnpj("documento", ParticipantesRequest.Item::documento),
            obrigatorio("nome", ParticipantesRequest.Item::nome),
            tamanhoMaximo("nome", 200, ParticipantesRequest.Item::nome),
            naoNegativo("rendaComprovada", ParticipantesRequest.Item::rendaComprovada));

    private static final CadeiaDeAtributos<ParticipantesRequest> CADEIA = CadeiaDeAtributos.de(
            obrigatorio("participantes", ParticipantesRequest::participantes),
            cadaItem("participantes", ParticipantesRequest::participantes, ITEM));

    void validar(ParticipantesRequest requisicao) {
        CADEIA.validar(requisicao);
    }
}
