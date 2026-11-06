package br.com.financiamento.shared.validacao;

import java.util.List;

/** A requisição chegou com campos fora de forma ou formato (400). Leva todas as falhas de uma vez. */
public class ErroDeAtributoException extends RuntimeException {

    private final List<Falha> falhas;

    public ErroDeAtributoException(List<Falha> falhas) {
        super(falhas.stream().map(Falha::toString).reduce((a, b) -> a + "; " + b).orElse("atributo inválido"));
        this.falhas = List.copyOf(falhas);
    }

    public List<Falha> falhas() {
        return falhas;
    }
}
