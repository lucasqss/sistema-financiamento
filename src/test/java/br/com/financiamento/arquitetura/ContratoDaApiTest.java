package br.com.financiamento.arquitetura;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/**
 * Fitness function do contrato da API: todo resource tem a tag do seu domínio e todo endpoint diz o que faz,
 * de preferência pelo caso de uso do modelo. Assim o contrato publicado (docs/api/openapi.yaml) não fica para trás.
 */
class ContratoDaApiTest {

    @Test
    void todoEndpointDocumentado() {
        var classes = new ClassFileImporter().withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages(ArquiteturaTest.BASE);
        classes().that().areAnnotatedWith(Path.class)
                .should(new ArchCondition<JavaClass>("ter @Tag e @Operation(summary) em todo endpoint") {
                    @Override
                    public void check(JavaClass resource, ConditionEvents events) {
                        if (!resource.isAnnotatedWith(Tag.class)) {
                            events.add(SimpleConditionEvent.violated(resource, resource.getName() + " sem @Tag"));
                        }
                        for (var m : resource.getMethods()) {
                            if (!m.getModifiers().contains(JavaModifier.PUBLIC) || m.getOwner() != resource) continue;
                            boolean documentado = m.tryGetAnnotationOfType(Operation.class)
                                    .map(o -> !o.summary().isBlank()).orElse(false);
                            if (!documentado) {
                                events.add(SimpleConditionEvent.violated(m, m.getFullName() + " sem @Operation(summary)"));
                            }
                        }
                    }
                }).check(classes);
    }
}
