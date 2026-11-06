package br.com.financiamento.garantia.entrypoint;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static br.com.financiamento.DocumentosParaTeste.cpf;
import static br.com.financiamento.JornadaParaTeste.motoUsadaComContratoAssinado;
import static br.com.financiamento.JornadaParaTeste.residencialComContratoAssinado;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Do contrato assinado à garantia constituída (UC-0018), com exigência
 * (UC-0020), recusa (UC-0019) e retornos classificados como fato (RN-0029).
 */
@QuarkusTest
class GarantiaWebTest {

    static final String ANA = cpf(401), VENDEDOR = cpf(402);

    @Inject
    EntityManager em;

    JsonPath registro(long contrato) {
        return given().queryParam("contrato", contrato).get("/web/registros").then().statusCode(200).extract().jsonPath();
    }

    String retorno(String protocolo, String resultado, String identificacao, String texto) {
        var corpo = new HashMap<String, Object>(Map.of("protocolo", protocolo, "resultado", resultado));
        if (identificacao != null) corpo.put("identificacaoRegistrada", identificacao);
        if (texto != null) corpo.put("texto", texto);
        return given().contentType(ContentType.JSON).body(corpo).post("/retornos/orgao-de-registro")
                .then().statusCode(202).extract().jsonPath().getString("efeito");
    }

    String estadoDoContrato(long contrato) {
        return given().get("/web/contratos/" + contrato).then().extract().jsonPath().getString("estado");
    }

    void acao(long registro, String acao) {
        given().contentType(ContentType.JSON).body(Map.of()).post("/web/registros/" + registro + "/" + acao)
                .then().statusCode(200);
    }

    @Test
    void veiculoNoOrgaoDeTransitoAteAGarantiaConstituida() {
        long contrato = motoUsadaComContratoAssinado(ANA, VENDEDOR);
        var r = registro(contrato);
        assertEquals("órgão de trânsito", r.getString("orgao"));
        assertEquals("GAR-" + contrato + "-1", r.getString("protocolo"));
        assertEquals("EM_REGISTRO", estadoDoContrato(contrato));

        var protocolo = r.getString("protocolo");
        var bem = r.getString("identificacaoDoBem");
        assertEquals("CONSTITUIDA", retorno(protocolo, "REGISTRADO", bem, null));
        assertEquals("REPETIDO", retorno(protocolo, "REGISTRADO", bem, null));
        given().get("/web/contratos/" + contrato).then()
                .body("estado", equalTo("GARANTIA_CONSTITUIDA"))
                .body("garantia.protocolo", equalTo(protocolo))
                .body("garantia.situacao", equalTo("constituída"));

        long registroId = r.getLong("id");
        assertEquals(List.of("RegistroSolicitado", "GarantiaRegistrada"), eventos("Garantia", registroId));
        assertEquals(List.of("ContratoEmitido", "ContratoAssinado"), eventos("Contrato", contrato));
    }

    @Test
    void outroBemRegistradoViraPendenciaQueOAnalistaReenvia() {
        long contrato = motoUsadaComContratoAssinado(ANA, VENDEDOR);
        var r = registro(contrato);
        assertEquals("PENDENCIA_DE_IDENTIFICACAO", retorno(r.getString("protocolo"), "REGISTRADO", "OUTRO-CHASSI", null));
        assertEquals("EM_REGISTRO", estadoDoContrato(contrato));

        acao(r.getLong("id"), "reenvio");
        var depois = registro(contrato);
        assertEquals("GAR-" + contrato + "-2", depois.getString("protocolo"));
        assertEquals("CONSTITUIDA", retorno(depois.getString("protocolo"), "REGISTRADO", r.getString("identificacaoDoBem"), null));
        assertEquals("GARANTIA_CONSTITUIDA", estadoDoContrato(contrato));
    }

