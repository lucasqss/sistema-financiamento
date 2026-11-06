package br.com.financiamento.shared.seguranca;

import br.com.financiamento.EnvioDeDocumentosParaTeste;
import br.com.financiamento.JornadaParaTeste;
import br.com.financiamento.TokensParaTeste;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static br.com.financiamento.DocumentosParaTeste.cpf;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Autorização por dono (ADR 0012) em todas as operações das pessoas: proponente, coobrigado, vendedor,
 * correspondente e cônjuge só agem no que participam. Um estranho com todos esses papéis recebe 403
 * SEG-0003 em cada uma; os participantes, o analista e o engenheiro passam.
 */
@QuarkusTest
class DonoDosRecursosWebTest {

    static final String ANA = cpf(801), VENDEDOR = cpf(802), ESTRANHO = cpf(803);

    long proposta, contratacao, contrato, desembolso;

    @BeforeEach
    void contratoComGarantiaConstituida() {
        proposta = JornadaParaTeste.motoUsadaAprovada(ANA, VENDEDOR);
        contratacao = given().contentType(ContentType.JSON).body(Map.of("propostaId", proposta))
                .post("/web/contratacoes").then().statusCode(201).extract().jsonPath().getLong("id");
        given().contentType(ContentType.JSON).body(Map.of("seguro", "seguro do bem", "apolice", "AP-" + UUID.randomUUID()))
                .post("/retornos/seguradora/" + contratacao).then().statusCode(202);
        contrato = given().get("/web/contratacoes/" + contratacao).then().extract().jsonPath().getLong("contratoId");
        como(TokensParaTeste.signatario(ANA)).contentType(ContentType.JSON).body(Map.of("documento", ANA))
                .post("/web/contratos/" + contrato + "/assinaturas").then().statusCode(200);
        JornadaParaTeste.garantiaConstituida(contrato);
        desembolso = given().queryParam("contrato", contrato).get("/web/desembolsos").then().statusCode(200)
                .extract().jsonPath().getLong("id");
    }

    static RequestSpecification como(String token) {
        return given().header("Authorization", "Bearer " + token);
    }

    record Chamada(String descricao, Function<RequestSpecification, io.restassured.response.Response> fazer) {
    }

    static Chamada json(String metodo, String caminho, Object corpo) {
        return new Chamada(metodo + " " + caminho, s -> s.contentType(ContentType.JSON).body(corpo).request(metodo, caminho));
    }

    static Chamada sem(String metodo, String caminho) {
        return new Chamada(metodo + " " + caminho, s -> s.request(metodo, caminho));
    }

    List<Chamada> operacoesDasPessoas() {
        var p = "/web/propostas/" + proposta;
        var c = "/web/contratos/" + contrato;
        return List.of(
                sem("GET", p),
                json("PUT", p + "/participantes", Map.of("participantes", List.of(Map.of("papel", "PROPONENTE",
                        "forma", "PESSOA_FISICA", "documento", ESTRANHO, "nome", "Estranho", "nascimento", "1990-05-20",
                        "rendaComprovada", 20000)))),
                json("PUT", p + "/bem", Map.of("identificacao", "MOTO-X", "valorDeCompra", 20000, "documentoDoVendedor", ESTRANHO)),
                json("PUT", p + "/condicoes", Map.of("entradaPropria", 7400, "prazoMeses", 36, "parcela", 900)),
                sem("POST", p + "/submissao"),
                sem("POST", p + "/cancelamento"),
                sem("POST", p + "/contraproposta/aceite"),
                sem("POST", p + "/contraproposta/recusa"),
                sem("POST", p + "/pendencia/resolucao"),
                new Chamada("POST /web/documentos", s -> s
                        .multiPart(EnvioDeDocumentosParaTeste.texto("propostaId", String.valueOf(proposta)))
                        .multiPart(EnvioDeDocumentosParaTeste.texto("titular", "bem"))
                        .multiPart(EnvioDeDocumentosParaTeste.texto("tipo", "CRLV"))
                        .multiPart("arquivo", "crlv.pdf", EnvioDeDocumentosParaTeste.PDF, "application/pdf")
                        .post("/web/documentos")),
                json("POST", "/web/contratacoes", Map.of("propostaId", proposta)),
                sem("GET", "/web/contratacoes/" + contratacao),
                sem("GET", c),
                json("POST", c + "/cancelamento", Map.of("motivo", "desistência")),
                json("POST", c + "/prorrogacoes", Map.of("novaData", LocalDate.now().plusYears(3).toString())),
                sem("POST", "/web/desembolsos/" + desembolso + "/entrada-recomposta"));
    }

    @Test
    void estranhoComTodosOsPapeisDePessoaNaoAgeNoQueNaoParticipa() {
        var token = TokensParaTeste.de(ESTRANHO, "proponente", "coobrigado", "vendedor", "correspondente", "conjuge");
        var erradas = new ArrayList<String>();
        for (var chamada : operacoesDasPessoas()) {
            var r = chamada.fazer().apply(como(token));
            var codigo = r.jsonPath().getString("falhas[0].codigo");
            if (r.statusCode() != 403 || !"SEG-0003".equals(codigo)) {
                erradas.add(chamada.descricao() + " → " + r.statusCode() + " " + codigo);
            }
        }
        assertTrue(erradas.isEmpty(), "Deveriam ser 403 SEG-0003: " + erradas);
    }

    @Test
    void participantesAnalistaEEngenheiroVeem() {
        var participante = TokensParaTeste.de(ANA, "proponente");
        como(participante).get("/web/propostas/" + proposta).then().statusCode(200);
        como(participante).get("/web/contratacoes/" + contratacao).then().statusCode(200);
        como(participante).get("/web/contratos/" + contrato).then().statusCode(200).body("estado", equalTo("GARANTIA_CONSTITUIDA"));
        como(TokensParaTeste.de(VENDEDOR, "vendedor")).get("/web/contratos/" + contrato).then().statusCode(200);
        var analista = TokensParaTeste.de(cpf(804), "analista");
        como(analista).get("/web/propostas/" + proposta).then().statusCode(200);
        como(analista).get("/web/contratos/" + contrato).then().statusCode(200);
        como(TokensParaTeste.de(cpf(805), "engenheiro")).queryParam("contrato", contrato).get("/web/medicoes")
                .then().statusCode(200);
    }

    /** Quem não é signatário exigido não assina, nem com o próprio documento (RN-0026). */
    @Test
    void estranhoNaoAssinaOContrato() {
        como(TokensParaTeste.signatario(ESTRANHO)).contentType(ContentType.JSON).body(Map.of("documento", ESTRANHO))
                .post("/web/contratos/" + contrato + "/assinaturas").then().statusCode(403)
                .body("falhas[0].codigo", equalTo("SEG-0003"));
    }
}
