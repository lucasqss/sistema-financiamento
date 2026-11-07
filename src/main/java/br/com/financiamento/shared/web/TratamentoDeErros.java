package br.com.financiamento.shared.web;

import br.com.financiamento.shared.seguranca.ErroDeSeguranca;
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

    /** Sem token, token inválido, expirado ou de outro emissor (ADR 0012). */
    @ServerExceptionMapper({io.quarkus.security.UnauthorizedException.class, io.quarkus.security.AuthenticationFailedException.class})
    Response naoAutenticado() {
        return Response.status(401).header("WWW-Authenticate", "Bearer")
                .entity(corpo(TipoDeErro.DOMINIO, List.of(Falha.de(ErroDeSeguranca.NAO_AUTENTICADO)))).build();
    }

    /** Autenticado, mas o papel não permite a operação (ADR 0012). */
    @ServerExceptionMapper(io.quarkus.security.ForbiddenException.class)
    Response semPapel() {
        return Response.status(403).entity(corpo(TipoDeErro.DOMINIO, List.of(Falha.de(ErroDeSeguranca.SEM_PAPEL)))).build();
    }

    /**
     * Conflito de concorrência que sobrou depois das repetições (ADR 0013): 503 com Retry-After, para quem chamou
     * tentar de novo. Não é erro de negócio, e por isso não tem código do catálogo.
     */
    @ServerExceptionMapper({jakarta.persistence.OptimisticLockException.class, org.hibernate.StaleStateException.class})
    Response conflito() {
        return Response.status(503).header("Retry-After", "1").build();
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
