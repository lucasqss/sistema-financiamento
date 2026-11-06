package br.com.financiamento.contratacao.entrypoint.api.web;

import br.com.financiamento.contratacao.aplicacao.AssinarContratoService;
import br.com.financiamento.contratacao.aplicacao.CancelarContratoService;
import br.com.financiamento.contratacao.aplicacao.ConsultarContratacaoService;
import br.com.financiamento.contratacao.aplicacao.ProrrogarObraService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

/** Canal web do contrato: acompanhar, assinar (UC-0015) e desistir (UC-0016). */
@Path("/web/contratos")
public class ContratoWebResource {

    @Inject AssinaturaRequestValidador assinaturaRequestValidador;
    @Inject CancelamentoRequestValidador cancelamentoRequestValidador;
    @Inject DecisaoDeProrrogacaoRequestValidador decisaoDeProrrogacaoRequestValidador;
    @Inject ProrrogacaoRequestValidador prorrogacaoRequestValidador;

    @Inject ConsultarContratacaoService consultar;
    @Inject AssinarContratoService assinar;
    @Inject CancelarContratoService cancelar;
    @Inject ProrrogarObraService prorrogar;

    @GET
    @Path("/{id}")
    public ContratoWebResponse consultar(@PathParam("id") Long id) {
        return ContratoWebResponse.de(consultar.contrato(id));
    }

    @POST
    @Path("/{id}/assinaturas")
    public ContratoWebResponse assinar(@PathParam("id") Long id, AssinaturaRequest r) {
        assinaturaRequestValidador.validar(r);
        return ContratoWebResponse.de(assinar.executar(id, r.documento()));
    }

    @POST
    @Path("/{id}/cancelamento")
    public ContratoWebResponse cancelar(@PathParam("id") Long id, CancelamentoRequest r) {
        cancelamentoRequestValidador.validar(r);
        return ContratoWebResponse.de(cancelar.executar(id, r == null ? null : r.motivo()));
    }

    /** UC-0017: a incorporadora pede nova data para a obra. */
    @POST
    @Path("/{id}/prorrogacoes")
    public ContratoWebResponse prorrogar(@PathParam("id") Long id, ProrrogacaoRequest r) {
        prorrogacaoRequestValidador.validar(r);
        return ContratoWebResponse.de(prorrogar.pedir(id, r.novaData()));
    }

    /** RN-0027, acima do limite: o analista decide a prorrogação pendente. */
    @POST
    @Path("/{id}/prorrogacoes/pendente/decisao")
    public ContratoWebResponse decidirProrrogacao(@PathParam("id") Long id, DecisaoDeProrrogacaoRequest r) {
        decisaoDeProrrogacaoRequestValidador.validar(r);
        return ContratoWebResponse.de(prorrogar.decidir(id, r.aprovada()));
    }
}
