package br.com.financiamento.avaliacao.entrypoint.api.web;

import br.com.financiamento.avaliacao.aplicacao.ConsultarAvaliacoesService;
import br.com.financiamento.shared.seguranca.Papeis;
import jakarta.annotation.security.RolesAllowed;
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
    @RolesAllowed(Papeis.ANALISTA)
    public List<AvaliacaoWebResponse> daProposta(@QueryParam("proposta") Long propostaId) {
        return consultar.daProposta(propostaId).stream().map(AvaliacaoWebResponse::de).toList();
    }
}
