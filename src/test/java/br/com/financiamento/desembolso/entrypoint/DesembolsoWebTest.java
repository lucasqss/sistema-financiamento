package br.com.financiamento.desembolso.entrypoint;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static br.com.financiamento.DocumentosParaTeste.cpf;
import static br.com.financiamento.JornadaParaTeste.contratoDeImovelAssinado;
import static br.com.financiamento.JornadaParaTeste.garantiaConstituida;
import static br.com.financiamento.JornadaParaTeste.motoUsadaComContratoAssinado;
import static br.com.financiamento.JornadaParaTeste.residencialComFgtsAprovado;
import static br.com.financiamento.JornadaParaTeste.residencialNaPlantaAprovado;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Da garantia constituída ao contrato formalizado: liberação única (UC-0021),
 * FGTS negado e entrada recomposta (UC-0022), e parcelas por medição no imóvel
 * na planta (UC-0013, UC-0023), com a prorrogação da obra (UC-0017).
 */
@QuarkusTest
class DesembolsoWebTest {

    static final String ANA = cpf(501), VENDEDOR = cpf(502);

    @Inject
    EntityManager em;

    JsonPath desembolso(long contrato) {
        return given().queryParam("contrato", contrato).get("/web/desembolsos").then().statusCode(200).extract().jsonPath();
    }

    void pagar(String protocolo) {
        given().contentType(ContentType.JSON).body(Map.of("protocolo", protocolo)).post("/retornos/pagamento")
                .then().statusCode(202);
    }

    String estado(long contrato) {
        return given().get("/web/contratos/" + contrato).then().extract().jsonPath().getString("estado");
    }

    void medir(long contrato, int percentual, int status) {
        given().contentType(ContentType.JSON)
                .body(Map.of("contratoId", contrato, "percentualAcumulado", percentual, "medidaEm", LocalDate.now().toString()))
                .post("/web/medicoes").then().statusCode(status);
    }

    @Test
    void bemProntoLiberadoDeUmaVezFormalizaOContrato() {
        long contrato = motoUsadaComContratoAssinado(ANA, VENDEDOR);
        given().queryParam("contrato", contrato).get("/web/desembolsos").then().statusCode(404); // RN-0030
        garantiaConstituida(contrato);

        var d = desembolso(contrato);
        assertEquals("NAO_SE_APLICA", d.getString("fgts"));
        assertEquals(12600.0f, d.getFloat("liberacoes[0].valor"));
        pagar(d.getString("liberacoes[0].protocolo"));
        pagar(d.getString("liberacoes[0].protocolo")); // confirmação repetida
        assertEquals("FORMALIZADO", estado(contrato));
        assertEquals(12600.0f, desembolso(contrato).getFloat("totalLiberado"));
        assertEquals(List.of("LiberacaoSolicitada", "RecursoLiberado"), eventos(d.getLong("id")));

        // RN-0031: protocolo do contrato, mas de uma liberação que não existe.
        given().contentType(ContentType.JSON).body(Map.of("protocolo", "LIB-" + contrato + "-99")).post("/retornos/pagamento")
                .then().statusCode(422).body("falhas[0].codigo", equalTo("DES-0006"));
        // UC-0022: sem FGTS negado, não há entrada a recompor.
        given().post("/web/desembolsos/" + d.getLong("id") + "/entrada-recomposta").then().statusCode(422)
                .body("falhas[0].codigo", equalTo("DES-0007"));
    }

    @Test
    void fgtsNegadoEEntradaRecomposta() {
        long proposta = residencialComFgtsAprovado(ANA, VENDEDOR);
        long contrato = contratoDeImovelAssinado(proposta, ANA, VENDEDOR, null);
        garantiaConstituida(contrato);

        var d = desembolso(contrato);
        assertEquals("SOLICITADO", d.getString("fgts"));
        assertEquals(0, d.getList("liberacoes").size());
        given().contentType(ContentType.JSON).body(Map.of("liberado", false, "motivo", "saldo insuficiente"))
                .post("/retornos/fgts/" + d.getLong("id")).then().statusCode(202);
        given().post("/web/desembolsos/" + d.getLong("id") + "/entrada-recomposta").then().statusCode(200)
                .body("fgts", equalTo("RECOMPOSTO"))
                .body("liberacoes[0].valor", equalTo(400000.0f));
        pagar(desembolso(contrato).getString("liberacoes[0].protocolo"));
        assertEquals("FORMALIZADO", estado(contrato));
        assertEquals(List.of("FgtsSolicitado", "FgtsNegado", "LiberacaoSolicitada", "RecursoLiberado"),
                eventos(d.getLong("id")));
    }

