package br.com.financiamento.desembolso.entrypoint.api.web;

import br.com.financiamento.desembolso.aplicacao.LiberarRecursoService;
import br.com.financiamento.shared.validacao.ErroDeDominioException;
import br.com.financiamento.desembolso.dominio.ErrosDoDesembolso;
import br.com.financiamento.shared.seguranca.Papeis;
import jakarta.annotation.security.RolesAllowed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

import static br.com.financiamento.shared.validacao.atributo.ParametroDeConsulta.obrigatorio;

/** Acompanhar o desembolso e recompor a entrada com o FGTS negado (UC-0022). */
@Tag(name = "Desembolso", description = "Liberação do recurso e FGTS da entrada (DOM-0006).")
@Path("/web/desembolsos")
public class DesembolsoWebResource {

    @Inject LiberarRecursoService liberar;

    @GET
    @Operation(summary = "Desembolso de um contrato")
    @RolesAllowed(Papeis.ANALISTA)
    public DesembolsoWebResponse doContrato(@Parameter(required = true) @QueryParam("contrato") Long contratoId) {
        return liberar.doContrato(obrigatorio("contrato", contratoId)).map(DesembolsoWebResponse::de)
                .orElseThrow(() -> new ErroDeDominioException(ErrosDoDesembolso.CONTRATO_SEM_DESEMBOLSO, contratoId));
    }

    @POST
    @Path("/{id}/entrada-recomposta")
    @Operation(summary = "UC-0022 Recompor a entrada (FGTS negado)")
    @RolesAllowed(Papeis.PROPONENTE)
    public DesembolsoWebResponse entradaRecomposta(@PathParam("id") Long id) {
        return DesembolsoWebResponse.de(liberar.entradaRecomposta(id));
    }
}
