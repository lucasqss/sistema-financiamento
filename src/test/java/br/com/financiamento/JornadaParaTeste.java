package br.com.financiamento;

import io.restassured.http.ContentType;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

/** Leva propostas pela API até a aprovação, para os testes das etapas seguintes. */
public final class JornadaParaTeste {

    private JornadaParaTeste() {
    }

    /** Moto usada de R$ 20.000, entrada de R$ 7.400, aprovada. */
    public static long motoUsadaAprovada(String proponente, String vendedor) {
        return aprovada("MOTO_USADA", proponente, vendedor,
                Map.of("identificacao", "MOTO-" + UUID.randomUUID(), "valorDeCompra", 20000,
                        "documentoDoVendedor", vendedor, "anoModelo", Year.now().getValue() - 2),
                Map.of("entradaPropria", 7400, "prazoMeses", 36, "parcela", 900),
                List.of("CRLV"), List.of("identidade", "comprovante de renda", "comprovante de residência"), 20000);
    }

    /** Imóvel residencial pronto de R$ 500.000, entrada de R$ 100.000, aprovado. */
    public static long residencialProntoAprovado(String proponente, String vendedor) {
        return aprovada("RESIDENCIAL_PRONTO", proponente, vendedor,
                Map.of("identificacao", "MAT-" + UUID.randomUUID(), "valorDeCompra", 500000, "documentoDoVendedor", vendedor),
                Map.of("entradaPropria", 100000, "prazoMeses", 360, "parcela", 4000),
                List.of("matrícula atualizada", "certidões do vendedor"),
                List.of("identidade", "comprovante de renda", "comprovante de residência", "estado civil"), 500000);
    }

    private static long aprovada(String tipo, String proponente, String vendedor, Map<String, Object> bem,
                                 Map<String, Object> condicoes, List<String> documentosDoBem,
                                 List<String> documentosDoProponente, int valorDeAvaliacao) {
        long id = given().contentType(ContentType.JSON).body(Map.of("tipoDeBem", tipo))
                .post("/web/propostas").then().statusCode(201).extract().jsonPath().getLong("id");
        put(id, "participantes", Map.of("participantes", List.of(Map.of("papel", "PROPONENTE", "forma", "PESSOA_FISICA",
                "documento", proponente, "nome", "Ana", "nascimento", "1990-05-20", "rendaComprovada", 20000))));
        put(id, "bem", bem);
        put(id, "condicoes", condicoes);
        var documentos = new ArrayList<Map<String, String>>();
        documentosDoBem.forEach(d -> documentos.add(Map.of("titular", "bem", "tipo", d)));
        documentosDoProponente.forEach(d -> documentos.add(Map.of("titular", proponente, "tipo", d)));
        for (var doc : documentos) {
            given().contentType(ContentType.JSON).body(doc).post("/web/propostas/" + id + "/documentos").then().statusCode(200);
        }
        given().post("/web/propostas/" + id + "/submissao").then().statusCode(200).body("estado", equalTo("EM_ANALISE"));

        long avaliacao = given().queryParam("proposta", id).get("/web/avaliacoes").then().extract().jsonPath().getLong("[0].id");
        given().contentType(ContentType.JSON)
                .body(Map.of("aceitoComoGarantia", true, "valor", valorDeAvaliacao, "avaliadaEm", LocalDate.now().toString()))
                .post("/retornos/avaliador/" + avaliacao).then().statusCode(202);
        long analise = given().queryParam("proposta", id).get("/web/analises-de-credito").then().extract().jsonPath().getLong("[0].id");
        given().contentType(ContentType.JSON).body(Map.of("resultado", "APROVADO"))
                .post("/retornos/bureau-de-credito/" + analise).then().statusCode(202);
        given().get("/web/propostas/" + id).then().body("estado", equalTo("APROVADA"));
        return id;
    }

    private static void put(long id, String parte, Object corpo) {
        given().contentType(ContentType.JSON).body(corpo).put("/web/propostas/" + id + "/" + parte).then().statusCode(200);
    }
}
