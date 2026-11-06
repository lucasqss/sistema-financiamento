package br.com.financiamento.arquitetura;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Path;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noMethods;

/**
 * Fitness function da autorização (ADR 0012): todo endpoint declara os papéis que o acessam; nenhum é aberto.
 */
class SegurancaArquiteturaTest {

    static final com.tngtech.archunit.core.domain.JavaClasses CLASSES = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS).importPackages(ArquiteturaTest.BASE);

    @Test
    void todoEndpointDeclaraOsPapeis() {
        classes().that().areAnnotatedWith(Path.class)
                .should(new ArchCondition<JavaClass>("declarar @RolesAllowed em todo método público") {
                    @Override
                    public void check(JavaClass resource, ConditionEvents events) {
                        boolean naClasse = resource.isAnnotatedWith(RolesAllowed.class);
                        for (var m : resource.getMethods()) {
                            if (!m.getModifiers().contains(JavaModifier.PUBLIC) || m.getOwner() != resource) continue;
                            if (!naClasse && !m.isAnnotatedWith(RolesAllowed.class)) {
                                events.add(SimpleConditionEvent.violated(m, m.getFullName() + " sem @RolesAllowed"));
                            }
                        }
                    }
                }).check(CLASSES);
    }

    @Test
    void nenhumEndpointAberto() {
        noMethods().should().beAnnotatedWith(PermitAll.class).check(CLASSES);
        classes().should().notBeAnnotatedWith(PermitAll.class).check(CLASSES);
    }
}
