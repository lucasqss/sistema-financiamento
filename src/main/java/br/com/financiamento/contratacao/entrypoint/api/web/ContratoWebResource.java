package br.com.financiamento.contratacao.entrypoint.api.web;

import br.com.financiamento.contratacao.aplicacao.AssinarContratoService;
import br.com.financiamento.contratacao.aplicacao.CancelarContratoService;
import br.com.financiamento.contratacao.aplicacao.ConsultarContratacaoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

/** Canal web do contrato: acompanhar, assinar (UC-0015) e desistir (UC-0016). */
@Path("/web/contratos")
public class ContratoWebResource {

    @Inject ConsultarContratacaoService consultar;
    @Inject AssinarContratoService assinar;
    @Inject CancelarContratoService cancelar;

    @GET
    @Path("/{id}")
    public ContratoWebResponse consultar(@PathParam("id") Long id) {
        return ContratoWebResponse.de(consultar.contrato(id));
    }

    @POST
    @Path("/{id}/assinaturas")
    public ContratoWebResponse assinar(@PathParam("id") Long id, AssinaturaRequest r) {
        return ContratoWebResponse.de(assinar.executar(id, r.documento()));
    }

    @POST
    @Path("/{id}/cancelamento")
    public ContratoWebResponse cancelar(@PathParam("id") Long id, CancelamentoRequest r) {
        return ContratoWebResponse.de(cancelar.executar(id, r == null ? null : r.motivo()));
    }
}
