package br.com.financiamento.shared.web;

import br.com.financiamento.shared.validacao.ErroDeAtributoException;
import br.com.financiamento.shared.validacao.ErroDeDominioException;
import br.com.financiamento.shared.validacao.Falha;
import br.com.financiamento.shared.validacao.TipoDeErro;
import br.com.financiamento.shared.validacao.atributo.ErroDeAtributo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import java.util.List;

/**
 * Os erros do catálogo viram respostas num formato único (ADR 0011): 400 para
 * atributo, 422 ou 404 para domínio, com o código nosso e o corporativo de cada falha.
 */
class TratamentoDeErros {

    record FalhaNoCorpo(String codigo, String codigoCorporativo, String campo, String regra, String mensagem) {
    }

    record CorpoDoErro(TipoDeErro tipo, List<FalhaNoCorpo> falhas) {
    }

    @Inject
    CatalogoCorporativo catalogo;

    @ServerExceptionMapper
    Response atributo(ErroDeAtributoException e) {
        return Response.status(400).entity(corpo(TipoDeErro.ATRIBUTO, e.falhas())).build();
    }

    @ServerExceptionMapper
    Response dominio(ErroDeDominioException e) {
        return Response.status(e.status()).entity(corpo(TipoDeErro.DOMINIO, e.falhas())).build();
    }

    /** JSON malformado ou valor que não cabe no campo (enum desconhecido, número inválido): erro de atributo. */
    @ServerExceptionMapper
    Response jsonInvalido(JsonProcessingException e) {
        String campo = "corpo";
        if (e instanceof JsonMappingException m && !m.getPath().isEmpty()) {
            campo = m.getPath().stream().map(r -> r.getFieldName() != null ? r.getFieldName() : "[" + r.getIndex() + "]")
                    .reduce((a, b) -> b.startsWith("[") ? a + b : a + "." + b).orElse("corpo");
        }
        var falha = Falha.doCampo(ErroDeAtributo.FORMATO, campo, "valor não reconhecido");
        return Response.status(400).entity(corpo(TipoDeErro.ATRIBUTO, List.of(falha))).build();
    }

    /** O Quarkus tem um tratamento próprio para este tipo, mais específico; este o substitui no nosso formato. */
    @ServerExceptionMapper
    Response valorQueNaoCabe(com.fasterxml.jackson.databind.exc.MismatchedInputException e) {
        return jsonInvalido(e);
    }

    private CorpoDoErro corpo(TipoDeErro tipo, List<Falha> falhas) {
        return new CorpoDoErro(tipo, falhas.stream().map(f -> new FalhaNoCorpo(f.codigo(),
                catalogo.codigoCorporativo(f.codigo()), f.campo(), f.regra(), f.mensagem())).toList());
    }
}
