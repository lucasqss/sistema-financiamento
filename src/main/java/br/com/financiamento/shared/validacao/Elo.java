package br.com.financiamento.shared.validacao;

/**
 * Um elo da cadeia de validação (Chain of Responsibility, ADR 0011). Recebe o
 * contexto e a continuação, e decide se segue: o elo de atributo registra a falha
 * e sempre segue; o elo de domínio lança o erro e a cadeia para ali.
 */
@FunctionalInterface
public interface Elo<C> {

    void validar(C contexto, Continuacao<C> proximo);

    /** O resto da cadeia, a partir do próximo elo. */
    @FunctionalInterface
    interface Continuacao<C> {
        void seguir(C contexto);
    }
}
