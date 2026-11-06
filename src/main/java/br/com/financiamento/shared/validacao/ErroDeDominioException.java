package br.com.financiamento.shared.validacao;

import java.util.List;

/**
 * A regra de negócio não deixa (422), ou o que se pede não existe (404). O status
 * vem da primeira falha.
 */
public class ErroDeDominioException extends RuntimeException {

    private final List<Falha> falhas;

    public ErroDeDominioException(List<Falha> falhas) {
        super(falhas.stream().map(Falha::toString).reduce((a, b) -> a + "; " + b).orElse("regra de negócio"));
        if (falhas.isEmpty()) throw new IllegalArgumentException("Erro de domínio sem falha");
        this.falhas = List.copyOf(falhas);
    }

    public ErroDeDominioException(ErroCatalogado erro, Object... parametros) {
        this(List.of(Falha.de(erro, parametros)));
    }

    public List<Falha> falhas() {
        return falhas;
    }

    public int status() {
        return falhas.getFirst().erro().status();
    }
}
