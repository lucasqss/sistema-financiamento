package br.com.financiamento.originacao.entrypoint;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static br.com.financiamento.DocumentosParaTeste.cnpj;
import static br.com.financiamento.DocumentosParaTeste.cpf;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Jornada da originação pela API web, no banco de verdade: iniciar, montar,
 * simular, submeter e cancelar, com os eventos indo para o outbox.
 */
@QuarkusTest
class PropostaWebResourceTest {

    static final String PROPONENTE = cpf(101);
    static final String VENDEDOR = cpf(102);

    @Inject
    EntityManager em;

    long iniciar(String tipo) {
        return given().contentType(ContentType.JSON).body(Map.of("tipoDeBem", tipo))
                .when().post("/web/propostas")
                .then().statusCode(201).body("estado", equalTo("RASCUNHO"))
                .extract().jsonPath().getLong("id");
    }

    void put(long id, String parte, Object corpo, int status) {
        given().contentType(ContentType.JSON).body(corpo).when().put("/web/propostas/" + id + "/" + parte)
                .then().statusCode(status);
    }

    Map<String, Object> proponentePf() {
        return Map.of("papel", "PROPONENTE", "forma", "PESSOA_FISICA", "documento", PROPONENTE,
                "nome", "Ana", "nascimento", "1990-05-20", "rendaComprovada", 20000);
    }

    Map<String, Object> bem(String identificacao) {
        return Map.of("identificacao", identificacao, "valorDeCompra", 20000, "documentoDoVendedor", VENDEDOR,
                "anoModelo", Year.now().getValue() - 2);
    }

    @Test
    void jornadaDaMotoUsadaAteASubmissao() {
        long id = iniciar("MOTO_USADA");
        put(id, "participantes", Map.of("participantes", List.of(proponentePf())), 200);
        put(id, "bem", bem("MOTO-" + UUID.randomUUID()), 200);

        // UC-0004: com entrada de 4.000 a cota de 70% estoura; com 7.400, enquadra.
        given().contentType(ContentType.JSON)
                .body(Map.of("entradaPropria", 4000, "prazoMeses", 36, "parcela", 900))
                .when().put("/web/propostas/" + id + "/condicoes")
                .then().statusCode(200)
                .body("enquadrada", equalTo(false))
                .body("pendenciasDeEnquadramento.regra", hasItem("RN-0006"))
                .body("condicoes.valorFinanciado", equalTo(16000.0f));
        put(id, "condicoes", Map.of("entradaPropria", 7400, "prazoMeses", 36, "parcela", 900), 200);

        // Sem documentos, não submete (RN-0009) e continua em rascunho.
        given().when().post("/web/propostas/" + id + "/submissao")
                .then().statusCode(422).body("violacoes.regra", hasItem("RN-0009"));

        for (var doc : List.of(Map.of("titular", "bem", "tipo", "CRLV"),
                Map.of("titular", PROPONENTE, "tipo", "identidade"),
                Map.of("titular", PROPONENTE, "tipo", "comprovante de renda"),
                Map.of("titular", PROPONENTE, "tipo", "comprovante de residência"))) {
            given().contentType(ContentType.JSON).body(doc).when().post("/web/propostas/" + id + "/documentos")
                    .then().statusCode(200);
        }

        given().when().post("/web/propostas/" + id + "/submissao")
                .then().statusCode(200).body("estado", equalTo("EM_ANALISE")).body("enquadrada", equalTo(true));

        // Em análise, não aceita alteração nem cancelamento (AGR-0001, CV-0001).
        put(id, "condicoes", Map.of("entradaPropria", 8000, "prazoMeses", 36, "parcela", 900), 422);
        given().when().post("/web/propostas/" + id + "/cancelamento")
                .then().statusCode(422).body("violacoes.regra", hasItem("CV-0001"));

        assertEquals(List.of("PropostaIniciada", "PropostaSubmetida"), eventosNoOutbox(id));
    }

    @Test
    void bemEmOutraPropostaAtivaERecusado() {
        var identificacao = "MOTO-" + UUID.randomUUID();
        long primeira = iniciar("MOTO_USADA");
        put(primeira, "bem", bem(identificacao), 200);

        long segunda = iniciar("MOTO_USADA");
        given().contentType(ContentType.JSON).body(bem(identificacao))
                .when().put("/web/propostas/" + segunda + "/bem")
                .then().statusCode(422).body("violacoes.regra", hasItem("RN-0004"));

        // Cancelada a primeira, o bem fica livre.
        given().when().post("/web/propostas/" + primeira + "/cancelamento").then().statusCode(200)
                .body("estado", equalTo("CANCELADA"));
        put(segunda, "bem", bem(identificacao), 200);
        assertEquals(List.of("PropostaIniciada", "PropostaCancelada"), eventosNoOutbox(primeira));
    }

    @Test
    void pessoaJuridicaNaoEProponenteDeVeiculoUsado() {
        long id = iniciar("CARRO_USADO");
        var pj = Map.of("papel", "PROPONENTE", "forma", "PESSOA_JURIDICA", "documento", cnpj(101),
                "nome", "Empresa", "rendaComprovada", 50000);
        given().contentType(ContentType.JSON).body(Map.of("participantes", List.of(pj)))
                .when().put("/web/propostas/" + id + "/participantes")
                .then().statusCode(422).body("violacoes.regra", hasItem("RN-0002"));
    }

    @Test
    void tipoDeBemForaDoModeloERecusado() {
        given().contentType(ContentType.JSON).body(Map.of("tipoDeBem", "BARCO_NOVO"))
                .when().post("/web/propostas")
                .then().statusCode(422).body("violacoes.regra", hasItem("DIM-0001"));
    }

    @Test
    void propostaInexistente() {
        given().when().get("/web/propostas/999999").then().statusCode(404);
    }

    @SuppressWarnings("unchecked")
    List<String> eventosNoOutbox(long propostaId) {
        return em.createNativeQuery("select evento from shared.outbox where agregado = 'Proposta' and agregado_id = ?1 order by id")
                .setParameter(1, String.valueOf(propostaId))
                .getResultList();
    }
}
