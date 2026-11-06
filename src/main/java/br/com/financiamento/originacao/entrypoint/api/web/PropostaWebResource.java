package br.com.financiamento.originacao.entrypoint.api.web;

import br.com.financiamento.originacao.aplicacao.CancelarPropostaService;
import br.com.financiamento.originacao.aplicacao.ConsultarPropostaService;
import br.com.financiamento.originacao.aplicacao.InformarBemService;
import br.com.financiamento.originacao.aplicacao.InformarParticipantesService;
import br.com.financiamento.originacao.aplicacao.IniciarPropostaService;
import br.com.financiamento.originacao.aplicacao.ResolverPendenciaService;
import br.com.financiamento.originacao.aplicacao.ResponderContrapropostaService;
import br.com.financiamento.originacao.aplicacao.SimularCondicoesService;
import br.com.financiamento.originacao.aplicacao.SubmeterPropostaService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

import java.net.URI;

/** Canal web da originação: monta, acompanha, submete e cancela propostas. */
@Path("/web/propostas")
public class PropostaWebResource {

    @Inject IniciarPropostaService iniciar;
    @Inject InformarParticipantesService informarParticipantes;
    @Inject InformarBemService informarBem;
    @Inject SimularCondicoesService simularCondicoes;
    @Inject SubmeterPropostaService submeter;
    @Inject CancelarPropostaService cancelar;
    @Inject ConsultarPropostaService consultar;
    @Inject ResponderContrapropostaService responderContraproposta;
    @Inject ResolverPendenciaService resolverPendencia;

    @POST
    public Response iniciar(IniciarPropostaRequest request) {
        var detalhe = DetalhePropostaWebResponse.de(iniciar.executar(request.tipoDeBem()));
        return Response.created(URI.create("/web/propostas/" + detalhe.id())).entity(detalhe).build();
    }

    @GET
    @Path("/{id}")
    public DetalhePropostaWebResponse consultar(@PathParam("id") Long id) {
        return DetalhePropostaWebResponse.de(consultar.executar(id));
    }

    @PUT
    @Path("/{id}/participantes")
    public DetalhePropostaWebResponse participantes(@PathParam("id") Long id, ParticipantesRequest request) {
        return DetalhePropostaWebResponse.de(informarParticipantes.executar(id, request.comando()));
    }

    @PUT
    @Path("/{id}/bem")
    public DetalhePropostaWebResponse bem(@PathParam("id") Long id, BemRequest request) {
        return DetalhePropostaWebResponse.de(informarBem.executar(id, request.comando()));
    }

    @PUT
    @Path("/{id}/condicoes")
    public DetalhePropostaWebResponse condicoes(@PathParam("id") Long id, CondicoesRequest request) {
        return DetalhePropostaWebResponse.de(simularCondicoes.executar(id, request.comando()));
    }

    @POST
    @Path("/{id}/submissao")
    public DetalhePropostaWebResponse submeter(@PathParam("id") Long id) {
        return DetalhePropostaWebResponse.de(submeter.executar(id));
    }

    @POST
    @Path("/{id}/cancelamento")
    public DetalhePropostaWebResponse cancelar(@PathParam("id") Long id) {
        return DetalhePropostaWebResponse.de(cancelar.executar(id));
    }

    @POST
    @Path("/{id}/contraproposta/aceite")
    public DetalhePropostaWebResponse aceitarContraproposta(@PathParam("id") Long id) {
        return DetalhePropostaWebResponse.de(responderContraproposta.aceitar(id));
    }

    @POST
    @Path("/{id}/contraproposta/recusa")
    public DetalhePropostaWebResponse recusarContraproposta(@PathParam("id") Long id) {
        return DetalhePropostaWebResponse.de(responderContraproposta.recusar(id));
    }

    @POST
    @Path("/{id}/pendencia/resolucao")
    public DetalhePropostaWebResponse resolverPendencia(@PathParam("id") Long id) {
        return DetalhePropostaWebResponse.de(resolverPendencia.executar(id));
    }
}
