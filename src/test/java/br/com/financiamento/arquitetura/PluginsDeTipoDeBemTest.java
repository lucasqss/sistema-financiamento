package br.com.financiamento.arquitetura;

import br.com.financiamento.ModeloDeNegocio;
import br.com.financiamento.bem.CategoriaDeBem;
import br.com.financiamento.bem.FormaDeLiberacao;
import br.com.financiamento.bem.FormaDoParticipante;
import br.com.financiamento.bem.TipoDeBem;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Fitness function "Plug-ins de tipo de bem" (ADR 0003, ADR 0008).
 *
 * - O núcleo e os outros módulos não conhecem nenhum plug-in: só a porta.
 * - Todo tipo de bem final da dimensão DIM-0001 do modelo aprovado é atendido
 *   por exatamente um plug-in, e nenhum plug-in atende tipo fora dela.
 * - Os parâmetros de cada plug-in são os da tabela RN-0010 do modelo.
 */
class PluginsDeTipoDeBemTest {

    static final String BEM = ArquiteturaTest.BASE + ".bem";

    static JavaClasses classes;
    static List<CategoriaDeBem> plugins;

    @BeforeAll
    static void carregar() throws Exception {
        classes = new ClassFileImporter().withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages(ArquiteturaTest.BASE);
        plugins = new ArrayList<>();
        for (var c : classes) {
            if (c.isInterface() || !c.isAssignableTo(CategoriaDeBem.class)) continue;
            var construtor = c.reflect().getDeclaredConstructor();
            construtor.setAccessible(true);
            plugins.add((CategoriaDeBem) construtor.newInstance());
        }
    }

    @Test
    void ninguemForaDoPluginDependeDele() {
        classes().should(new ArchCondition<JavaClass>("não depender de plug-in alheio") {
            @Override
            public void check(JavaClass origem, ConditionEvents events) {
                for (var dep : origem.getDirectDependenciesFromSelf()) {
                    var plugin = pluginDoPacote(dep.getTargetClass().getPackageName());
                    if (plugin != null && !plugin.equals(pluginDoPacote(origem.getPackageName()))) {
                        events.add(SimpleConditionEvent.violated(dep, dep.getDescription()));
                    }
                }
            }
        }).check(classes);
    }

    @Test
    void osPluginsImplementamSoAPortaENaoSaoPublicos() {
        classes().that().resideInAPackage(BEM + ".*..").and().areAssignableTo(CategoriaDeBem.class)
                .should().notBePublic()
                .check(classes);
    }

    @Test
    void todoTipoDeBemDoModeloTemExatamenteUmPlugin() {
        var doModelo = ModeloDeNegocio.folhasDaDimensao("DIM-0001").stream()
                .map(TipoDeBem::doNome).collect(Collectors.toSet());
        assertEquals(10, doModelo.size(), "folhas de DIM-0001 lidas do modelo: " + doModelo);

        Map<TipoDeBem, List<String>> atendidoPor = new HashMap<>();
        for (var p : plugins) {
            for (var t : p.tipos()) atendidoPor.computeIfAbsent(t, k -> new ArrayList<>()).add(p.nome());
        }
        var problemas = new ArrayList<String>();
        for (var t : doModelo) {
            var quem = atendidoPor.getOrDefault(t, List.of());
            if (quem.size() != 1) problemas.add(t + " atendido por " + quem);
        }
        for (var t : atendidoPor.keySet()) {
            if (!doModelo.contains(t)) problemas.add(t + " não é tipo de bem do modelo (DIM-0001)");
        }
        assertTrue(problemas.isEmpty(), String.join("\n", problemas));
    }

