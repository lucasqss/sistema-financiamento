package br.com.financiamento.shared.dominio;

import java.util.List;

/**
 * Uma operação foi recusada por regra de negócio. Leva o código da regra do
 * modelo (ex.: RN-0018) e o que falta ou viola, em linguagem de negócio.
 */
public class RegraVioladaException extends RuntimeException {

    private final List<Violacao> violacoes;

    public RegraVioladaException(List<Violacao> violacoes) {
        super(violacoes.stream().map(Violacao::toString).reduce((a, b) -> a + "; " + b).orElse("regra violada"));
        this.violacoes = List.copyOf(violacoes);
    }

    public RegraVioladaException(String regra, String mensagem) {
        this(List.of(new Violacao(regra, mensagem)));
    }

    public List<Violacao> violacoes() {
        return violacoes;
    }
}
