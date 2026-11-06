package br.com.financiamento.contratacao.entrypoint.api.web;

import br.com.financiamento.contratacao.aplicacao.AceitarCondicoesService;
import br.com.financiamento.contratacao.aplicacao.ConsultarContratacaoService;
import br.com.financiamento.contratacao.aplicacao.DetalheDaContratacao;
import br.com.financiamento.contratacao.aplicacao.EmitirContratoService;
import br.com.financiamento.shared.seguranca.Papeis;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

import java.net.URI;

/** Canal web da contratação: aceite das condições finais e emissão (UC-0014). */
@Path("/web/contratacoes")
public class ContratacaoWebResource {

    @Inject AceiteRequestValidador aceiteRequestValidador;

    @Inject AceitarCondicoesService aceitar;
    @Inject EmitirContratoService emitir;
    @Inject ConsultarContratacaoService consultar;

    @POST
    @RolesAllowed(Papeis.PROPONENTE)
    public Response aceitar(AceiteRequest r) {
        aceiteRequestValidador.validar(r);
        var d = aceitar.executar(r.propostaId(), r.segurosOpcionais(), r.conjuges(), r.previsaoDeEntregaDaObra());
        if (d.id() == null) return impedida(d);
        return Response.created(URI.create("/web/contratacoes/" + d.id())).entity(ContratacaoWebResponse.de(d)).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({Papeis.PROPONENTE, Papeis.COOBRIGADO, Papeis.VENDEDOR, Papeis.CORRESPONDENTE, Papeis.ANALISTA})
    public ContratacaoWebResponse consultar(@PathParam("id") Long id) {
        return ContratacaoWebResponse.de(consultar.contratacao(id));
    }

    /** Nova tentativa de emissão, depois de resolvido um impedimento. */
    @POST
    @Path("/{id}/emissao")
    @RolesAllowed(Papeis.ANALISTA)
    public Response emitir(@PathParam("id") Long id) {
        var d = emitir.executar(id);
        return d.contratoId() == null ? impedida(d) : Response.ok(ContratacaoWebResponse.de(d)).build();
    }

    /** O impedimento já mudou a proposta (expirada ou com pendência): 422, mas sem desfazer essa mudança. */
    private static Response impedida(DetalheDaContratacao d) {
        return Response.status(422).entity(ContratacaoWebResponse.de(d)).build();
    }
}
