package br.com.financiamento.credito.entrypoint.api.web;

import br.com.financiamento.credito.aplicacao.ConsultarAnalisesService;
import br.com.financiamento.credito.aplicacao.RegistrarDecisaoService;
import br.com.financiamento.credito.aplicacao.RegistrarDecisaoService.Decisao;
import br.com.financiamento.credito.dominio.AnaliseDeCredito;
import br.com.financiamento.shared.seguranca.Papeis;
import jakarta.annotation.security.RolesAllowed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

import java.util.List;

/** Canal web do analista de crédito (ATR-0005). */
@Tag(name = "Crédito", description = "Análise de crédito pelo analista (DOM-0002).")
@Path("/web/analises-de-credito")
public class AnaliseDeCreditoWebResource {

    @Inject DecisaoDoAnalistaRequestValidador decisaoDoAnalistaRequestValidador;

    @Inject ConsultarAnalisesService consultar;
    @Inject RegistrarDecisaoService registrar;

    @GET
    @Operation(summary = "Análises de crédito de uma proposta")
    @RolesAllowed(Papeis.ANALISTA)
    public List<AnaliseDeCreditoWebResponse> daProposta(@QueryParam("proposta") Long propostaId) {
        return consultar.daProposta(propostaId).stream().map(AnaliseDeCreditoWebResponse::de).toList();
    }

    @POST
    @Path("/{id}/decisao")
    @Operation(summary = "UC-0011 Decisão do analista (fora da alçada automática)")
    @RolesAllowed(Papeis.ANALISTA)
    public AnaliseDeCreditoWebResponse decidir(@PathParam("id") Long id, DecisaoDoAnalistaRequest r) {
        decisaoDoAnalistaRequestValidador.validar(r);
        return AnaliseDeCreditoWebResponse.de(registrar.executar(id,
                new Decisao(r.resultado(), r.ressalva(), r.sugeridas(), AnaliseDeCredito.Origem.ANALISTA)));
    }
}
