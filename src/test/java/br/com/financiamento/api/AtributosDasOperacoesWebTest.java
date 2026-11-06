package br.com.financiamento.api;

import br.com.financiamento.ContratoPublicado;
import br.com.financiamento.ContratoPublicado.Operacao;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A cadeia de atributos de cada operação (ADR 0011): um corpo errado em vários campos volta 400 com
 * <b>todas</b> as falhas de uma vez, cada uma com o campo e o código ATR. Os ids apontam recursos que
 * não existem de propósito: o atributo é conferido antes de qualquer regra de negócio.
 */
@QuarkusTest
class AtributosDasOperacoesWebTest {

    static final long INEXISTENTE = TodasAsOperacoesWebTest.INEXISTENTE;
    static final String LONGO = "x".repeat(601);

    record Caso(String operacao, Object corpo, Set<String> falhas) {
        @Override
        public String toString() {
            return operacao;
        }
    }

    static Caso caso(String operacao, Object corpo, String... falhas) {
        return new Caso(operacao, corpo, Set.of(falhas));
    }

    /** Operações que recebem arquivo (multipart): a forma do envio é conferida em DocumentosWebTest. */
    static final Set<String> MULTIPART = Set.of("POST /web/documentos");

    static final List<Caso> CASOS = List.of(
            // Retornos de sistemas externos
            caso("POST /retornos/avaliador/{avaliacaoId}", Map.of("valor", -1),
                    "avaliadaEm ATR-0001", "valor ATR-0007"),
            caso("POST /retornos/bureau-de-credito/{analiseId}",
                    Map.of("ressalva", LONGO, "sugeridas", Map.of("prazoMeses", 0, "parcela", 0, "entradaPropria", -1)),
                    "resultado ATR-0001", "ressalva ATR-0002", "sugeridas.prazoMeses ATR-0007",
                    "sugeridas.parcela ATR-0007", "sugeridas.entradaPropria ATR-0008"),
            caso("POST /retornos/fgts/{desembolsoId}", Map.of("liberado", false, "motivo", LONGO), "motivo ATR-0002"),
            caso("POST /retornos/orgao-de-registro", Map.of("identificacaoRegistrada", LONGO, "texto", LONGO + LONGO),
                    "protocolo ATR-0001", "resultado ATR-0001", "identificacaoRegistrada ATR-0002", "texto ATR-0002"),
            caso("POST /retornos/pagamento", Map.of("protocolo", LONGO), "protocolo ATR-0002"),
            caso("POST /retornos/seguradora/{contratacaoId}", Map.of(), "seguro ATR-0001", "apolice ATR-0001"),
            // Crédito
            caso("POST /web/analises-de-credito/{id}/decisao", Map.of("sugeridas", Map.of("prazoMeses", 0)),
                    "resultado ATR-0001", "sugeridas.prazoMeses ATR-0007"),
            // Contratação
            caso("POST /web/contratacoes", Map.of("conjuges", List.of(Map.of("documento", "12345678900"))),
                    "propostaId ATR-0001", "conjuges[0].documento ATR-0004", "conjuges[0].nome ATR-0001"),
            caso("POST /web/contratos/{id}/assinaturas", Map.of("documento", "123"), "documento ATR-0006"),
            caso("POST /web/contratos/{id}/cancelamento", Map.of("motivo", LONGO), "motivo ATR-0002"),
            caso("POST /web/contratos/{id}/prorrogacoes", Map.of(), "novaData ATR-0001"),
            caso("POST /web/contratos/{id}/prorrogacoes/pendente/decisao", Map.of("aprovada", "talvez"), "aprovada ATR-0003"),
            // Documentos
            caso("POST /web/documentos/{id}/conferencia", Map.of("aceito", false, "observacao", LONGO),
                    "motivo ATR-0001", "observacao ATR-0002"),
            // Avaliação (medição da obra)
            caso("POST /web/medicoes", Map.of("percentualAcumulado", 101),
                    "contratoId ATR-0001", "percentualAcumulado ATR-0012", "medidaEm ATR-0001"),
            // Originação
            caso("POST /web/propostas", Map.of("tipoDeBem", "1x"), "tipoDeBem ATR-0003"),
            caso("PUT /web/propostas/{id}/bem", Map.of("valorDeCompra", 0, "documentoDoVendedor", "1", "anoModelo", 0),
                    "identificacao ATR-0001", "valorDeCompra ATR-0007", "documentoDoVendedor ATR-0006", "anoModelo ATR-0007"),
            caso("PUT /web/propostas/{id}/condicoes", Map.of("entradaPropria", -1, "entradaFgts", -1, "prazoMeses", 0),
                    "entradaPropria ATR-0008", "entradaFgts ATR-0008", "prazoMeses ATR-0007", "parcela ATR-0001"),
            caso("PUT /web/propostas/{id}/participantes",
                    Map.of("participantes", List.of(Map.of("documento", "1", "nome", LONGO, "rendaComprovada", -1))),
                    "participantes[0].papel ATR-0001", "participantes[0].forma ATR-0001",
                    "participantes[0].documento ATR-0006", "participantes[0].nome ATR-0002",
                    "participantes[0].rendaComprovada ATR-0008"),
            // Garantia
            caso("POST /web/registros/{id}/desfazimento", Map.of("motivo", LONGO), "motivo ATR-0002"),
            // Consultas sem o filtro obrigatório
            caso("GET /web/analises-de-credito", null, "proposta ATR-0001"),
            caso("GET /web/avaliacoes", null, "proposta ATR-0001"),
            caso("GET /web/desembolsos", null, "contrato ATR-0001"),
            caso("GET /web/medicoes", null, "contrato ATR-0001"),
            caso("GET /web/registros", null, "contrato ATR-0001"));

