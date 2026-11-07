package br.com.financiamento.credito.entrypoint.retorno;

import br.com.financiamento.credito.aplicacao.RegistrarDecisaoService;
import br.com.financiamento.credito.aplicacao.RegistrarDecisaoService.Decisao;
import br.com.financiamento.credito.dominio.AnaliseDeCredito;
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
 * Retorno do bureau de crédito (ATR-0008), pelo protocolo da análise. Em
 * produção chega pela fila (ADR 0006); o tratamento é o mesmo e é idempotente.
 */
@Tag(name = "Retornos de sistemas externos", description = "Entradas dos sistemas externos (bureau, avaliador, seguradora, cartório/órgão de trânsito, FGTS, pagamento). Idempotentes pelo protocolo.")
@RepeteEmConflito
@Path("/retornos/bureau-de-credito")
public class RetornoDoBureauResource {

    @Inject RetornoDoBureauRequestValidador retornoDoBureauRequestValidador;

    @Inject RegistrarDecisaoService registrar;

    @POST
    @Path("/{analiseId}")
    @Operation(summary = "Retorno do bureau de crédito (UC-0011)")
    @RolesAllowed(Papeis.SISTEMA_BUREAU)
    public Response receber(@PathParam("analiseId") Long analiseId, RetornoDoBureauRequest r) {
        retornoDoBureauRequestValidador.validar(r);
        registrar.executar(analiseId, new Decisao(r.resultado(), r.ressalva(), r.sugeridas(), AnaliseDeCredito.Origem.BUREAU));
        return Response.accepted().build();
    }
}
