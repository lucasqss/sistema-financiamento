package br.com.financiamento.arquitetura;

import br.com.financiamento.shared.outbox.RotaDeSaida;
import br.com.financiamento.shared.web.RepeteEmConflito;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Fitness functions da integração com os sistemas externos (ADR 0013): os formatos externos ficam nas camadas
 * anticorrupção, todo externo chamado tem o seu simulador no WireMock (docker/wiremock), todo externo do mapa de
 * contexto do modelo tem simulador, e os retornos se repetem em conflito de concorrência.
 */
class IntegracaoArquiteturaTest {

    static final String BASE = ArquiteturaTest.BASE;
    static final java.nio.file.Path MAPPINGS = java.nio.file.Path.of("docker", "wiremock", "mappings");
    static JavaClasses classes;

    /** Os sistemas externos do mapa de contexto do modelo e o arquivo do simulador de cada um. */
    static final Map<String, String> SIMULADOR_DO_EXTERNO = Map.of(
            "Avaliador", "avaliador.json",
            "Bureau de crédito", "bureau.json",
            "Cartório de Registro de Imóveis", "cartorio.json",
            "Órgão de trânsito", "orgao-de-transito.json",
            "Seguradora", "seguradora.json",
            "Sistema do FGTS", "fgts.json",
            "Sistema financeiro", "financeiro.json",
            "Sistema de cobrança", "cobranca.json");

    @BeforeAll
    static void importar() {
        classes = new ClassFileImporter().withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS).importPackages(BASE);
    }

    @Test
    void formatosExternosSoNaCamadaAnticorrupcao() {
        classes().that().implement(RotaDeSaida.class).should().resideInAPackage("..integracao..").check(classes);
        classes().that().areAnnotatedWith(RegisterRestClient.class).should().resideInAPackage("..integracao..")
                .andShould().notBePublic().check(classes);
    }

    @Test
    void retornosSeRepetemEmConflito() {
        classes().that().resideInAPackage("..entrypoint.retorno..").and().haveSimpleNameEndingWith("Resource")
                .should().beAnnotatedWith(RepeteEmConflito.class).check(classes);
    }

    /** Todo endpoint de externo chamado por um cliente tem stub no WireMock, e a URL dele está configurada. */
    @Test
    void todoExternoChamadoTemSimuladorEConfiguracao() throws Exception {
        var simulados = caminhosSimulados();
        var propriedades = new Properties();
        try (var in = Files.newInputStream(java.nio.file.Path.of("src/main/resources/application.properties"))) {
            propriedades.load(in);
        }
        var faltas = new TreeSet<String>();
        for (var cliente : classes) {
            if (!cliente.isAnnotatedWith(RegisterRestClient.class)) continue;
            var tipo = cliente.reflect();
            var chave = tipo.getAnnotation(RegisterRestClient.class).configKey();
            if (!propriedades.containsKey("quarkus.rest-client." + chave + ".url")) faltas.add("URL de " + chave);
            for (var metodo : tipo.getDeclaredMethods()) {
                var p = metodo.getAnnotation(Path.class);
                if (p != null && !simulados.contains(p.value())) faltas.add("stub de " + p.value());
            }
        }
        assertTrue(faltas.isEmpty(), "Faltam: " + faltas);
    }

    /** Todo sistema externo do mapa de contexto do modelo tem o seu simulador. */
    @Test
    void todoExternoDoModeloTemSimulador() throws Exception {
        var especificacao = Files.readString(java.nio.file.Path.of("docs/dominio/especificacao.md"));
        var externos = new HashSet<String>();
        var m = Pattern.compile("ATR_\\d+\\[\\(\"([^\"]+)\"\\)\\]").matcher(especificacao);
        while (m.find()) externos.add(m.group(1));
        assertTrue(externos.size() >= 6, "externos lidos do mapa de contexto: " + externos);
        var semSimulador = new TreeSet<String>();
        for (var externo : externos) {
            var arquivo = SIMULADOR_DO_EXTERNO.get(externo);
            if (arquivo == null || !Files.exists(MAPPINGS.resolve(arquivo))) semSimulador.add(externo);
        }
        assertTrue(semSimulador.isEmpty(), "Externos do modelo sem simulador em docker/wiremock: " + semSimulador);
    }

    static Set<String> caminhosSimulados() throws Exception {
        var caminhos = new HashSet<String>();
        var json = new ObjectMapper();
        try (var arquivos = Files.list(MAPPINGS)) {
            for (var arquivo : arquivos.toList()) {
                for (var mapping : json.readTree(arquivo.toFile()).path("mappings")) {
                    caminhos.add(mapping.path("request").path("urlPath").asText());
                }
            }
        }
        return caminhos;
    }
}