    @Test
    void parametrosConferemComARegraRN0010() {
        var tabela = ModeloDeNegocio.tabela("RN-0010");
        var problemas = new ArrayList<String>();
        for (var tipoNoModelo : tabela.get("Prazo (meses)").keySet()) {
            var tipo = TipoDeBem.doNome(tipoNoModelo);
            var plugin = plugins.stream().filter(p -> p.tipos().contains(tipo)).findFirst().orElse(null);
            if (plugin == null) { problemas.add("sem plug-in: " + tipo); continue; }
            var p = plugin.parametros(tipo);
            if (p == null) { problemas.add(tipo + ": plug-in " + plugin.nome() + " sem parâmetros (RN-0010)"); continue; }

            var prazo = tabela.get("Prazo (meses)").get(tipoNoModelo).split("[–-]");
            confere(problemas, tipo, "prazo mínimo", Integer.parseInt(prazo[0].trim()), p.prazoMinimoMeses());
            confere(problemas, tipo, "prazo máximo", Integer.parseInt(prazo[1].trim()), p.prazoMaximoMeses());
            var cota = new BigDecimal(tabela.get("Cota máxima").get(tipoNoModelo).replace("%", "")).movePointLeft(2);
            confere(problemas, tipo, "cota máxima", 0, cota.compareTo(p.cotaMaxima()));
            confere(problemas, tipo, "aceita PJ", simNao(tabela.get("Aceita PJ").get(tipoNoModelo)), p.aceitaPessoaJuridica());
            confere(problemas, tipo, "FGTS", simNao(tabela.get("FGTS na entrada").get(tipoNoModelo)), p.aceitaFgtsNaEntrada());
            var idade = tabela.get("Idade máxima ao fim do contrato").get(tipoNoModelo);
            confere(problemas, tipo, "idade máxima", idade.equals("—") ? null : Integer.valueOf(idade.replaceAll("\\D", "")),
                    p.idadeMaximaDoBem());
            var liberacao = tabela.get("Liberação").get(tipoNoModelo).equals("por medição")
                    ? FormaDeLiberacao.POR_MEDICAO : FormaDeLiberacao.UNICA;
            confere(problemas, tipo, "liberação", liberacao, p.liberacao());

            // RN-0002 e RN-0017 dizem a mesma coisa: onde a PJ não é aceita, não há documentos de PJ.
            boolean temDocumentosDePj = !plugin.documentosDoParticipante(tipo, FormaDoParticipante.PESSOA_JURIDICA).isEmpty();
            confere(problemas, tipo, "documentos de PJ (RN-0017) x aceita PJ", p.aceitaPessoaJuridica(), temDocumentosDePj);
            if (plugin.documentosDoBem(tipo) == null || plugin.documentosDoBem(tipo).isEmpty()) {
                problemas.add(tipo + ": sem documentos do bem (RN-0016)");
            }
        }
        assertTrue(problemas.isEmpty(), String.join("\n", problemas));
    }

    @Test
    void fonteEValidadeDaAvaliacaoConferemComARegraRN0023() {
        var tabela = ModeloDeNegocio.tabela("RN-0023");
        var dia = java.time.LocalDate.of(2026, 2, 10);
        var problemas = new ArrayList<String>();
        var cobertos = new java.util.HashSet<TipoDeBem>();
        for (var linha : tabela.entrySet()) {
            var fonte = ModeloDeNegocio.semLinks(linha.getValue().get("Fonte"));
            var validade = linha.getValue().entrySet().stream().filter(e -> e.getKey().startsWith("Validade"))
                    .findFirst().orElseThrow().getValue();
            var folhas = new ArrayList<String>();
            for (var parte : linha.getKey().split("\\s+/\\s+")) {
                folhas.addAll(ModeloDeNegocio.folhasAbaixoDe("DIM-0001", parte.trim()));
            }
            if (folhas.isEmpty()) problemas.add("linha da RN-0023 sem tipo de bem: " + linha.getKey());
            for (var folha : folhas) {
                var tipo = TipoDeBem.doNome(folha);
                cobertos.add(tipo);
                var plugin = plugins.stream().filter(p -> p.tipos().contains(tipo)).findFirst().orElseThrow();
                confere(problemas, tipo, "fonte da avaliação", fonte, plugin.fonteDaAvaliacao(tipo));
                var esperada = validade.equals("mês da consulta")
                        ? dia.with(java.time.temporal.TemporalAdjusters.lastDayOfMonth())
                        : dia.plusDays(Integer.parseInt(validade.replaceAll("\\D", "")));
                confere(problemas, tipo, "validade da avaliação", esperada, plugin.validadeDaAvaliacao(tipo, dia));
            }
        }
        assertEquals(10, cobertos.size(), "tipos cobertos pela RN-0023: " + cobertos);
        assertTrue(problemas.isEmpty(), String.join("\n", problemas));
    }

    private static boolean simNao(String valor) {
        return switch (valor) {
            case "sim" -> true;
            case "não" -> false;
            default -> throw new IllegalStateException("Valor inesperado na RN-0010: " + valor);
        };
    }

    private static void confere(List<String> problemas, TipoDeBem tipo, String o, Object esperado, Object obtido) {
        if (esperado == null ? obtido != null : !esperado.equals(obtido)) {
            problemas.add(tipo + ": " + o + " no modelo = " + esperado + ", no plug-in = " + obtido);
        }
    }

    /** "br.com.financiamento.bem.veiculo.x" -> "veiculo"; fora de um plug-in -> null. */
    static String pluginDoPacote(String pkg) {
        if (!pkg.startsWith(BEM + ".")) return null;
        return pkg.substring(BEM.length() + 1).split("\\.")[0];
    }

    @Test
    void existeAoMenosUmPlugin() {
        if (plugins.isEmpty()) fail("Nenhum plug-in de categoria de bem encontrado.");
    }
}
