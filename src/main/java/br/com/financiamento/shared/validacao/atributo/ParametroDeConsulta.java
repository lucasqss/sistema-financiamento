package br.com.financiamento.shared.validacao.atributo;

import br.com.financiamento.shared.validacao.ErroDeAtributoException;
import br.com.financiamento.shared.validacao.Falha;

import java.util.List;

/**
 * Parâmetros de consulta (query string) que filtram uma lista: sem o filtro, a consulta não tem sentido
 * e é falha de atributo (ADR 0011), como um campo obrigatório do corpo.
 */
public final class ParametroDeConsulta {

    private ParametroDeConsulta() {
    }

    public static <T> T obrigatorio(String nome, T valor) {
        if (valor == null) {
            throw new ErroDeAtributoException(List.of(Falha.doCampo(ErroDeAtributo.OBRIGATORIO, nome)));
        }
        return valor;
    }
}