    static Stream<Caso> casos() {
        return CASOS.stream();
    }

    static Operacao operacao(String nome) {
        return ContratoPublicado.operacoes().stream().filter(o -> o.nome().equals(nome)).findFirst()
                .orElseThrow(() -> new AssertionError(nome + " não está no contrato publicado"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("casos")
    void atributosErradosVoltamTodosDeUmaVez(Caso caso) {
        var op = operacao(caso.operacao());
        var spec = given();
        if (caso.corpo() != null) spec.contentType(ContentType.JSON).body(caso.corpo());
        List<String> falhas = spec.request(op.metodo(), op.caminhoCom(INEXISTENTE)).then()
                .statusCode(400)
                .body("tipo", equalTo("ATRIBUTO"))
                .extract().jsonPath().getList("falhas.collect { it.campo + ' ' + it.codigo }");
        assertEquals(caso.falhas(), new HashSet<>(falhas), caso.operacao());
    }

    /** Corpo vazio onde ele é obrigatório também é falha de atributo, não erro interno. */
    @Test
    void semCorpoEFalhaDeAtributo() {
        given().contentType(ContentType.JSON).post("/web/propostas").then().statusCode(400)
                .body("falhas[0].codigo", equalTo("ATR-0001"))
                .body("falhas[0].campo", equalTo("corpo"));
    }

    /** Toda operação que recebe corpo ou filtro obrigatório tem o seu caso aqui: operação nova não fica de fora. */
    @Test
    void todaOperacaoComEntradaTemCaso() {
        var comCaso = CASOS.stream().map(Caso::operacao).toList();
        var semCaso = ContratoPublicado.operacoes().stream()
                .filter(o -> o.temCorpo() || !o.consultasObrigatorias().isEmpty())
                .map(Operacao::nome)
                .filter(n -> !comCaso.contains(n) && !MULTIPART.contains(n))
                .toList();
        assertTrue(semCaso.isEmpty(), "Operações sem caso de atributo: " + semCaso);
    }
}
