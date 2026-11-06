package br.com.financiamento.credito.entrypoint.api.web;

import br.com.financiamento.credito.aplicacao.ConsultarAnalisesService;
import br.com.financiamento.credito.aplicacao.RegistrarDecisaoService;
import br.com.financiamento.credito.aplicacao.RegistrarDecisaoService.Decisao;
import br.com.financiamento.credito.dominio.AnaliseDeCredito;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

import java.util.List;

/** Canal web do analista de crédito (ATR-0005). */
@Path("/web/analises-de-credito")
public class AnaliseDeCreditoWebResource {

    @Inject DecisaoDoAnalistaRequestValidador decisaoDoAnalistaRequestValidador;

    @Inject ConsultarAnalisesService consultar;
    @Inject RegistrarDecisaoService registrar;

    @GET
    public List<AnaliseDeCreditoWebResponse> daProposta(@QueryParam("proposta") Long propostaId) {
        return consultar.daProposta(propostaId).stream().map(AnaliseDeCreditoWebResponse::de).toList();
    }

    @POST
    @Path("/{id}/decisao")
    public AnaliseDeCreditoWebResponse decidir(@PathParam("id") Long id, DecisaoDoAnalistaRequest r) {
        decisaoDoAnalistaRequestValidador.validar(r);
        return AnaliseDeCreditoWebResponse.de(registrar.executar(id,
                new Decisao(r.resultado(), r.ressalva(), r.sugeridas(), AnaliseDeCredito.Origem.ANALISTA)));
    }
}
