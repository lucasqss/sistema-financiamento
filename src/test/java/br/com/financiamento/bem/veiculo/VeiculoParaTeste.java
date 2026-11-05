package br.com.financiamento.bem.veiculo;

import br.com.financiamento.bem.CategoriaDeBem;

/** Acesso de teste ao plug-in, que é fechado no próprio pacote. */
public final class VeiculoParaTeste {

    private VeiculoParaTeste() {
    }

    public static CategoriaDeBem plugin() {
        return new Veiculo();
    }
}
