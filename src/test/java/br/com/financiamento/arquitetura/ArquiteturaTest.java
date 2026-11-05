package br.com.financiamento.arquitetura;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * Fitness function "Fronteiras de módulo" (ADR 0008): as regras da skill
 * arquitetura-modular. Uma falha se corrige no código, nunca na regra.
 */
class ArquiteturaTest {

    static final String BASE = "br.com.financiamento";

    static JavaClasses classes;

    @BeforeAll
    static void importar() {
        classes = new ClassFileImporter().withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS).importPackages(BASE);
    }

    // --- Fronteiras entre módulos ---

    @Test
    void semCiclosEntreModulos() {
        slices()
            .matching(BASE + ".(*)..")
            .should().beFreeOfCycles()
            .check(classes);
    }

    @Test
    void outrosModulosSoPelaRaiz() {
        classes()
            .should(acessarOutrosModulosSoPelaRaiz())
            .check(classes);
    }

    @Test
    void sharedNaoDependeDeModulos() {
        noClasses()
            .that().resideInAPackage(BASE + ".shared..")
            .should().dependOnClassesThat(resideInAPackage(BASE + "..").and(not(resideInAPackage(BASE + ".shared.."))))
            .because("shared é transversal e não conhece nenhum módulo")
            .check(classes);
    }

    // --- Camadas dentro do módulo ---

    @Test
    void dominioIsolado() {
        noClasses()
            .that().resideInAPackage("..dominio..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..aplicacao..", "..entrypoint..", "..integracao..")
            .check(classes);
    }

    @Test
    void aplicacaoNaoConheceEntradasNemIntegracoes() {
        noClasses()
            .that().resideInAPackage("..aplicacao..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..entrypoint..", "..integracao..")
            .check(classes);
    }

    @Test
    void requestsEResponsesSoNasBordas() {
        classes()
            .that().haveSimpleNameEndingWith("Response")
            .or().haveSimpleNameEndingWith("Request")
            .should().resideInAnyPackage("..entrypoint..", "..integracao..", "..canal..")
            .check(classes);
    }

    // --- Visibilidade ---

    @Test
    void repositoriesDeSubconceitoFechados() {
        classes()
            .that().resideInAPackage("..dominio.*..")
            .and().haveSimpleNameEndingWith("Repository")
            .should().notBePublic()
            .allowEmptyShould(true)
            .check(classes);
    }

    @Test
    void adaptadoresFechados() {
        classes()
            .that().resideInAPackage("..integracao..")
            .and().haveSimpleNameEndingWith("Adapter")
            .should().notBePublic()
            .allowEmptyShould(true)
            .check(classes);
    }

    /** A Facade é a API pública do módulo por definição, mesmo antes de ter quem a chame. */
    @Test
    void publicSoQuandoNecessario() {
        classes()
            .that().resideOutsideOfPackages("..entrypoint..", "..canal..")
            .and().haveSimpleNameNotEndingWith("Facade")
            .should(serPublicSoSeUsadaForaDoPacote())
            .check(classes);
    }

    // --- Condições ---

    static ArchCondition<JavaClass> acessarOutrosModulosSoPelaRaiz() {
        return new ArchCondition<>("acessar outros módulos só pela raiz") {
            @Override
            public void check(JavaClass origem, ConditionEvents events) {
                var moduloOrigem = modulo(origem.getPackageName());
                for (var dep : origem.getDirectDependenciesFromSelf()) {
                    var pkg = dep.getTargetClass().getPackageName();
                    if (!pkg.startsWith(BASE + ".")) continue;
                    var destino = modulo(pkg);
                    boolean outroModulo = !destino.equals(moduloOrigem);
                    boolean interno = !pkg.equals(BASE + "." + destino);
                    if (outroModulo && interno && !destino.equals("shared")) {
                        events.add(SimpleConditionEvent.violated(dep, dep.getDescription()));
                    }
                }
            }
        };
    }

    static ArchCondition<JavaClass> serPublicSoSeUsadaForaDoPacote() {
        return new ArchCondition<>("ser public só se usada fora do pacote") {
            @Override
            public void check(JavaClass c, ConditionEvents events) {
                if (!c.getModifiers().contains(JavaModifier.PUBLIC)) return;
                boolean usadaFora = c.getDirectDependenciesToSelf().stream()
                        .anyMatch(d -> !d.getOriginClass().getPackageName().equals(c.getPackageName()));
                if (!usadaFora) {
                    events.add(SimpleConditionEvent.violated(c, c.getName() + " é public mas só é usada no próprio pacote"));
                }
            }
        };
    }

    static String modulo(String pkg) {
        if (!pkg.startsWith(BASE + ".")) return "";
        return pkg.substring(BASE.length() + 1).split("\\.")[0];
    }
}
