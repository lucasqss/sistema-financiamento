package br.com.financiamento.documentos.entrypoint.api.web;

import br.com.financiamento.shared.validacao.atributo.CadeiaDeAtributos;
import java.util.Set;
import jakarta.enterprise.context.ApplicationScoped;

import static br.com.financiamento.shared.validacao.atributo.ElosDeAtributo.*;

/**
 * Validação de atributos de {@link EnvioDeDocumentoRequest} (UC-0024), em cadeia (ADR 0011): forma e formato
 * de cada campo, com todas as falhas de uma vez. As regras de negócio ficam na cadeia de domínio.
 */
@ApplicationScoped
class EnvioDeDocumentoRequestValidador {

    private static final CadeiaDeAtributos<EnvioDeDocumentoRequest> CADEIA = CadeiaDeAtributos.de(
            obrigatorio("propostaId", EnvioDeDocumentoRequest::propostaId),
            obrigatorio("tipo", EnvioDeDocumentoRequest::tipo),
            tamanhoMaximo("tipo", 120, EnvioDeDocumentoRequest::tipo),
            formato("titular", "bem|[0-9.\\-/]{11,18}", "\"bem\" ou o CPF/CNPJ do participante", EnvioDeDocumentoRequest::titular),
            obrigatorio("arquivo", EnvioDeDocumentoRequest::nomeDoArquivo),
            tamanhoMaximo("arquivo", 255, EnvioDeDocumentoRequest::nomeDoArquivo),
            umDe("arquivo.tipo", Set.of("application/pdf", "image/jpeg", "image/png"), EnvioDeDocumentoRequest::tipoDeConteudo),
            maiorQueZero("arquivo.tamanho", EnvioDeDocumentoRequest::tamanho),
            noMaximo("arquivo.tamanho", 10L * 1024 * 1024, EnvioDeDocumentoRequest::tamanho));

    void validar(EnvioDeDocumentoRequest requisicao) {
        CADEIA.validar(requisicao);
    }
}
