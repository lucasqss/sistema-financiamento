package br.com.financiamento;

import io.restassured.http.ContentType;

import java.time.LocalDate;
import java.time.Year;
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

    /** Residencial pronto de R$ 500.000 com R$ 50.000 do FGTS na entrada, aprovado. */
    public static long residencialComFgtsAprovado(String proponente, String vendedor) {
        return aprovada("RESIDENCIAL_PRONTO", proponente, vendedor,
                Map.of("identificacao", "MAT-" + UUID.randomUUID(), "valorDeCompra", 500000, "documentoDoVendedor", vendedor),
                Map.of("entradaPropria", 50000, "entradaFgts", 50000, "prazoMeses", 360, "parcela", 4000),
                List.of("matrícula atualizada", "certidões do vendedor"),
                List.of("identidade", "comprovante de renda", "comprovante de residência", "estado civil"), 500000);
    }

    /** Residencial na planta de R$ 500.000, entrada de R$ 100.000, aprovado. */
    public static long residencialNaPlantaAprovado(String proponente, String incorporadora) {
        return aprovada("RESIDENCIAL_NA_PLANTA", proponente, incorporadora,
                Map.of("identificacao", "MAT-" + UUID.randomUUID(), "valorDeCompra", 500000, "documentoDoVendedor", incorporadora),
                Map.of("entradaPropria", 100000, "prazoMeses", 360, "parcela", 4000),
                List.of("contrato com a incorporadora", "memorial de incorporação", "cronograma da obra"),
                List.of("identidade", "comprovante de renda", "comprovante de residência", "estado civil"), 500000);
    }

    /** Contrato assinado de uma proposta aprovada de imóvel (assinam proponente e vendedor). */
    public static long contratoDeImovelAssinado(long proposta, String proponente, String vendedor,
                                                java.time.LocalDate previsaoDaObra) {
        var aceite = new java.util.HashMap<String, Object>(Map.of("propostaId", proposta));
        if (previsaoDaObra != null) aceite.put("previsaoDeEntregaDaObra", previsaoDaObra.toString());
        return contratoAssinado(aceite, List.of("MIP", "DFI"), List.of(proponente, vendedor));
    }

    /** O órgão confirma o registro do bem do contrato: garantia constituída. */
    public static void garantiaConstituida(long contrato) {
        var registro = given().queryParam("contrato", contrato).get("/web/registros").then().statusCode(200)
                .extract().jsonPath();
        given().contentType(ContentType.JSON).body(Map.of("protocolo", registro.getString("protocolo"),
                        "resultado", "REGISTRADO", "identificacaoRegistrada", registro.getString("identificacaoDoBem")))
                .post("/retornos/orgao-de-registro").then().statusCode(202);
    }

    /** Moto usada aprovada, contratada e assinada: devolve o contrato. */
    public static long motoUsadaComContratoAssinado(String proponente, String vendedor) {
        long proposta = motoUsadaAprovada(proponente, vendedor);
        return contratoAssinado(Map.of("propostaId", proposta), List.of("seguro do bem"), List.of(proponente));
    }

    /** Residencial pronto aprovado, contratado e assinado (proponente e vendedor): devolve o contrato. */
    public static long residencialComContratoAssinado(String proponente, String vendedor) {
        long proposta = residencialProntoAprovado(proponente, vendedor);
        return contratoAssinado(Map.of("propostaId", proposta), List.of("MIP", "DFI"), List.of(proponente, vendedor));
    }

    private static long contratoAssinado(Map<String, Object> aceite, List<String> seguros, List<String> signatarios) {
        long contratacao = given().contentType(ContentType.JSON).body(aceite)
                .post("/web/contratacoes").then().statusCode(201).extract().jsonPath().getLong("id");
        for (var s : seguros) {
            given().contentType(ContentType.JSON).body(Map.of("seguro", s, "apolice", "AP-" + UUID.randomUUID()))
                    .post("/retornos/seguradora/" + contratacao).then().statusCode(202);
        }
        long contrato = given().get("/web/contratacoes/" + contratacao).then().extract().jsonPath().getLong("contratoId");
        for (var d : signatarios) {
            given().header("Authorization", "Bearer " + TokensParaTeste.signatario(d)).contentType(ContentType.JSON)
                    .body(Map.of("documento", d)).post("/web/contratos/" + contrato + "/assinaturas").then().statusCode(200);
        }
        return contrato;
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
        EnvioDeDocumentosParaTeste.enviarTodos(id, proponente, documentosDoBem, documentosDoProponente);
        given().post("/web/propostas/" + id + "/submissao").then().statusCode(200).body("estado", equalTo("EM_ANALISE"));
        EnvioDeDocumentosParaTeste.aceitarTodos(id); // UC-0024: a aprovação exige os documentos aceitos (RN-0019)

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
