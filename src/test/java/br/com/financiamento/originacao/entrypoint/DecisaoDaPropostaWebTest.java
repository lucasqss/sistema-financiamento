package br.com.financiamento.originacao.entrypoint;

import br.com.financiamento.originacao.aplicacao.VerificarPrazosService;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static br.com.financiamento.DocumentosParaTeste.cpf;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * A decisão da proposta pelos três módulos: a submissão pede crédito e
 * avaliação, os retornos chegam pelas portas de entrada de cada módulo e a
 * Originação decide (UC-0006), com contraproposta (UC-0009) e prazos (UC-0007).
 */
@QuarkusTest
class DecisaoDaPropostaWebTest {

    static final String PROPONENTE = cpf(201);
    static final String VENDEDOR = cpf(202);

    @Inject
    EntityManager em;

    @Inject
    VerificarPrazosService prazos;

    /** Moto usada de R$ 20.000 com entrada de R$ 7.400, montada e submetida. */
    long submetida() {
        long id = given().contentType(ContentType.JSON).body(Map.of("tipoDeBem", "MOTO_USADA"))
                .post("/web/propostas").then().statusCode(201).extract().jsonPath().getLong("id");
        put(id, "participantes", Map.of("participantes", List.of(Map.of("papel", "PROPONENTE", "forma", "PESSOA_FISICA",
                "documento", PROPONENTE, "nome", "Ana", "nascimento", "1990-05-20", "rendaComprovada", 20000))));
        put(id, "bem", Map.of("identificacao", "MOTO-" + UUID.randomUUID(), "valorDeCompra", 20000,
                "documentoDoVendedor", VENDEDOR, "anoModelo", Year.now().getValue() - 2));
        put(id, "condicoes", Map.of("entradaPropria", 7400, "prazoMeses", 36, "parcela", 900));
        for (var doc : List.of(Map.of("titular", "bem", "tipo", "CRLV"),
                Map.of("titular", PROPONENTE, "tipo", "identidade"),
                Map.of("titular", PROPONENTE, "tipo", "comprovante de renda"),
                Map.of("titular", PROPONENTE, "tipo", "comprovante de residência"))) {
            given().contentType(ContentType.JSON).body(doc).post("/web/propostas/" + id + "/documentos").then().statusCode(200);
        }
        given().post("/web/propostas/" + id + "/submissao").then().statusCode(200).body("estado", equalTo("EM_ANALISE"));
        return id;
    }

    void put(long id, String parte, Object corpo) {
        given().contentType(ContentType.JSON).body(corpo).put("/web/propostas/" + id + "/" + parte).then().statusCode(200);
    }

    long analiseDe(long proposta, int indice) {
        return given().queryParam("proposta", proposta).get("/web/analises-de-credito")
                .then().statusCode(200).extract().jsonPath().getLong("[" + indice + "].id");
    }

    long avaliacaoDe(long proposta) {
        return given().queryParam("proposta", proposta).get("/web/avaliacoes")
                .then().statusCode(200).body("[0].fonte", equalTo("tabela de referência"))
                .extract().jsonPath().getLong("[0].id");
    }

    void laudo(long avaliacao, int valor) {
        given().contentType(ContentType.JSON)
                .body(Map.of("aceitoComoGarantia", true, "valor", valor, "avaliadaEm", LocalDate.now().toString()))
                .post("/retornos/avaliador/" + avaliacao).then().statusCode(202);
    }

    void bureau(long analise, Map<String, Object> retorno, int status) {
        given().contentType(ContentType.JSON).body(retorno).post("/retornos/bureau-de-credito/" + analise)
                .then().statusCode(status);
    }

    String estado(long proposta) {
        return given().get("/web/propostas/" + proposta).then().statusCode(200).extract().jsonPath().getString("estado");
    }

