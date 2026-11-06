package br.com.financiamento.documentos.entrypoint;

import br.com.financiamento.EnvioDeDocumentosParaTeste;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.Year;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static br.com.financiamento.DocumentosParaTeste.cpf;
import static br.com.financiamento.EnvioDeDocumentosParaTeste.PDF;
import static br.com.financiamento.EnvioDeDocumentosParaTeste.enviar;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * A análise documental do MVP: envio dos arquivos, conferência depois da
 * submissão (UC-0024), recusa que dá pendência, reenvio e aprovação só com os
 * documentos aceitos (RN-0019).
 */
@QuarkusTest
class DocumentosWebTest {

    static final String ANA = cpf(601), VENDEDOR = cpf(602);
    static final List<String> DO_PROPONENTE = List.of("identidade", "comprovante de renda", "comprovante de residência");

    @Inject
    EntityManager em;

    /** Moto usada montada, com os documentos enviados, ainda em rascunho. */
    long montada() {
        long id = given().contentType(ContentType.JSON).body(Map.of("tipoDeBem", "MOTO_USADA"))
                .post("/web/propostas").then().statusCode(201).extract().jsonPath().getLong("id");
        put(id, "participantes", Map.of("participantes", List.of(Map.of("papel", "PROPONENTE", "forma", "PESSOA_FISICA",
                "documento", ANA, "nome", "Ana", "nascimento", "1990-05-20", "rendaComprovada", 20000))));
        put(id, "bem", Map.of("identificacao", "MOTO-" + UUID.randomUUID(), "valorDeCompra", 20000,
                "documentoDoVendedor", VENDEDOR, "anoModelo", Year.now().getValue() - 2));
        put(id, "condicoes", Map.of("entradaPropria", 7400, "prazoMeses", 36, "parcela", 900));
        EnvioDeDocumentosParaTeste.enviarTodos(id, ANA, List.of("CRLV"), DO_PROPONENTE);
        return id;
    }

    void put(long id, String parte, Object corpo) {
        given().contentType(ContentType.JSON).body(corpo).put("/web/propostas/" + id + "/" + parte).then().statusCode(200);
    }

    long documento(long proposta, String tipo) {
        return given().queryParam("proposta", proposta).get("/web/documentos").then().statusCode(200).extract().jsonPath()
                .getLong("findAll { it.tipo == '" + tipo + "' && it.situacao != 'SUBSTITUIDO' }[0].id");
    }

    void conferir(long documento, Map<String, Object> corpo, int status) {
        given().contentType(ContentType.JSON).body(corpo).post("/web/documentos/" + documento + "/conferencia")
                .then().statusCode(status);
    }

    String estado(long proposta) {
        return given().get("/web/propostas/" + proposta).then().extract().jsonPath().getString("estado");
    }

    @Test
    void recusaDaPendenciaEReenvioLevaAteAAprovacao() throws Exception {
        long id = montada();
        long crlv = documento(id, "CRLV");
        conferir(crlv, Map.of("aceito", true), 422); // UC-0024: só depois da submissão

        given().post("/web/propostas/" + id + "/submissao").then().statusCode(200);
        long analise = given().queryParam("proposta", id).get("/web/analises-de-credito").then().extract().jsonPath().getLong("[0].id");
        long avaliacao = given().queryParam("proposta", id).get("/web/avaliacoes").then().extract().jsonPath().getLong("[0].id");
        given().contentType(ContentType.JSON).body(Map.of("resultado", "APROVADO"))
                .post("/retornos/bureau-de-credito/" + analise).then().statusCode(202);
        given().contentType(ContentType.JSON)
                .body(Map.of("aceitoComoGarantia", true, "valor", 20000, "avaliadaEm", LocalDate.now().toString()))
                .post("/retornos/avaliador/" + avaliacao).then().statusCode(202);
        assertEquals("EM_ANALISE", estado(id), "crédito e avaliação prontos, mas os documentos não foram conferidos");

        // O arquivo guardado é o enviado.
        byte[] baixado = given().get("/web/documentos/" + crlv + "/arquivo").then().statusCode(200)
                .extract().asByteArray();
        assertArrayEquals(PDF, baixado);
        assertEquals(HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(PDF)),
                given().queryParam("proposta", id).get("/web/documentos").then().extract().jsonPath()
                        .getString("find { it.id == " + crlv + " }.sha256"));

        conferir(crlv, Map.of("aceito", false, "motivo", "ILEGIVEL", "observacao", "foto tremida"), 200);
        given().get("/web/propostas/" + id).then()
                .body("estado", equalTo("COM_PENDENCIA"))
                .body("pendencia", org.hamcrest.Matchers.containsString("ilegível"));
        given().post("/web/propostas/" + id + "/pendencia/resolucao").then().statusCode(422)
                .body("falhas.regra", hasItem("RN-0009"));

        long reenviado = enviar(id, "bem", "CRLV");
        given().queryParam("proposta", id).get("/web/documentos").then()
                .body("find { it.id == " + crlv + " }.situacao", equalTo("SUBSTITUIDO"))
                .body("find { it.id == " + crlv + " }.motivo", equalTo("ilegível: foto tremida"));
        given().post("/web/propostas/" + id + "/pendencia/resolucao").then().statusCode(200)
                .body("estado", equalTo("EM_ANALISE"));

        conferir(reenviado, Map.of("aceito", true), 200);
        conferir(reenviado, Map.of("aceito", true), 200); // repetida, sem efeito
        for (var tipo : DO_PROPONENTE) conferir(documento(id, tipo), Map.of("aceito", true), 200);
        assertEquals("APROVADA", estado(id));

        assertEquals(List.of("DocumentoRecusado"), eventos(crlv));
        assertEquals(List.of("DocumentoAceito"), eventos(reenviado));
    }

    @Test
    void envioInvalidoERecusado() {
        long id = montada();
        given().multiPart("propostaId", String.valueOf(id)).multiPart("titular", "bem").multiPart("tipo", "CRLV")
                .multiPart("arquivo", "crlv.txt", "texto".getBytes(), "text/plain")
                .post("/web/documentos").then().statusCode(400).body("falhas[0].campo", equalTo("arquivo.tipo"));
        given().multiPart("propostaId", String.valueOf(id)).multiPart("titular", cpf(699)).multiPart("tipo", "identidade")
                .multiPart("arquivo", "rg.pdf", PDF, "application/pdf")
                .post("/web/documentos").then().statusCode(422).body("falhas[0].regra", equalTo("RN-0009"));
    }

    @Test
    void naoEnviaDocumentoComAPropostaEmAnalise() {
        long id = montada();
        given().post("/web/propostas/" + id + "/submissao").then().statusCode(200);
        given().multiPart("propostaId", String.valueOf(id)).multiPart("titular", "bem").multiPart("tipo", "CRLV")
                .multiPart("arquivo", "crlv.pdf", PDF, "application/pdf")
                .post("/web/documentos").then().statusCode(422).body("falhas[0].regra", equalTo("AGR-0001"));
    }

    @Test
    void filaDoAnalista() {
        long id = montada();
        given().get("/web/documentos").then().statusCode(200)
                .body("findAll { it.propostaId == " + id + " }.size()", equalTo(4));
    }

    @SuppressWarnings("unchecked")
    List<String> eventos(long documento) {
        return em.createNativeQuery("select evento from shared.outbox where agregado = 'Documento' and agregado_id = ?1 order by id")
                .setParameter(1, String.valueOf(documento)).getResultList();
    }
}
