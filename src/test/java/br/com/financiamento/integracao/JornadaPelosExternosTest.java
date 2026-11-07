package br.com.financiamento.integracao;

import br.com.financiamento.ExternosNoWireMock;
import br.com.financiamento.TokensParaTeste;
import br.com.financiamento.shared.outbox.RelayParaTeste;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static br.com.financiamento.DocumentosParaTeste.cpf;
import static br.com.financiamento.JornadaParaTeste.submetidaComDocumentosAceitos;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * A jornada inteira com os 7 sistemas externos simulados no WireMock (os mesmos stubs do docker/compose.yml):
 * o relay do outbox leva cada pedido ao externo, e o externo devolve o resultado por webhook nos /retornos.
 * Nenhum retorno é lançado à mão: as pessoas só fazem o que lhes cabe (enviar, conferir, aceitar, assinar).
 */
@QuarkusTest
@QuarkusTestResource(value = ExternosNoWireMock.class, restrictToAnnotatedClass = true)
class JornadaPelosExternosTest {

    static final String ANA = cpf(901), VENDEDOR = cpf(902);

    @Inject
    EntityManager em;

    /** Isola a jornada: os pedidos que outros testes deixaram no outbox não vão aos externos. */
    @BeforeEach
    void outboxSoDestaJornada() {
        QuarkusTransaction.requiringNew().run(() -> em.createNativeQuery(
                "update shared.outbox set publicado_em = now() where publicado_em is null and desistido_em is null")
                .executeUpdate());
    }

    /** Roda o relay até a condição valer: cada webhook pode gerar novos pedidos no outbox. */
    void ateQue(String descricao, java.util.function.BooleanSupplier condicao) {
        await(descricao).atMost(Duration.ofSeconds(20)).pollInterval(Duration.ofMillis(250)).until(() -> {
            RelayParaTeste.enviarPendentes();
            return condicao.getAsBoolean();
        });
    }

    String estadoDaProposta(long id) {
        return given().get("/web/propostas/" + id).then().statusCode(200).extract().jsonPath().getString("estado");
    }

    String estadoDoContrato(long id) {
        return given().get("/web/contratos/" + id).then().statusCode(200).extract().jsonPath().getString("estado");
    }

    long aceitar(Map<String, Object> aceite) {
        return given().contentType(ContentType.JSON).body(aceite).post("/web/contratacoes").then().statusCode(201)
                .extract().jsonPath().getLong("id");
    }

    Long contratoDa(long contratacao) {
        var id = given().get("/web/contratacoes/" + contratacao).then().statusCode(200).extract().jsonPath().get("contratoId");
        return id == null ? null : ((Number) id).longValue();
    }

    void assinar(long contrato, String documento) {
        given().header("Authorization", "Bearer " + TokensParaTeste.signatario(documento)).contentType(ContentType.JSON)
                .body(Map.of("documento", documento)).post("/web/contratos/" + contrato + "/assinaturas").then().statusCode(200);
    }

    @Test
    void motoUsadaDaPropostaAoContratoFormalizado() {
        long proposta = submetidaComDocumentosAceitos("MOTO_USADA", ANA,
                Map.of("identificacao", "MOTO-" + UUID.randomUUID(), "valorDeCompra", 20000, "documentoDoVendedor", VENDEDOR,
                        "anoModelo", Year.now().getValue() - 2),
                Map.of("entradaPropria", 7400, "prazoMeses", 36, "parcela", 900),
                List.of("CRLV"), List.of("identidade", "comprovante de renda", "comprovante de residência"));

        // Bureau e avaliador recebem os pedidos e respondem: a proposta é aprovada (UC-0006).
        ateQue("proposta aprovada", () -> "APROVADA".equals(estadoDaProposta(proposta)));
        var wiremock = ExternosNoWireMock.servidor();
        wiremock.verify(postRequestedFor(urlPathEqualTo("/bureau/consultas"))
                .withRequestBody(matchingJsonPath("$.documentos[0]", equalTo(ANA)))
                .withRequestBody(matchingJsonPath("$.valorFinanciado", matching("12600(\\.0+)?"))));
        wiremock.verify(postRequestedFor(urlPathEqualTo("/avaliador/laudos"))
                .withRequestBody(matchingJsonPath("$.fonte", equalTo("tabela de referência"))));
        given().get("/web/propostas/" + proposta).then().body("avaliacao.valor", org.hamcrest.Matchers.equalTo(20000.0f));

        // A seguradora emite a apólice do seguro do bem: o contrato é emitido (UC-0014).
        long contratacao = aceitar(Map.of("propostaId", proposta));
        ateQue("contrato emitido", () -> contratoDa(contratacao) != null);
        long contrato = contratoDa(contratacao);
        wiremock.verify(postRequestedFor(urlPathEqualTo("/seguradora/apolices"))
                .withRequestBody(matchingJsonPath("$.contratacao", equalTo(String.valueOf(contratacao)))));

        // Assinado, o órgão de trânsito grava o gravame; com a garantia, o pagamento transfere ao vendedor.
        assinar(contrato, ANA);
        ateQue("contrato formalizado", () -> "FORMALIZADO".equals(estadoDoContrato(contrato)));
        wiremock.verify(postRequestedFor(urlPathEqualTo("/orgao-de-transito/gravames"))
                .withRequestBody(matchingJsonPath("$.protocolo", equalTo("GAR-" + contrato + "-1"))));
        wiremock.verify(postRequestedFor(urlPathEqualTo("/pagamento/transferencias"))
                .withRequestBody(matchingJsonPath("$.protocolo", equalTo("LIB-" + contrato + "-1")))
                .withRequestBody(matchingJsonPath("$.favorecido", equalTo(VENDEDOR))));
    }