    @Test
    void creditoEAvaliacaoAprovamAProposta() {
        long id = submetida();
        long analise = analiseDe(id, 0);
        long avaliacao = avaliacaoDe(id);

        laudo(avaliacao, 20000);
        assertEquals("EM_ANALISE", estado(id), "falta o crédito");
        bureau(analise, Map.of("resultado", "APROVADO"), 202);

        given().get("/web/propostas/" + id).then().statusCode(200)
                .body("estado", equalTo("APROVADA"))
                .body("aprovacaoValidaAte", equalTo(LocalDate.now().plusDays(30).toString()))
                .body("avaliacao.valor", equalTo(20000.0f));

        // Retorno repetido não tem novo efeito; uma decisão diferente é recusada.
        bureau(analise, Map.of("resultado", "APROVADO"), 202);
        bureau(analise, Map.of("resultado", "REPROVADO"), 422);

        assertEquals(List.of("PropostaIniciada", "PropostaSubmetida", "PropostaAprovada"), eventos("Proposta", id));
        assertEquals(List.of("AnaliseCreditoSolicitada", "CreditoAprovado"), eventos("AnaliseDeCredito", analise));
        assertEquals(List.of("AvaliacaoSolicitada", "BemAvaliado"), eventos("Avaliacao", avaliacao));

        // UC-0007: sem contrato em 30 dias, expira.
        prazos.executar(LocalDate.now().plusDays(31));
        assertEquals("EXPIRADA", estado(id));
    }

    @Test
    void creditoReprovadoPeloAnalistaReprovaAProposta() {
        long id = submetida();
        given().contentType(ContentType.JSON).body(Map.of("resultado", "REPROVADO"))
                .post("/web/analises-de-credito/" + analiseDe(id, 0) + "/decisao")
                .then().statusCode(200).body("decididaPor", equalTo("ANALISTA"));
        assertEquals("REPROVADA", estado(id));
    }

    @Test
    void ressalvaViraContrapropostaQueAceitaAprova() {
        long id = submetida();
        laudo(avaliacaoDe(id), 20000);
        bureau(analiseDe(id, 0), Map.of("resultado", "APROVADO_COM_RESSALVA", "ressalva", "entrada maior",
                "sugeridas", Map.of("entradaPropria", 9000, "entradaFgts", 0, "prazoMeses", 36, "parcela", 800)), 202);

        given().get("/web/propostas/" + id).then()
                .body("estado", equalTo("EM_CONTRAPROPOSTA"))
                .body("contraproposta.validaAte", equalTo(LocalDate.now().plusDays(5).toString()));

        given().post("/web/propostas/" + id + "/contraproposta/aceite").then().statusCode(200)
                .body("estado", equalTo("APROVADA"))
                .body("condicoes.versao", equalTo(2))
                .body("condicoes.entradaPropria", equalTo(9000.0f));
    }

    @Test
    void cotaEstouradaNaAvaliacaoDaPendenciaQueSeResolveComNovaEntradaENovoCredito() {
        long id = submetida();
        bureau(analiseDe(id, 0), Map.of("resultado", "APROVADO"), 202);
        laudo(avaliacaoDe(id), 15000);
        given().get("/web/propostas/" + id).then()
                .body("estado", equalTo("COM_PENDENCIA"))
                .body("pendenciasDeEnquadramento.regra", hasItem("RN-0006"));

        given().post("/web/propostas/" + id + "/pendencia/resolucao").then().statusCode(422);
        put(id, "condicoes", Map.of("entradaPropria", 9500, "prazoMeses", 36, "parcela", 700));
        given().post("/web/propostas/" + id + "/pendencia/resolucao").then().statusCode(200)
                .body("estado", equalTo("EM_ANALISE"));

        // Condições novas: o crédito analisa a versão 2.
        bureau(analiseDe(id, 1), Map.of("resultado", "APROVADO"), 202);
        assertEquals("APROVADA", estado(id));
    }

    @SuppressWarnings("unchecked")
    List<String> eventos(String agregado, long id) {
        return em.createNativeQuery("select evento from shared.outbox where agregado = ?1 and agregado_id = ?2 order by id")
                .setParameter(1, agregado).setParameter(2, String.valueOf(id)).getResultList();
    }
}
