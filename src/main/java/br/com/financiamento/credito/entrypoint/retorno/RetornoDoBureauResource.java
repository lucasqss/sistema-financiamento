package br.com.financiamento.credito.entrypoint.retorno;

import br.com.financiamento.credito.aplicacao.RegistrarDecisaoService;
import br.com.financiamento.credito.aplicacao.RegistrarDecisaoService.Decisao;
import br.com.financiamento.credito.dominio.AnaliseDeCredito;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

/**
 * Retorno do bureau de crédito (ATR-0008), pelo protocolo da análise. Em
 * produção chega pela fila (ADR 0006); o tratamento é o mesmo e é idempotente.
 */
@Path("/retornos/bureau-de-credito")
public class RetornoDoBureauResource {

    @Inject RetornoDoBureauRequestValidador retornoDoBureauRequestValidador;

    @Inject RegistrarDecisaoService registrar;

    @POST
    @Path("/{analiseId}")
    public Response receber(@PathParam("analiseId") Long analiseId, RetornoDoBureauRequest r) {
        retornoDoBureauRequestValidador.validar(r);
        registrar.executar(analiseId, new Decisao(r.resultado(), r.ressalva(), r.sugeridas(), AnaliseDeCredito.Origem.BUREAU));
        return Response.accepted().build();
    }
}
