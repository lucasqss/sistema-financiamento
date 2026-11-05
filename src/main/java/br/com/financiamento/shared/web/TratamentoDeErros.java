package br.com.financiamento.shared.web;

import br.com.financiamento.shared.dominio.NaoEncontradoException;
import br.com.financiamento.shared.dominio.RegraVioladaException;
import br.com.financiamento.shared.dominio.Violacao;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import java.util.List;

/** Erros de negócio viram respostas HTTP com a lista do que foi recusado. */
class TratamentoDeErros {

    record Erro(String mensagem, List<Violacao> violacoes) {
    }

    @ServerExceptionMapper
    Response regraViolada(RegraVioladaException e) {
        return Response.status(422).entity(new Erro("Recusado por regra de negócio", e.violacoes())).build();
    }

    @ServerExceptionMapper
    Response naoEncontrado(NaoEncontradoException e) {
        return Response.status(404).entity(new Erro(e.getMessage(), List.of())).build();
    }
}
