package br.com.financiamento.documentos.entrypoint.api.web;

import br.com.financiamento.shared.validacao.atributo.CadeiaDeAtributos;
import br.com.financiamento.shared.validacao.atributo.ErroDeAtributo;
import jakarta.enterprise.context.ApplicationScoped;

import static br.com.financiamento.shared.validacao.atributo.ElosDeAtributo.*;

/**
 * Validação de atributos de {@link ConferenciaRequest} (UC-0024), em cadeia (ADR 0011): forma e formato
 * de cada campo, com todas as falhas de uma vez. As regras de negócio ficam na cadeia de domínio.
 */
@ApplicationScoped
class ConferenciaRequestValidador {

    private static final CadeiaDeAtributos<ConferenciaRequest> CADEIA = CadeiaDeAtributos.de(
            (contexto, proximo) -> {
                // A recusa (UC-0024) diz por quê: ilegível, vencido ou divergente.
                var r = contexto.requisicao();
                if (!r.aceito() && r.motivo() == null) contexto.falhar(ErroDeAtributo.OBRIGATORIO, "motivo");
                proximo.seguir(contexto);
            },
            tamanhoMaximo("observacao", 500, ConferenciaRequest::observacao));

    void validar(ConferenciaRequest requisicao) {
        CADEIA.validar(requisicao);
    }
}
