package br.com.financiamento.api;

import br.com.financiamento.ContratoPublicado;
import br.com.financiamento.ContratoPublicado.Operacao;
import br.com.financiamento.SemTokenPadrao;
import br.com.financiamento.TokensParaTeste;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static br.com.financiamento.DocumentosParaTeste.cpf;
import static br.com.financiamento.EnvioDeDocumentosParaTeste.PDF;
import static br.com.financiamento.EnvioDeDocumentosParaTeste.texto;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * Percorre todas as operações do contrato publicado (docs/api/openapi.json) e confere o que vale para
 * qualquer uma delas: sem token é 401, papel que a operação não aceita é 403 (ADR 0012) e recurso que não
 * existe é 404 do catálogo (ADR 0011). Operação nova entra aqui sozinha; se ela recebe corpo, este teste
 * pede um exemplo válido em {@link #EXEMPLOS}.
 */
@QuarkusTest
@SemTokenPadrao
class TodasAsOperacoesWebTest {

    static final long INEXISTENTE = 999_999_999L;

    /** Um corpo válido (passa pela cadeia de atributos) para cada operação que recebe JSON. */
    static final Map<String, Object> EXEMPLOS = Map.ofEntries(
            Map.entry("POST /retornos/avaliador/{avaliacaoId}",
                    Map.of("aceitoComoGarantia", true, "valor", 20000, "avaliadaEm", LocalDate.now().toString())),
            Map.entry("POST /retornos/bureau-de-credito/{analiseId}", Map.of("resultado", "APROVADO")),
            Map.entry("POST /retornos/fgts/{desembolsoId}", Map.of("liberado", true)),
            Map.entry("POST /retornos/orgao-de-registro",
                    Map.of("protocolo", "GAR-" + INEXISTENTE + "-1", "resultado", "REGISTRADO", "identificacaoRegistrada", "X")),
            Map.entry("POST /retornos/pagamento", Map.of("protocolo", "LIB-" + INEXISTENTE + "-1")),
            Map.entry("POST /retornos/seguradora/{contratacaoId}", Map.of("seguro", "MIP", "apolice", "AP-1")),
            Map.entry("POST /web/analises-de-credito/{id}/decisao", Map.of("resultado", "APROVADO")),
            Map.entry("POST /web/contratacoes", Map.of("propostaId", INEXISTENTE)),
            Map.entry("POST /web/contratos/{id}/assinaturas", Map.of("documento", TokensParaTeste.OPERADOR_DOCUMENTO)),
            Map.entry("POST /web/contratos/{id}/cancelamento", Map.of("motivo", "desistência")),
            Map.entry("POST /web/contratos/{id}/prorrogacoes", Map.of("novaData", LocalDate.now().plusYears(2).toString())),
            Map.entry("POST /web/contratos/{id}/prorrogacoes/pendente/decisao", Map.of("aprovada", true)),
            Map.entry("POST /web/documentos/{id}/conferencia", Map.of("aceito", true)),
            Map.entry("POST /web/medicoes", Map.of("contratoId", INEXISTENTE, "percentualAcumulado", 10,
                    "medidaEm", LocalDate.now().toString())),
            Map.entry("POST /web/propostas", Map.of("tipoDeBem", "MOTO_USADA")),
            Map.entry("PUT /web/propostas/{id}/bem",
                    Map.of("identificacao", "MOTO-1", "valorDeCompra", 20000, "documentoDoVendedor", cpf(950))),
            Map.entry("PUT /web/propostas/{id}/condicoes", Map.of("entradaPropria", 7400, "prazoMeses", 36, "parcela", 900)),
            Map.entry("PUT /web/propostas/{id}/participantes", Map.of("participantes", List.of(Map.of(
                    "papel", "PROPONENTE", "forma", "PESSOA_FISICA", "documento", cpf(951), "nome", "Ana",
                    "nascimento", "1990-05-20", "rendaComprovada", 20000)))),
            Map.entry("POST /web/registros/{id}/desfazimento", Map.of("motivo", "registro indevido")));

    static Stream<Operacao> todas() {
        return ContratoPublicado.operacoes().stream();
    }

    static Stream<Operacao> comIdNoCaminho() {
        return todas().filter(Operacao::temIdNoCaminho);
    }

    /** Monta a chamada da operação com o id dado no caminho e nos filtros obrigatórios, e o corpo de exemplo. */
    static Response chamar(RequestSpecification spec, Operacao op, long id) {
        if (op.multipart()) {
            spec.multiPart(texto("propostaId", String.valueOf(id))).multiPart(texto("titular", "bem"))
                    .multiPart(texto("tipo", "CRLV")).multiPart("arquivo", "documento.pdf", PDF, "application/pdf");
        } else if (op.temCorpo()) {
            var corpo = EXEMPLOS.get(op.nome());
            assertNotNull(corpo, "Falta um corpo de exemplo para " + op.nome() + " em TodasAsOperacoesWebTest.EXEMPLOS");
            spec.contentType(ContentType.JSON).body(corpo);
        }
        op.consultasObrigatorias().forEach(c -> spec.queryParam(c, id));
        return spec.request(op.metodo(), op.caminhoCom(id));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("todas")
    void semTokenE401(Operacao op) {
        chamar(given(), op, INEXISTENTE).then().statusCode(401)
                .header("WWW-Authenticate", "Bearer")
                .body("falhas[0].codigo", equalTo("SEG-0001"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("todas")
    void papelQueAOperacaoNaoAceitaE403(Operacao op) {
        var outros = TokensParaTeste.TODOS_OS_PAPEIS.stream().filter(p -> !op.papeis().contains(p)).toArray(String[]::new);
        assumeFalse(outros.length == 0, "a operação aceita todos os papéis");
        var token = TokensParaTeste.de(cpf(952), outros);
        chamar(given().header("Authorization", "Bearer " + token), op, INEXISTENTE).then().statusCode(403)
                .body("falhas[0].codigo", equalTo("SEG-0002"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("comIdNoCaminho")
    void recursoInexistenteE404DoCatalogo(Operacao op) {
        chamar(given().header("Authorization", "Bearer " + TokensParaTeste.operador()), op, INEXISTENTE).then()
                .statusCode(404)
                .body("tipo", equalTo("DOMINIO"))
                .body("falhas[0].codigo", matchesPattern("[A-Z]{3}-\\d{4}"));
    }

    /** O retorno do pagamento aponta a liberação pelo protocolo nosso: protocolo desconhecido é 404. */
    @Test
    void pagamentoDeProtocoloDesconhecidoE404() {
        given().header("Authorization", "Bearer " + TokensParaTeste.sistema("sistema-pagamento"))
                .contentType(ContentType.JSON).body(EXEMPLOS.get("POST /retornos/pagamento"))
                .post("/retornos/pagamento").then().statusCode(404)
                .body("falhas[0].codigo", matchesPattern("DES-\\d{4}"));
    }
}