    @Test
    void imovelComExigenciaNoCartorio() {
        long contrato = residencialComContratoAssinado(ANA, VENDEDOR);
        var r = registro(contrato);
        assertEquals("cartório de registro de imóveis", r.getString("orgao"));

        assertEquals("EXIGENCIA", retorno(r.getString("protocolo"), "EXIGENCIA", null, "certidão de ônus atualizada"));
        given().get("/web/contratos/" + contrato).then().body("estado", equalTo("EM_EXIGENCIA"));

        acao(r.getLong("id"), "exigencia-cumprida");
        assertEquals("EM_REGISTRO", estadoDoContrato(contrato));
        var depois = registro(contrato);
        assertEquals("SEM_EFEITO", retorno(r.getString("protocolo"), "REGISTRADO", r.getString("identificacaoDoBem"), null),
                "retorno do protocolo antigo é guardado, sem efeito");
        assertEquals("CONSTITUIDA", retorno(depois.getString("protocolo"), "REGISTRADO", r.getString("identificacaoDoBem"), null));
        assertEquals("GARANTIA_CONSTITUIDA", estadoDoContrato(contrato));
        assertEquals(List.of("RegistroSolicitado", "ExigenciaEmitida", "ExigenciaCumprida", "RegistroSolicitado",
                "GarantiaRegistrada"), eventos("Garantia", r.getLong("id")));
    }

    @Test
    void recusaReenviadaERecusaSemSolucao() {
        long contrato = motoUsadaComContratoAssinado(ANA, VENDEDOR);
        var r = registro(contrato);
        assertEquals("RECUSADO", retorno(r.getString("protocolo"), "RECUSADO", null, "dados do proprietário divergentes"));
        assertEquals("REGISTRO_RECUSADO", estadoDoContrato(contrato));
        acao(r.getLong("id"), "reenvio");
        assertEquals("EM_REGISTRO", estadoDoContrato(contrato));

        var segunda = registro(contrato);
        retorno(segunda.getString("protocolo"), "RECUSADO", null, "bem com restrição judicial");
        given().contentType(ContentType.JSON).body(Map.of("motivo", "restrição judicial sem solução"))
                .post("/web/registros/" + r.getLong("id") + "/desfazimento").then().statusCode(200)
                .body("situacao", equalTo("DESFEITO"));
        assertEquals("CANCELADO", estadoDoContrato(contrato));
        assertEquals("ContratoCancelado", eventos("Contrato", contrato).getLast());
    }

    @Test
    void acoesDoAnalistaForaDaSituacaoCertaSaoRecusadas() {
        long contrato = motoUsadaComContratoAssinado(ANA, VENDEDOR);
        long registro = registro(contrato).getLong("id");
        // Em registro, sem retorno do órgão: não há recusa a reenviar (UC-0019), exigência a cumprir (UC-0020)
        // nem contrato a desfazer.
        given().contentType(ContentType.JSON).body(Map.of()).post("/web/registros/" + registro + "/reenvio")
                .then().statusCode(422).body("falhas[0].codigo", equalTo("GAR-0004"));
        given().contentType(ContentType.JSON).body(Map.of()).post("/web/registros/" + registro + "/exigencia-cumprida")
                .then().statusCode(422).body("falhas[0].codigo", equalTo("GAR-0005"));
        given().contentType(ContentType.JSON).body(Map.of("motivo", "engano"))
                .post("/web/registros/" + registro + "/desfazimento")
                .then().statusCode(422).body("falhas[0].codigo", equalTo("GAR-0006"));
        assertEquals("EM_REGISTRO", estadoDoContrato(contrato));
    }

    @Test
    void protocoloQueNaoENossoNaoEncontraRegistro() {
        given().contentType(ContentType.JSON).body(Map.of("protocolo", "XYZ-1", "resultado", "REGISTRADO"))
                .post("/retornos/orgao-de-registro").then().statusCode(404);
    }

    @SuppressWarnings("unchecked")
    List<String> eventos(String agregado, long id) {
        return em.createNativeQuery("select evento from shared.outbox where agregado = ?1 and agregado_id = ?2 order by id")
                .setParameter(1, agregado).setParameter(2, String.valueOf(id)).getResultList();
    }
}
