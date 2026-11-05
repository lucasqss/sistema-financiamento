package br.com.financiamento.bem.imovel;

import br.com.financiamento.bem.CategoriaDeBem;

/** Acesso de teste ao plug-in, que é fechado no próprio pacote. */
public final class ImovelParaTeste {

    private ImovelParaTeste() {
    }

    public static CategoriaDeBem plugin() {
        return new Imovel();
    }
}
