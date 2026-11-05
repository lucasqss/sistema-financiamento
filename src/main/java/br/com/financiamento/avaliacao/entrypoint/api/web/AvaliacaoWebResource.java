package br.com.financiamento.avaliacao.entrypoint.api.web;

import br.com.financiamento.avaliacao.aplicacao.ConsultarAvaliacoesService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

import java.util.List;

/** Acompanhamento das avaliações de uma proposta. */
@Path("/web/avaliacoes")
public class AvaliacaoWebResource {

    @Inject ConsultarAvaliacoesService consultar;

    @GET
    public List<AvaliacaoWebResponse> daProposta(@QueryParam("proposta") Long propostaId) {
        return consultar.daProposta(propostaId).stream().map(AvaliacaoWebResponse::de).toList();
    }
}
