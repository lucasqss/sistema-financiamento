package br.com.financiamento.avaliacao.entrypoint.retorno;

import br.com.financiamento.avaliacao.aplicacao.RegistrarLaudoService;
import br.com.financiamento.avaliacao.aplicacao.RegistrarLaudoService.Laudo;
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
 * Retorno do avaliador (ATR-0007), pelo protocolo da avaliação. Em produção
 * chega pela fila (ADR 0006); o tratamento é o mesmo e é idempotente.
 */
@Tag(name = "Retornos de sistemas externos", description = "Entradas dos sistemas externos (bureau, avaliador, seguradora, cartório/órgão de trânsito, FGTS, pagamento). Idempotentes pelo protocolo.")
@RepeteEmConflito
@Path("/retornos/avaliador")
public class RetornoDoAvaliadorResource {

    @Inject LaudoRequestValidador laudoRequestValidador;

    @Inject RegistrarLaudoService registrar;

    @POST
    @Path("/{avaliacaoId}")
    @Operation(summary = "Laudo do avaliador (UC-0012)")
    @RolesAllowed(Papeis.SISTEMA_AVALIADOR)
    public Response receber(@PathParam("avaliacaoId") Long avaliacaoId, LaudoRequest r) {
        laudoRequestValidador.validar(r);
        registrar.executar(avaliacaoId, new Laudo(r.aceitoComoGarantia(), r.valor(), r.avaliadaEm()));
        return Response.accepted().build();
    }
}
