package br.com.financiamento.arquitetura;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Table;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Fitness function "Esquema por módulo" (ADR 0004, ADR 0008): cada módulo só
 * mapeia, cria e consulta o próprio esquema do PostgreSQL.
 */
class EsquemaPorModuloTest {

    static final Path FONTES = Path.of("src/main/java/br/com/financiamento");
    static final Path MIGRACOES = Path.of("src/main/resources/db/migration");
    static final Pattern MIGRACAO = Pattern.compile("V\\d+__([a-z]+)_[a-z0-9_]+\\.sql");
    static final Pattern SQL = Pattern.compile("(?i)\\b(select|insert|update|delete|from|join)\\b");

    static Set<String> modulos() throws IOException {
        try (Stream<Path> s = Files.list(FONTES)) {
            return s.filter(Files::isDirectory).map(p -> p.getFileName().toString()).collect(Collectors.toSet());
        }
    }

    @Test
    void mapeamentoDeCadaModuloApontaSoParaOProprioEsquema() {
        var classes = new ClassFileImporter().withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages(ArquiteturaTest.BASE);
        classes().that().areAnnotatedWith(Entity.class)
                .should(new ArchCondition<JavaClass>("mapear só o esquema do próprio módulo") {
                    @Override
                    public void check(JavaClass c, ConditionEvents events) {
                        var modulo = ArquiteturaTest.modulo(c.getPackageName());
                        var tabela = c.tryGetAnnotationOfType(Table.class);
                        if (tabela.isEmpty() || !modulo.equals(tabela.get().schema())) {
                            events.add(SimpleConditionEvent.violated(c, c.getName() + " precisa de @Table(schema = \"" + modulo + "\")"));
                        }
                        for (var campo : c.getFields()) {
                            campo.tryGetAnnotationOfType(CollectionTable.class)
                                    .filter(t -> !modulo.equals(t.schema()))
                                    .ifPresent(t -> events.add(SimpleConditionEvent.violated(campo,
                                            campo.getFullName() + " precisa de @CollectionTable(schema = \"" + modulo + "\")")));
                            campo.tryGetAnnotationOfType(JoinTable.class)
                                    .filter(t -> !modulo.equals(t.schema()))
                                    .ifPresent(t -> events.add(SimpleConditionEvent.violated(campo,
                                            campo.getFullName() + " precisa de @JoinTable(schema = \"" + modulo + "\")")));
                        }
                    }
                }).check(classes);
    }

    @Test
    void cadaMigracaoSoTocaOEsquemaDoSeuModulo() throws IOException {
        var modulos = modulos();
        var problemas = new ArrayList<String>();
        try (Stream<Path> s = Files.list(MIGRACOES)) {
            for (var arquivo : s.sorted().toList()) {
                var nome = arquivo.getFileName().toString();
                var m = MIGRACAO.matcher(nome);
                if (!m.matches() || !modulos.contains(m.group(1))) {
                    problemas.add(nome + ": o nome deve ser V<n>__<modulo>_<descricao>.sql, com um módulo existente");
                    continue;
                }
                var modulo = m.group(1);
                var sql = Files.readString(arquivo).replaceAll("--[^\n]*", "");
                problemas.addAll(esquemasAlheios(nome, sql, modulo, modulos));
                var semEsquema = Pattern.compile("(?i)\\b(create\\s+table|alter\\s+table|references|\\bon)\\s+(?!" + modulo + "\\.)([a-z_]+)\\s*[(\\s]")
                        .matcher(sql);
                while (semEsquema.find()) {
                    problemas.add(nome + ": \"" + semEsquema.group().trim() + "\" sem o esquema " + modulo);
                }
                var criaEsquema = Pattern.compile("(?i)create\\s+schema\\s+(?:if\\s+not\\s+exists\\s+)?([a-z_]+)").matcher(sql);
                while (criaEsquema.find()) {
                    if (!criaEsquema.group(1).equalsIgnoreCase(modulo)) {
                        problemas.add(nome + ": cria o esquema " + criaEsquema.group(1) + ", que não é do módulo " + modulo);
                    }
                }
            }
        }
        assertTrue(problemas.isEmpty(), String.join("\n", problemas));
    }

    @Test
    void nenhumaConsultaNoCodigoCitaOEsquemaDeOutroModulo() throws IOException {
        var modulos = modulos();
        var problemas = new ArrayList<String>();
        var literal = Pattern.compile("\"((?:[^\"\\\\]|\\\\.)*)\"");
        for (var modulo : modulos) {
            try (Stream<Path> s = Files.walk(FONTES.resolve(modulo))) {
                for (var arquivo : s.filter(p -> p.toString().endsWith(".java")).toList()) {
                    var m = literal.matcher(Files.readString(arquivo));
                    while (m.find()) {
                        var texto = m.group(1);
                        if (SQL.matcher(texto).find()) {
                            problemas.addAll(esquemasAlheios(FONTES.relativize(arquivo).toString(), texto, modulo, modulos));
                        }
                    }
                }
            }
        }
        assertTrue(problemas.isEmpty(), String.join("\n", problemas));
    }

    private static List<String> esquemasAlheios(String onde, String texto, String modulo, Set<String> modulos) {
        var problemas = new ArrayList<String>();
        var qualificado = Pattern.compile("(?i)\\b([a-z_]+)\\.([a-z_]+)\\b").matcher(texto);
        while (qualificado.find()) {
            var esquema = qualificado.group(1).toLowerCase();
            if (modulos.contains(esquema) && !esquema.equals(modulo)) {
                problemas.add(onde + " (" + modulo + ") cita o esquema de outro módulo: " + qualificado.group());
            }
        }
        return problemas;
    }
}