    @Test
    void imovelComExigenciaNoCartorioEFgtsNaEntrada() {
        long proposta = submetidaComDocumentosAceitos("RESIDENCIAL_PRONTO", ANA,
                Map.of("identificacao", "MAT-EXIGENCIA-" + UUID.randomUUID(), "valorDeCompra", 500000,
                        "documentoDoVendedor", VENDEDOR),
                Map.of("entradaPropria", 50000, "entradaFgts", 50000, "prazoMeses", 360, "parcela", 4000),
                List.of("matrícula atualizada", "certidões do vendedor"),
                List.of("identidade", "comprovante de renda", "comprovante de residência", "estado civil"));
        ateQue("proposta aprovada", () -> "APROVADA".equals(estadoDaProposta(proposta)));

        // MIP e DFI: um pedido de apólice por seguro.
        long contratacao = aceitar(Map.of("propostaId", proposta));
        ateQue("contrato emitido", () -> contratoDa(contratacao) != null);
        long contrato = contratoDa(contratacao);
        assinar(contrato, ANA);
        assinar(contrato, VENDEDOR);

        // O cartório faz uma exigência na primeira tentativa (massa de teste); cumprida, registra (UC-0020).
        ateQue("exigência do cartório", () -> "EM_EXIGENCIA".equals(estadoDoContrato(contrato)));
        long registro = given().queryParam("contrato", contrato).get("/web/registros").then().extract().jsonPath().getLong("id");
        given().contentType(ContentType.JSON).body(Map.of()).post("/web/registros/" + registro + "/exigencia-cumprida")
                .then().statusCode(200);

        // Com a garantia, o FGTS libera a entrada e o pagamento transfere o financiado.
        ateQue("contrato formalizado", () -> "FORMALIZADO".equals(estadoDoContrato(contrato)));
        var wiremock = ExternosNoWireMock.servidor();
        wiremock.verify(2, postRequestedFor(urlPathEqualTo("/seguradora/apolices"))
                .withRequestBody(matchingJsonPath("$.contratacao", equalTo(String.valueOf(contratacao)))));
        wiremock.verify(postRequestedFor(urlPathEqualTo("/cartorio/registros"))
                .withRequestBody(matchingJsonPath("$.protocolo", equalTo("GAR-" + contrato + "-2"))));
        wiremock.verify(postRequestedFor(urlPathEqualTo("/fgts/saques"))
                .withRequestBody(matchingJsonPath("$.valor", matching("50000(\\.0+)?"))));
        given().queryParam("contrato", contrato).get("/web/desembolsos").then()
                .body("fgts", org.hamcrest.Matchers.equalTo("LIBERADO"));
    }

    @Test
    void bureauReprovaFinanciamentoAcimaDeUmMilhao() {
        long proposta = submetidaComDocumentosAceitos("RESIDENCIAL_PRONTO", ANA,
                Map.of("identificacao", "MAT-" + UUID.randomUUID(), "valorDeCompra", 2000000, "documentoDoVendedor", VENDEDOR),
                Map.of("entradaPropria", 400000, "prazoMeses", 360, "parcela", 6000),
                List.of("matrícula atualizada", "certidões do vendedor"),
                List.of("identidade", "comprovante de renda", "comprovante de residência", "estado civil"));
        ateQue("proposta reprovada", () -> "REPROVADA".equals(estadoDaProposta(proposta)));
        assertEquals("BUREAU", given().queryParam("proposta", proposta).get("/web/analises-de-credito").then()
                .extract().jsonPath().getString("[0].decididaPor"));
    }
}
