package br.com.financiamento.shared.seguranca;

import br.com.financiamento.EnvioDeDocumentosParaTeste;
import br.com.financiamento.SemTokenPadrao;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static br.com.financiamento.DocumentosParaTeste.cpf;
import static br.com.financiamento.TokensParaTeste.de;
import static br.com.financiamento.TokensParaTeste.deOutroEmissor;
import static br.com.financiamento.TokensParaTeste.expirado;
import static br.com.financiamento.TokensParaTeste.sistema;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

/**
 * Autenticação delegada e autorização por papel e dono (ADR 0012), de ponta a ponta, com tokens do emissor de teste.
 */
@QuarkusTest
@SemTokenPadrao
class SegurancaWebTest {

    static final String ANA = cpf(701), BRUNO = cpf(702), VENDEDOR = cpf(703), ANALISTA = cpf(704);

    RequestSpecification como(String token) {
        return given().header("Authorization", "Bearer " + token);
    }

    long propostaDe(String proponente) {
        long id = como(de(proponente, "proponente")).contentType(ContentType.JSON).body(Map.of("tipoDeBem", "MOTO_USADA"))
                .post("/web/propostas").then().statusCode(201).extract().jsonPath().getLong("id");
        como(de(proponente, "proponente")).contentType(ContentType.JSON)
                .body(Map.of("participantes", List.of(Map.of("papel", "PROPONENTE", "forma", "PESSOA_FISICA",
                        "documento", proponente, "nome", "Titular", "nascimento", "1990-05-20", "rendaComprovada", 20000))))
                .put("/web/propostas/" + id + "/participantes").then().statusCode(200);
        return id;
    }

    @Test
    void semTokenE401DoCatalogo() {
        given().get("/web/propostas/1").then().statusCode(401)
                .body("falhas[0].codigo", equalTo("SEG-0001"));
    }

    @Test
    void tokenDeOutroEmissorOuExpiradoE401() {
        como(deOutroEmissor(ANA, "proponente")).get("/web/propostas/1").then().statusCode(401);
        como(expirado(ANA, "proponente")).get("/web/propostas/1").then().statusCode(401);
        como("isto-nao-e-um-jwt").get("/web/propostas/1").then().statusCode(401);
    }

    @Test
    void papelErradoE403DoCatalogo() {
        como(de(ANA, "proponente")).get("/web/analises-de-credito?proposta=1").then().statusCode(403)
                .body("falhas[0].codigo", equalTo("SEG-0002"));
        como(de(ANA, "engenheiro")).contentType(ContentType.JSON).body(Map.of("tipoDeBem", "MOTO_USADA"))
                .post("/web/propostas").then().statusCode(403);
    }

    @Test
    void proponenteSoVeEAlteraAsPropriasPropostas() {
        long daAna = propostaDe(ANA);
        como(de(ANA, "proponente")).get("/web/propostas/" + daAna).then().statusCode(200);

        como(de(BRUNO, "proponente")).get("/web/propostas/" + daAna).then().statusCode(403)
                .body("falhas[0].codigo", equalTo("SEG-0003"));
        como(de(BRUNO, "proponente")).contentType(ContentType.JSON)
                .body(Map.of("entradaPropria", 1, "prazoMeses", 36, "parcela", 900))
                .put("/web/propostas/" + daAna + "/condicoes").then().statusCode(403);
        // Nem enviando documento para a proposta alheia.
        given().header("Authorization", "Bearer " + de(BRUNO, "proponente"))
                .multiPart(EnvioDeDocumentosParaTeste.texto("propostaId", String.valueOf(daAna)))
                .multiPart(EnvioDeDocumentosParaTeste.texto("titular", "bem"))
                .multiPart(EnvioDeDocumentosParaTeste.texto("tipo", "CRLV"))
                .multiPart("arquivo", "crlv.pdf", EnvioDeDocumentosParaTeste.PDF, "application/pdf")
                .post("/web/documentos").then().statusCode(403);

        // O vendedor informado no bem passa a participar; o analista vê todas.
        como(de(ANA, "proponente")).contentType(ContentType.JSON)
                .body(Map.of("identificacao", "MOTO-" + UUID.randomUUID(), "valorDeCompra", 20000,
                        "documentoDoVendedor", VENDEDOR, "anoModelo", Year.now().getValue() - 2))
                .put("/web/propostas/" + daAna + "/bem").then().statusCode(200);
        como(de(VENDEDOR, "vendedor")).get("/web/propostas/" + daAna).then().statusCode(200);
        como(de(ANALISTA, "analista")).get("/web/propostas/" + daAna).then().statusCode(200);
    }

    @Test
    void retornoSoDoSistemaDono() {
        como(sistema("sistema-avaliador")).contentType(ContentType.JSON).body(Map.of("resultado", "APROVADO"))
                .post("/retornos/bureau-de-credito/1").then().statusCode(403);
        como(de(ANALISTA, "analista")).contentType(ContentType.JSON).body(Map.of("resultado", "APROVADO"))
                .post("/retornos/bureau-de-credito/1").then().statusCode(403);
        como(sistema("sistema-bureau")).contentType(ContentType.JSON).body(Map.of("resultado", "APROVADO"))
                .post("/retornos/bureau-de-credito/999999").then().statusCode(404);
    }

    @Test
    void cadaSignatarioAssinaPorSi() {
        como(de(ANA, "proponente")).contentType(ContentType.JSON).body(Map.of("documento", BRUNO))
                .post("/web/contratos/1/assinaturas").then().statusCode(403)
                .body("falhas[0].codigo", equalTo("SEG-0004"));
    }
}
