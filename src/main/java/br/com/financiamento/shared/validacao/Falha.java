package br.com.financiamento.shared.validacao;

import java.util.List;

/**
 * Uma falha de validação: o erro do catálogo, o campo (nas falhas de atributo) e
 * os parâmetros da mensagem.
 */
public record Falha(ErroCatalogado erro, String campo, List<Object> parametros) {

    public Falha {
        // Parâmetro pode ser nulo (ex.: id ainda não gerado): a mensagem mostra "null", sem quebrar.
        parametros = parametros == null ? List.of() : java.util.Collections.unmodifiableList(new java.util.ArrayList<>(parametros));
    }

    public static Falha de(ErroCatalogado erro, Object... parametros) {
        return new Falha(erro, null, java.util.Arrays.asList(parametros));
    }

    public static Falha doCampo(ErroCatalogado erro, String campo, Object... parametros) {
        return new Falha(erro, campo, java.util.Arrays.asList(parametros));
    }

    public String mensagem() {
        return erro.mensagem(parametros.toArray());
    }

    public String codigo() {
        return erro.codigo();
    }

    public String regra() {
        return erro.regraDoModelo();
    }

    @Override
    public String toString() {
        return codigo() + (regra() == null ? "" : " " + regra()) + (campo == null ? "" : " [" + campo + "]") + ": " + mensagem();
    }
}
