package br.com.financiamento.arquitetura;

import br.com.financiamento.shared.validacao.CadeiaDeValidacao;
import br.com.financiamento.shared.validacao.ContextoDeDominio;
import br.com.financiamento.shared.validacao.ErroCatalogado;
import br.com.financiamento.shared.validacao.atributo.CadeiaDeAtributos;
import br.com.financiamento.shared.validacao.atributo.ElosDeAtributo;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Fitness functions do fluxo comum de validação (ADR 0011): toda requisição passa
 * pela cadeia de atributos do seu request e pela cadeia de domínio do caso de uso,
 * e todo erro de negócio sai do catálogo.
 */
class ValidacaoTest {

    static final String BASE = ArquiteturaTest.BASE;
    static JavaClasses classes;

    @BeforeAll
    static void importar() {
        classes = new ClassFileImporter().withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS).importPackages(BASE);
    }

    /** Todo *Request tem o seu *RequestValidador no mesmo pacote. */
    @Test
    void todoRequestTemValidador() {
        classes().that().haveSimpleNameEndingWith("Request").and().areNotNestedClasses()
                .should(new ArchCondition<JavaClass>("ter um <Request>Validador no mesmo pacote") {
                    @Override
                    public void check(JavaClass request, ConditionEvents events) {
                        if (!classes.contain(request.getName() + "Validador")) {
                            events.add(SimpleConditionEvent.violated(request, request.getName() + " não tem " + request.getSimpleName() + "Validador"));
                        }
                    }
                }).check(classes);
    }

    /** Todo resource que recebe um request chama o validador dele. */
    @Test
    void resourceQueRecebeRequestChamaOValidador() {
        classes().that().haveSimpleNameEndingWith("Resource")
                .should(new ArchCondition<JavaClass>("chamar o validador de cada request que recebe") {
                    @Override
                    public void check(JavaClass resource, ConditionEvents events) {
                        for (var metodo : resource.getMethods()) {
                            for (var parametro : metodo.getRawParameterTypes()) {
                                if (!parametro.getSimpleName().endsWith("Request")) continue;
                                var validador = parametro.getName() + "Validador";
                                boolean chama = metodo.getMethodCallsFromSelf().stream()
                                        .anyMatch(c -> c.getTargetOwner().getName().equals(validador) && c.getName().equals("validar"));
                                if (!chama) {
                                    events.add(SimpleConditionEvent.violated(metodo, metodo.getFullName() + " não chama "
                                            + parametro.getSimpleName() + "Validador.validar"));
                                }
                            }
                        }
                    }
                }).check(classes);
    }

    /**
     * Todo caso de uso chamado por uma entrada (API ou retorno) roda uma cadeia de
     * domínio: depende de CadeiaDeValidacao ou ContextoDeDominio, direto ou por um
     * apoio do próprio pacote (ElosDaProposta, Contratacoes…). Consultas (Consultar*) não alteram nada e ficam de fora.
     */
    @Test
    void casoDeUsoChamadoPorEntradaRodaCadeiaDeDominio() {
        var problemas = new HashSet<String>();
        for (var service : classes) {
            if (!service.getPackageName().endsWith(".aplicacao") || !service.getSimpleName().endsWith("Service")
                    || service.getSimpleName().startsWith("Consultar")) continue;
            boolean chamadoPorEntrada = service.getDirectDependenciesToSelf().stream()
                    .anyMatch(d -> d.getOriginClass().getPackageName().contains(".entrypoint.api.")
                            || d.getOriginClass().getPackageName().endsWith(".entrypoint.retorno"));
            if (chamadoPorEntrada && !rodaCadeia(service, new HashMap<>())) problemas.add(service.getName());
        }
        assertTrue(problemas.isEmpty(), "Casos de uso sem cadeia de domínio: " + problemas);
    }

    private static boolean rodaCadeia(JavaClass c, HashMap<String, Boolean> vistos) {
        if (vistos.containsKey(c.getName())) return vistos.get(c.getName());
        vistos.put(c.getName(), false);
        for (var d : c.getDirectDependenciesFromSelf()) {
            var alvo = d.getTargetClass();
            if (alvo.isEquivalentTo(CadeiaDeValidacao.class) || alvo.isEquivalentTo(ContextoDeDominio.class)) {
                vistos.put(c.getName(), true);
                return true;
            }
            if (alvo.getPackageName().equals(c.getPackageName()) && !alvo.equals(c) && rodaCadeia(alvo, vistos)) {
                vistos.put(c.getName(), true);
                return true;
            }
        }
        return false;
    }

    /** Erro de negócio só sai do catálogo: nenhuma exceção própria fora de shared.validacao. */
    @Test
    void nenhumaExcecaoPropriaForaDoCatalogo() {
        noClasses().that().resideOutsideOfPackage(BASE + ".shared.validacao..")
                .should().beAssignableTo(RuntimeException.class)
                .because("erros de negócio são entradas do catálogo (ErroCatalogado) lançadas como ErroDeAtributoException ou ErroDeDominioException")
                .check(classes);
    }

    /** Cada catálogo fica no domínio do seu módulo (ou em shared.validacao, para os erros de atributo). */
    @Test
    void catalogosNoDominioDoModulo() {
        classes().that().implement(ErroCatalogado.class)
                .should().resideInAnyPackage("..dominio..", BASE + ".shared.validacao..", BASE + ".bem")
                .check(classes);
    }

    /** Códigos do catálogo são únicos em todo o sistema: é o que se mapeia para o catálogo corporativo. */
    @Test
    void codigosDoCatalogoSaoUnicos() throws Exception {
        Set<String> vistos = new HashSet<>();
        var repetidos = new HashSet<String>();
        for (var c : classes) {
            if (!c.isEnum() || !c.isAssignableTo(ErroCatalogado.class)) continue;
            for (var constante : c.reflect().getEnumConstants()) {
                var codigo = ((ErroCatalogado) constante).codigo();
                if (!vistos.add(codigo)) repetidos.add(codigo);
            }
        }
        assertTrue(repetidos.isEmpty(), "Códigos repetidos no catálogo: " + repetidos);
        assertTrue(vistos.size() > 50, "catálogo lido: " + vistos.size());
    }

    /** As cadeias ficam na camada certa: atributo na entrada, domínio na aplicação. */
    @Test
    void cadeiasNaCamadaCerta() {
        noClasses().that().resideOutsideOfPackages("..entrypoint..", BASE + ".shared.validacao..")
                .should().dependOnClassesThat().belongToAnyOf(CadeiaDeAtributos.class, ElosDeAtributo.class)
                .check(classes);
        noClasses().that().resideOutsideOfPackages("..aplicacao..", BASE + ".shared.validacao..")
                .should().dependOnClassesThat().belongToAnyOf(ContextoDeDominio.class, CadeiaDeValidacao.class)
                .check(classes);
    }
}
