package br.com.financiamento;

import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.MultiPartSpecification;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

/** Envia e confere documentos pela API do módulo Documentos, para os testes de jornada. */
public final class EnvioDeDocumentosParaTeste {

    /** Um PDF mínimo: o começo "%PDF" é o que o envio confere. */
    public static final byte[] PDF = "%PDF-1.4\n% documento de teste\n".getBytes(StandardCharsets.US_ASCII);

    private EnvioDeDocumentosParaTeste() {
    }

    public static long enviar(long proposta, String titular, String tipo) {
        return given().multiPart(texto("propostaId", String.valueOf(proposta)))
                .multiPart(texto("titular", titular))
                .multiPart(texto("tipo", tipo))
                .multiPart("arquivo", "documento.pdf", PDF, "application/pdf")
                .post("/web/documentos").then().statusCode(201).extract().jsonPath().getLong("id");
    }

    /** Campo de texto do formulário em UTF-8, como o navegador envia ("comprovante de residência"). */
    public static MultiPartSpecification texto(String nome, String valor) {
        return new MultiPartSpecBuilder(valor).controlName(nome).charset(StandardCharsets.UTF_8).build();
    }

    public static void enviarTodos(long proposta, String proponente, List<String> doBem, List<String> doProponente) {
        doBem.forEach(t -> enviar(proposta, "bem", t));
        doProponente.forEach(t -> enviar(proposta, proponente, t));
    }

    /** UC-0024: o analista aceita todos os documentos ainda não conferidos da proposta. */
    public static void aceitarTodos(long proposta) {
        List<Integer> ids = given().queryParam("proposta", proposta).get("/web/documentos").then().statusCode(200)
                .extract().jsonPath().getList("findAll { it.situacao == 'ENVIADO' }.id");
        for (var id : ids) {
            given().contentType(ContentType.JSON).body(Map.of("aceito", true))
                    .post("/web/documentos/" + id + "/conferencia").then().statusCode(200);
        }
    }
}
