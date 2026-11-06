package br.com.financiamento.arquitetura;

import br.com.financiamento.shared.validacao.ErroCatalogado;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * O catálogo de erros publicado para quem integra (docs/api/catalogo-de-erros.md) é o do código (ADR 0011).
 * Gerado dos catálogos; desatualizado, o teste falha. Para regenerar: ./mvnw test -Dtest=CatalogoDeErrosPublicadoTest -Dregerar=true
 */
class CatalogoDeErrosPublicadoTest {

    static final Path ARQUIVO = Path.of("docs/api/catalogo-de-erros.md");

    @Test
    void catalogoPublicadoEstaEmDia() throws Exception {
        var gerado = gerar();
        if (Boolean.getBoolean("regerar")) {
            Files.createDirectories(ARQUIVO.getParent());
            Files.writeString(ARQUIVO, gerado);
        }
        var publicado = Files.exists(ARQUIVO) ? Files.readString(ARQUIVO) : "";
        assertEquals(gerado, publicado, "docs/api/catalogo-de-erros.md está desatualizado. Regenere com "
                + "./mvnw test -Dtest=CatalogoDeErrosPublicadoTest -Dregerar=true");
    }

    static String gerar() throws Exception {
        var classes = new ClassFileImporter().withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages(ArquiteturaTest.BASE);
        Map<String, List<ErroCatalogado>> porPrefixo = new LinkedHashMap<>();
        var enums = new ArrayList<ErroCatalogado>();
        for (var c : classes) {
            if (!c.isEnum() || !c.isAssignableTo(ErroCatalogado.class)) continue;
            for (var constante : c.reflect().getEnumConstants()) enums.add((ErroCatalogado) constante);
        }
        enums.sort(Comparator.comparing(ErroCatalogado::codigo));
        for (var e : enums) porPrefixo.computeIfAbsent(e.codigo().substring(0, 3), k -> new ArrayList<>()).add(e);

        var md = new StringBuilder();
        md.append("# Catálogo de erros da API\n\n");
        md.append("Gerado do código (ADR 0011) e conferido pelo teste `CatalogoDeErrosPublicadoTest`: não edite à mão.\n");
        md.append("Toda resposta de erro traz `falhas[]`, cada uma com `codigo` (desta lista), `codigoCorporativo` (quando mapeado em\n");
        md.append("`src/main/resources/catalogo-corporativo.properties`), `campo` (nas falhas de atributo), `regra` (do modelo de negócio) e `mensagem`.\n");
        md.append("Os códigos são estáveis: um código nunca muda de significado nem é reaproveitado.\n");
        md.append("`{0}`, `{1}`… na mensagem são preenchidos com os dados do caso.\n");
        for (var grupo : porPrefixo.entrySet()) {
            md.append("\n## ").append(nomeDoGrupo(grupo.getKey())).append("\n\n");
            md.append("| Código | Status | Tipo | Regra | Mensagem |\n|---|---|---|---|---|\n");
            for (var e : grupo.getValue()) {
                md.append("| `").append(e.codigo()).append("` | ").append(e.status()).append(" | ").append(e.tipo().name().toLowerCase())
                        .append(" | ").append(e.regraDoModelo() == null ? "—" : e.regraDoModelo()).append(" | ")
                        .append(e.modelo().replace("|", "\\|")).append(" |\n");
            }
        }
        return md.toString();
    }

    static String nomeDoGrupo(String prefixo) {
        return switch (prefixo) {
            case "ATR" -> "Atributo (forma e formato, todas as requisições)";
            case "SEG" -> "Acesso (autenticação e autorização, ADR 0012)";
            case "ORI" -> "Originação (DOM-0001)";
            case "BEM" -> "Bem e categorias (DIM-0001)";
            case "CRE" -> "Crédito (DOM-0002)";
            case "AVA" -> "Avaliação (DOM-0003)";
            case "CON" -> "Contratação (DOM-0004)";
            case "GAR" -> "Garantia (DOM-0005)";
            case "DES" -> "Desembolso (DOM-0006)";
            case "DOC" -> "Documentos (DOM-0008)";
            default -> prefixo;
        };
    }
}
