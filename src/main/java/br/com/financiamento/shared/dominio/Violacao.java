package br.com.financiamento.shared.dominio;

/** O que uma regra do modelo de negócio recusou, com o código da regra. */
public record Violacao(String regra, String mensagem) {

    @Override
    public String toString() {
        return regra + ": " + mensagem;
    }
}
