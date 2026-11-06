package br.com.financiamento.shared.validacao.atributo;

import br.com.financiamento.shared.validacao.CadeiaDeValidacao;
import br.com.financiamento.shared.validacao.Elo;
import br.com.financiamento.shared.validacao.ErroDeAtributoException;

import java.util.List;

/**
 * A cadeia de validação de atributos de uma requisição: roda todos os elos e, no
 * fim, lança {@link ErroDeAtributoException} com todas as falhas, se houver.
 */
public final class CadeiaDeAtributos<R> {

    private final CadeiaDeValidacao<ContextoDeAtributos<R>> cadeia;

    private CadeiaDeAtributos(List<Elo<ContextoDeAtributos<R>>> elos) {
        this.cadeia = CadeiaDeValidacao.de(elos);
    }

    @SafeVarargs
    public static <R> CadeiaDeAtributos<R> de(Elo<ContextoDeAtributos<R>>... elos) {
        return new CadeiaDeAtributos<>(List.of(elos));
    }

    public void validar(R requisicao) {
        var contexto = new ContextoDeAtributos<>(requisicao);
        if (requisicao == null) {
            contexto.falhar(ErroDeAtributo.OBRIGATORIO, "corpo");
        } else {
            cadeia.validar(contexto);
        }
        if (!contexto.falhas().isEmpty()) throw new ErroDeAtributoException(contexto.falhas());
    }

    /** Roda os elos sobre um item aninhado, acumulando no contexto do pai. */
    void validarItem(ContextoDeAtributos<R> contextoDoItem) {
        cadeia.validar(contextoDoItem);
    }
}
