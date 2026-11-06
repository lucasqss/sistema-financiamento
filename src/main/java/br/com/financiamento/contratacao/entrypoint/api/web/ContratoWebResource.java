package br.com.financiamento.contratacao.entrypoint.api.web;

import br.com.financiamento.contratacao.aplicacao.AssinarContratoService;
import br.com.financiamento.contratacao.aplicacao.CancelarContratoService;
import br.com.financiamento.contratacao.aplicacao.ConsultarContratacaoService;
import br.com.financiamento.contratacao.aplicacao.ProrrogarObraService;
import br.com.financiamento.shared.seguranca.Papeis;
import jakarta.annotation.security.RolesAllowed;
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
    @RolesAllowed({Papeis.PROPONENTE, Papeis.COOBRIGADO, Papeis.VENDEDOR, Papeis.CORRESPONDENTE, Papeis.ANALISTA, Papeis.CONJUGE})
    public ContratoWebResponse consultar(@PathParam("id") Long id) {
        return ContratoWebResponse.de(consultar.contrato(id));
    }

    @POST
    @Path("/{id}/assinaturas")
    @RolesAllowed({Papeis.PROPONENTE, Papeis.COOBRIGADO, Papeis.VENDEDOR, Papeis.CONJUGE})
    public ContratoWebResponse assinar(@PathParam("id") Long id, AssinaturaRequest r) {
        assinaturaRequestValidador.validar(r);
        return ContratoWebResponse.de(assinar.executar(id, r.documento()));
    }

    @POST
    @Path("/{id}/cancelamento")
    @RolesAllowed(Papeis.PROPONENTE)
    public ContratoWebResponse cancelar(@PathParam("id") Long id, CancelamentoRequest r) {
        cancelamentoRequestValidador.validar(r);
        return ContratoWebResponse.de(cancelar.executar(id, r == null ? null : r.motivo()));
    }

    /** UC-0017: a incorporadora pede nova data para a obra. */
    @POST
    @Path("/{id}/prorrogacoes")
    @RolesAllowed(Papeis.VENDEDOR)
    public ContratoWebResponse prorrogar(@PathParam("id") Long id, ProrrogacaoRequest r) {
        prorrogacaoRequestValidador.validar(r);
        return ContratoWebResponse.de(prorrogar.pedir(id, r.novaData()));
    }

    /** RN-0027, acima do limite: o analista decide a prorrogação pendente. */
    @POST
    @Path("/{id}/prorrogacoes/pendente/decisao")
    @RolesAllowed(Papeis.ANALISTA)
    public ContratoWebResponse decidirProrrogacao(@PathParam("id") Long id, DecisaoDeProrrogacaoRequest r) {
        decisaoDeProrrogacaoRequestValidador.validar(r);
        return ContratoWebResponse.de(prorrogar.decidir(id, r.aprovada()));
    }
}
