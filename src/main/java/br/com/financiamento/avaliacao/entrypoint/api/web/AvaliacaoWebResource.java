package br.com.financiamento.avaliacao.entrypoint.api.web;

import br.com.financiamento.avaliacao.aplicacao.ConsultarAvaliacoesService;
import br.com.financiamento.shared.seguranca.Papeis;
import jakarta.annotation.security.RolesAllowed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

import java.util.List;

import static br.com.financiamento.shared.validacao.atributo.ParametroDeConsulta.obrigatorio;

/** Acompanhamento das avaliações de uma proposta. */
@Tag(name = "Avaliação", description = "Avaliação do bem e medição da obra (DOM-0003).")
@Path("/web/avaliacoes")
public class AvaliacaoWebResource {

    @Inject ConsultarAvaliacoesService consultar;

    @GET
    @Operation(summary = "Avaliações do bem de uma proposta")
    @RolesAllowed(Papeis.ANALISTA)
    public List<AvaliacaoWebResponse> daProposta(@Parameter(required = true) @QueryParam("proposta") Long propostaId) {
        return consultar.daProposta(obrigatorio("proposta", propostaId)).stream().map(AvaliacaoWebResponse::de).toList();
    }
}
