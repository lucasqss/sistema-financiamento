package br.com.financiamento.shared.validacao.atributo;

import br.com.financiamento.shared.validacao.Falha;

import java.util.ArrayList;
import java.util.List;

/** O que a cadeia de atributos confere (a requisição) e as falhas que ela acumula. */
public final class ContextoDeAtributos<R> {

    private final R requisicao;
    private final String prefixo;
    private final List<Falha> falhas;

    public ContextoDeAtributos(R requisicao) {
        this(requisicao, "", new ArrayList<>());
    }

    private ContextoDeAtributos(R requisicao, String prefixo, List<Falha> falhas) {
        this.requisicao = requisicao;
        this.prefixo = prefixo;
        this.falhas = falhas;
    }

    /** Contexto de um item aninhado ("participantes[0]."), que acumula nas mesmas falhas. */
    <I> ContextoDeAtributos<I> doItem(I item, String caminho) {
        return new ContextoDeAtributos<>(item, prefixo + caminho + ".", falhas);
    }

    public R requisicao() {
        return requisicao;
    }

    public void falhar(ErroDeAtributo erro, String campo, Object... parametros) {
        falhas.add(Falha.doCampo(erro, prefixo + campo, parametros));
    }

    public List<Falha> falhas() {
        return List.copyOf(falhas);
    }
}
