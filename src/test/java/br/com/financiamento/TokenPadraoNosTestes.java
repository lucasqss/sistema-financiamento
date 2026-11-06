package br.com.financiamento;

import io.quarkus.test.junit.callback.QuarkusTestBeforeEachCallback;
import io.quarkus.test.junit.callback.QuarkusTestMethodContext;
import io.restassured.RestAssured;
import io.restassured.filter.Filter;

import java.util.List;

/**
 * Os testes de jornada falam com a API como um operador com todos os papéis (ADR 0012), para focar no negócio.
 * Quem quer testar o acesso marca a classe com {@link SemTokenPadrao} e escolhe o token de cada chamada.
 * Um Authorization informado na chamada prevalece sobre o padrão.
 */
public class TokenPadraoNosTestes implements QuarkusTestBeforeEachCallback {

    private static final Filter TOKEN_DO_OPERADOR = (requisicao, resposta, contexto) -> {
        if (!requisicao.getHeaders().hasHeaderWithName("Authorization")) {
            requisicao.header("Authorization", "Bearer " + TokensParaTeste.operador());
        }
        return contexto.next(requisicao, resposta);
    };

    @Override
    public void beforeEach(QuarkusTestMethodContext context) {
        boolean sem = context.getTestInstance().getClass().isAnnotationPresent(SemTokenPadrao.class);
        RestAssured.replaceFiltersWith(sem ? List.of() : List.of(TOKEN_DO_OPERADOR));
    }
}
