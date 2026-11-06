package br.com.financiamento.originacao.entrypoint;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static br.com.financiamento.DocumentosParaTeste.cpf;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.startsWith;

/** O fluxo comum de validação de ponta a ponta (ADR 0011): atributo 400, domínio 422, não encontrado 404. */
@QuarkusTest
class ValidacaoWebTest {

    long iniciar() {
        return given().contentType(ContentType.JSON).body(Map.of("tipoDeBem", "MOTO_USADA"))
                .post("/web/propostas").then().statusCode(201).extract().jsonPath().getLong("id");
    }

    @Test
    void atributosErradosVoltamTodosDeUmaVez() {
        long id = iniciar();
        var participante = new HashMap<String, Object>(Map.of("papel", "PROPONENTE", "forma", "PESSOA_FISICA",
                "documento", "111.111.111-11", "nome", "", "rendaComprovada", -10));
        given().contentType(ContentType.JSON).body(Map.of("participantes", List.of(participante)))
                .put("/web/propostas/" + id + "/participantes").then().statusCode(400)
                .body("tipo", equalTo("ATRIBUTO"))
                .body("falhas.campo", containsInAnyOrder("participantes[0].documento", "participantes[0].nome",
                        "participantes[0].rendaComprovada"))
                .body("falhas.codigo", containsInAnyOrder("ATR-0006", "ATR-0001", "ATR-0008"));
    }

    @Test
    void requisicaoBemFormadaQueARegraNaoDeixaE422ComCodigoERegra() {
        long id = iniciar();
        given().post("/web/propostas/" + id + "/cancelamento").then().statusCode(200);
        given().contentType(ContentType.JSON)
                .body(Map.of("entradaPropria", 7400, "prazoMeses", 36, "parcela", 900))
                .put("/web/propostas/" + id + "/condicoes").then().statusCode(422)
                .body("tipo", equalTo("DOMINIO"))
                .body("falhas[0].regra", equalTo("AGR-0001"))
                .body("falhas[0].codigo", startsWith("ORI-"));
    }

    @Test
    void propostaInexistenteE404DoCatalogo() {
        given().get("/web/propostas/999999").then().statusCode(404)
                .body("falhas[0].codigo", equalTo("ORI-0001"))
                .body("falhas[0].mensagem", equalTo("Proposta 999999 não encontrada."));
    }

    @Test
    void valorQueNaoCabeNoCampoEFalhaDeAtributo() {
        long id = iniciar();
        given().contentType(ContentType.JSON).body(Map.of("participantes", List.of(Map.of("papel", "GERENTE",
                        "forma", "PESSOA_FISICA", "documento", cpf(1), "nome", "Ana"))))
                .put("/web/propostas/" + id + "/participantes").then().statusCode(400)
                .body("falhas[0].codigo", equalTo("ATR-0003"))
                .body("falhas.campo", hasItem(startsWith("participantes")));
    }

    @Test
    void tipoDeBemComFormatoInvalidoNaoChegaAoDominio() {
        given().contentType(ContentType.JSON).body(Map.of("tipoDeBem", "carro-novo"))
                .post("/web/propostas").then().statusCode(400)
                .body("falhas.campo", contains("tipoDeBem"));
    }
}
