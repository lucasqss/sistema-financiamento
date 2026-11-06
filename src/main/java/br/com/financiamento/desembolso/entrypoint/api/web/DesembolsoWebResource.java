package br.com.financiamento.desembolso.entrypoint.api.web;

import br.com.financiamento.desembolso.aplicacao.LiberarRecursoService;
import br.com.financiamento.shared.validacao.ErroDeDominioException;
import br.com.financiamento.desembolso.dominio.ErrosDoDesembolso;
import br.com.financiamento.shared.seguranca.Papeis;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

/** Acompanhar o desembolso e recompor a entrada com o FGTS negado (UC-0022). */
@Path("/web/desembolsos")
public class DesembolsoWebResource {

    @Inject LiberarRecursoService liberar;

    @GET
    @RolesAllowed(Papeis.ANALISTA)
    public DesembolsoWebResponse doContrato(@QueryParam("contrato") Long contratoId) {
        return liberar.doContrato(contratoId).map(DesembolsoWebResponse::de)
                .orElseThrow(() -> new ErroDeDominioException(ErrosDoDesembolso.CONTRATO_SEM_DESEMBOLSO, contratoId));
    }

    @POST
    @Path("/{id}/entrada-recomposta")
    @RolesAllowed(Papeis.PROPONENTE)
    public DesembolsoWebResponse entradaRecomposta(@PathParam("id") Long id) {
        return DesembolsoWebResponse.de(liberar.entradaRecomposta(id));
    }
}
