package br.com.financiamento.desembolso.entrypoint.retorno;

import br.com.financiamento.desembolso.aplicacao.LiberarRecursoService;
import br.com.financiamento.shared.seguranca.Papeis;
import jakarta.annotation.security.RolesAllowed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import jakarta.inject.Inject;
import br.com.financiamento.shared.web.RepeteEmConflito;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

/**
 * Retornos do sistema do FGTS (ATR-0012) e da confirmação de pagamento ao
 * vendedor. Em produção chegam pela fila (ADR 0006); são idempotentes.
 */
@Tag(name = "Retornos de sistemas externos", description = "Entradas dos sistemas externos (bureau, avaliador, seguradora, cartório/órgão de trânsito, FGTS, pagamento). Idempotentes pelo protocolo.")
@RepeteEmConflito
@Path("/retornos")
public class RetornoDoDesembolsoResource {

    @Inject PagamentoRequestValidador pagamentoRequestValidador;
    @Inject RetornoDoFgtsRequestValidador retornoDoFgtsRequestValidador;

    @Inject LiberarRecursoService liberar;

    @POST
    @Path("/fgts/{desembolsoId}")
    @Operation(summary = "Retorno do sistema do FGTS (UC-0021, UC-0022)")
    @RolesAllowed(Papeis.SISTEMA_FGTS)
    public Response fgts(@PathParam("desembolsoId") Long desembolsoId, RetornoDoFgtsRequest r) {
        retornoDoFgtsRequestValidador.validar(r);
        liberar.fgtsRespondido(desembolsoId, r.liberado(), r.motivo());
        return Response.accepted().build();
    }

    @POST
    @Path("/pagamento")
    @Operation(summary = "Confirmação do pagamento ao vendedor (UC-0021, UC-0023)")
    @RolesAllowed(Papeis.SISTEMA_PAGAMENTO)
    public Response pagamento(PagamentoRequest r) {
        pagamentoRequestValidador.validar(r);
        liberar.pagamentoConfirmado(r.protocolo());
        return Response.accepted().build();
    }
}
