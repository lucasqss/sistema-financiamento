package br.com.financiamento.contratacao.entrypoint;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static br.com.financiamento.DocumentosParaTeste.cpf;
import static br.com.financiamento.JornadaParaTeste.motoUsadaAprovada;
import static br.com.financiamento.JornadaParaTeste.residencialProntoAprovado;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Da proposta aprovada ao contrato assinado: aceite das condições (UC-0014),
 * seguros pela seguradora, emissão, assinaturas (UC-0015) e desistência (UC-0016).
 */
@QuarkusTest
class ContratacaoWebTest {

    static final String ANA = cpf(301), VENDEDOR = cpf(302), CONJUGE = cpf(303);

    @Inject
    EntityManager em;

    long aceitar(long proposta, Map<String, Object> corpo) {
        return given().contentType(ContentType.JSON).body(corpo).post("/web/contratacoes")
                .then().statusCode(201).extract().jsonPath().getLong("id");
    }

    void apolice(long contratacao, String seguro, String apolice) {
        given().contentType(ContentType.JSON).body(Map.of("seguro", seguro, "apolice", apolice))
                .post("/retornos/seguradora/" + contratacao).then().statusCode(202);
    }

    long contratoDe(long contratacao) {
        return given().get("/web/contratacoes/" + contratacao).then().statusCode(200)
                .extract().jsonPath().getLong("contratoId");
    }

    void assinar(long contrato, String documento) {
        given().contentType(ContentType.JSON).body(Map.of("documento", documento))
                .post("/web/contratos/" + contrato + "/assinaturas").then().statusCode(200);
    }

    @Test
    void veiculoDoAceiteAoContratoAssinado() {
        long proposta = motoUsadaAprovada(ANA, VENDEDOR);
        long contratacao = aceitar(proposta, Map.of("propostaId", proposta));
        given().get("/web/contratacoes/" + contratacao).then()
                .body("seguros.nome", contains("seguro do bem"))
                .body("contratoId", nullValue());

        apolice(contratacao, "seguro do bem", "APOLICE-1");
        apolice(contratacao, "seguro do bem", "APOLICE-1"); // retorno repetido
        long contrato = contratoDe(contratacao);

        given().get("/web/propostas/" + proposta).then().body("estado", equalTo("CONTRATADA"));
        given().get("/web/contratos/" + contrato).then()
                .body("estado", equalTo("AGUARDANDO_ASSINATURA"))
                .body("signatarios.papel", contains("PROPONENTE"))
                .body("condicoes.valorFinanciado", equalTo(12600.0f));

        assinar(contrato, ANA);
        // Assinado, o contrato já segue para o registro da garantia (UC-0018), na mesma transação.
        given().get("/web/contratos/" + contrato).then().body("estado", equalTo("EM_REGISTRO"));

        assertEquals(List.of("CondicoesAceitas", "SegurosContratados"), eventos("Contratacao", contratacao));
        assertEquals(List.of("ContratoEmitido", "ContratoAssinado"), eventos("Contrato", contrato));
        assertEquals("PropostaAprovada", eventos("Proposta", proposta).get(eventos("Proposta", proposta).size() - 1),
                "ContratoEmitido é do contrato; a proposta só muda de estado");

        // Uma proposta gera no máximo um contrato.
        given().contentType(ContentType.JSON).body(Map.of("propostaId", proposta)).post("/web/contratacoes")
                .then().statusCode(422);
    }

    @Test
    void imovelExigeMipEDfiEAssinamVendedorEConjuge() {
        long proposta = residencialProntoAprovado(ANA, VENDEDOR);
        long contratacao = aceitar(proposta, Map.of("propostaId", proposta, "segurosOpcionais", List.of("prestamista"),
                "conjuges", List.of(Map.of("documento", CONJUGE, "nome", "Bruno"))));
        given().get("/web/contratacoes/" + contratacao).then()
                .body("seguros.nome", contains("MIP", "DFI", "prestamista"));

        apolice(contratacao, "MIP", "A-1");
        apolice(contratacao, "DFI", "A-2");
        given().get("/web/contratacoes/" + contratacao).then().body("contratoId", nullValue());
        apolice(contratacao, "prestamista", "A-3");
        long contrato = contratoDe(contratacao);

        given().get("/web/contratos/" + contrato).then()
                .body("signatarios.papel", containsInAnyOrder("PROPONENTE", "VENDEDOR", "CONJUGE"));
        assinar(contrato, ANA);
        assinar(contrato, VENDEDOR);
        given().get("/web/contratos/" + contrato).then().body("estado", equalTo("AGUARDANDO_ASSINATURA"));
        assinar(contrato, CONJUGE);
        // Assinado, o contrato já segue para o registro da garantia (UC-0018), na mesma transação.
        given().get("/web/contratos/" + contrato).then().body("estado", equalTo("EM_REGISTRO"));
    }

    @Test
    void desistenciaAntesDeAssinar() {
        long proposta = motoUsadaAprovada(ANA, VENDEDOR);
        long contratacao = aceitar(proposta, Map.of("propostaId", proposta));
        apolice(contratacao, "seguro do bem", "APOLICE-2");
        long contrato = contratoDe(contratacao);
        given().contentType(ContentType.JSON).body(Map.of("motivo", "desistência"))
                .post("/web/contratos/" + contrato + "/cancelamento")
                .then().statusCode(200).body("estado", equalTo("CANCELADO"));
        given().contentType(ContentType.JSON).body(Map.of("documento", ANA))
                .post("/web/contratos/" + contrato + "/assinaturas").then().statusCode(422);
    }

    @Test
    void propostaQueNaoEstaAprovadaNaoEContratada() {
        long proposta = given().contentType(ContentType.JSON).body(Map.of("tipoDeBem", "CARRO_NOVO"))
                .post("/web/propostas").then().extract().jsonPath().getLong("id");
        given().contentType(ContentType.JSON).body(Map.of("propostaId", proposta)).post("/web/contratacoes")
                .then().statusCode(422).body("impedimento", startsWith("A proposta está rascunho"));
    }

    @Test
    void conjugeNaoAssinaContratoDeVeiculo() {
        long proposta = motoUsadaAprovada(ANA, VENDEDOR);
        given().contentType(ContentType.JSON)
                .body(Map.of("propostaId", proposta, "conjuges", List.of(Map.of("documento", CONJUGE, "nome", "Bruno"))))
                .post("/web/contratacoes").then().statusCode(422).body("falhas[0].regra", equalTo("RN-0026"));
    }

    @SuppressWarnings("unchecked")
    List<String> eventos(String agregado, long id) {
        return em.createNativeQuery("select evento from shared.outbox where agregado = ?1 and agregado_id = ?2 order by id")
                .setParameter(1, agregado).setParameter(2, String.valueOf(id)).getResultList();
    }
}
