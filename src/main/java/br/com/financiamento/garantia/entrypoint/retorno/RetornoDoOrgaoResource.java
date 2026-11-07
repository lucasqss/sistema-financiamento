package br.com.financiamento.garantia.entrypoint.retorno;

import br.com.financiamento.garantia.aplicacao.ClassificarRetornoService;
import br.com.financiamento.shared.seguranca.Papeis;
import jakarta.annotation.security.RolesAllowed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import jakarta.inject.Inject;
import br.com.financiamento.shared.web.RepeteEmConflito;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

/**
 * Retorno do cartório (ATR-0009) ou do órgão de trânsito (ATR-0010), pelo nosso
 * protocolo. Em produção chega pela fila (ADR 0006). É classificado, nunca
 * recusado (RN-0029): responde 202 com o efeito que teve.
 */
@Tag(name = "Retornos de sistemas externos", description = "Entradas dos sistemas externos (bureau, avaliador, seguradora, cartório/órgão de trânsito, FGTS, pagamento). Idempotentes pelo protocolo.")
@RepeteEmConflito
@Path("/retornos/orgao-de-registro")
public class RetornoDoOrgaoResource {

    @Inject RetornoDoOrgaoRequestValidador retornoDoOrgaoRequestValidador;

    @Inject ClassificarRetornoService classificar;

    @POST
    @Operation(summary = "Retorno do cartório ou órgão de trânsito (RN-0029: classificado, nunca recusado)")
    @RolesAllowed(Papeis.SISTEMA_REGISTRO)
    public Response receber(RetornoDoOrgaoRequest r) {
        retornoDoOrgaoRequestValidador.validar(r);
        var efeito = classificar.executar(r.protocolo(), r.resultado(), r.identificacaoRegistrada(), r.texto());
        return Response.accepted(new RetornoDoOrgaoResponse(r.protocolo(), efeito.name())).build();
    }
}
