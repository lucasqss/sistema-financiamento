package br.com.financiamento.contratacao.entrypoint.retorno;

import br.com.financiamento.contratacao.aplicacao.RegistrarApoliceService;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

/**
 * Retorno da seguradora (ATR-0011), pelo protocolo da contratação. Em produção
 * chega pela fila (ADR 0006); o tratamento é o mesmo e é idempotente.
 */
@Path("/retornos/seguradora")
public class RetornoDaSeguradoraResource {

    @Inject ApoliceRequestValidador apoliceRequestValidador;

    @Inject RegistrarApoliceService registrar;

    @POST
    @Path("/{contratacaoId}")
    public Response receber(@PathParam("contratacaoId") Long contratacaoId, ApoliceRequest r) {
        apoliceRequestValidador.validar(r);
        registrar.executar(contratacaoId, r.seguro(), r.apolice());
        return Response.accepted().build();
    }
}