    @Test
    void imovelNaPlantaPorMedicaoComProrrogacao() {
        var previsao = LocalDate.now().plusYears(2);
        long proposta = residencialNaPlantaAprovado(ANA, VENDEDOR);
        // No imóvel na planta, a previsão de entrega da obra é obrigatória no aceite.
        given().contentType(ContentType.JSON).body(Map.of("propostaId", proposta)).post("/web/contratacoes")
                .then().statusCode(422);
        long contrato = contratoDeImovelAssinado(proposta, ANA, VENDEDOR, previsao);
        medir(contrato, 10, 422); // antes da garantia, a medição é recusada (UC-0013)
        garantiaConstituida(contrato);
        assertEquals(0, desembolso(contrato).getList("liberacoes").size(), "na planta, espera a medição");

        medir(contrato, 30, 200);
        given().queryParam("contrato", contrato).get("/web/medicoes").then().statusCode(200)
                .body("percentualAcumulado", equalTo(List.of(30.0f)));
        var d = desembolso(contrato);
        assertEquals(120000.0f, d.getFloat("liberacoes[0].valor")); // 400.000 × 30%
        pagar(d.getString("liberacoes[0].protocolo"));
        assertEquals("EM_OBRA", estado(contrato));

        // UC-0017: 150 dias somados são aceitos; 195 viram pendência para o analista.
        prorrogar(contrato, previsao.plusDays(90));
        prorrogar(contrato, previsao.plusDays(150));
        prorrogar(contrato, previsao.plusDays(195));
        given().get("/web/contratos/" + contrato).then()
                .body("obra.diasProrrogados", equalTo(150))
                .body("obra.prorrogacaoPendente", equalTo(previsao.plusDays(195).toString()));
        given().contentType(ContentType.JSON).body(Map.of("aprovada", false))
                .post("/web/contratos/" + contrato + "/prorrogacoes/pendente/decisao").then().statusCode(200)
                .body("obra.prorrogacaoPendente", equalTo(null));
        // RN-0027: sem prorrogação pendente, não há o que decidir; a nova data vem depois da previsão atual.
        given().contentType(ContentType.JSON).body(Map.of("aprovada", true))
                .post("/web/contratos/" + contrato + "/prorrogacoes/pendente/decisao").then().statusCode(422)
                .body("falhas[0].codigo", equalTo("CON-0015"));
        given().contentType(ContentType.JSON).body(Map.of("novaData", previsao.toString()))
                .post("/web/contratos/" + contrato + "/prorrogacoes").then().statusCode(422)
                .body("falhas[0].codigo", equalTo("CON-0014"));

        medir(contrato, 25, 422); // RN-0024: nunca diminui
        medir(contrato, 70, 200);
        pagar(desembolso(contrato).getString("liberacoes[1].protocolo"));
        assertEquals("EM_OBRA", estado(contrato), "parcela intermediária");
        medir(contrato, 100, 200);
        d = desembolso(contrato);
        assertEquals(120000.0f, d.getFloat("liberacoes[2].valor"));
        pagar(d.getString("liberacoes[2].protocolo"));
        assertEquals("FORMALIZADO", estado(contrato));
        assertEquals(400000.0f, desembolso(contrato).getFloat("totalLiberado"));
    }

    @Test
    void medicaoDeContratoDeBemProntoERecusada() {
        long contrato = motoUsadaComContratoAssinado(ANA, VENDEDOR);
        garantiaConstituida(contrato);
        medir(contrato, 50, 422);
    }

    void prorrogar(long contrato, LocalDate novaData) {
        given().contentType(ContentType.JSON).body(Map.of("novaData", novaData.toString()))
                .post("/web/contratos/" + contrato + "/prorrogacoes").then().statusCode(200);
    }

    @SuppressWarnings("unchecked")
    List<String> eventos(long desembolso) {
        return em.createNativeQuery("select evento from shared.outbox where agregado = 'Desembolso' and agregado_id = ?1 order by id")
                .setParameter(1, String.valueOf(desembolso)).getResultList();
    }
}
