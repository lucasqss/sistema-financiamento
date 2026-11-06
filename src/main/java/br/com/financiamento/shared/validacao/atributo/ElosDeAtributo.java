package br.com.financiamento.shared.validacao.atributo;

import br.com.financiamento.shared.dominio.DocumentoFiscal;
import br.com.financiamento.shared.validacao.Elo;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Os elos de atributo reutilizáveis (ADR 0011). Cada um confere um campo da
 * requisição, registra a falha no contexto e sempre segue para o próximo elo, para
 * que a requisição volte com todos os campos errados de uma vez. Campo vazio só é
 * conferido por {@link #obrigatorio}; os demais elos o ignoram.
 */
public final class ElosDeAtributo {

    private ElosDeAtributo() {
    }

    private static <R, V> Elo<ContextoDeAtributos<R>> elo(Function<R, V> campo, java.util.function.BiConsumer<ContextoDeAtributos<R>, V> conferir) {
        return (contexto, proximo) -> {
            var valor = campo.apply(contexto.requisicao());
            if (valor != null) conferir.accept(contexto, valor);
            proximo.seguir(contexto);
        };
    }

    public static <R> Elo<ContextoDeAtributos<R>> obrigatorio(String nome, Function<R, ?> campo) {
        return (contexto, proximo) -> {
            var valor = campo.apply(contexto.requisicao());
            if (valor == null || (valor instanceof String s && s.isBlank())
                    || (valor instanceof Collection<?> c && c.isEmpty())) {
                contexto.falhar(ErroDeAtributo.OBRIGATORIO, nome);
            }
            proximo.seguir(contexto);
        };
    }

    public static <R> Elo<ContextoDeAtributos<R>> tamanhoMaximo(String nome, int maximo, Function<R, String> campo) {
        return elo(campo, (c, v) -> {
            if (v.length() > maximo) c.falhar(ErroDeAtributo.TAMANHO_MAXIMO, nome, maximo);
        });
    }

    public static <R> Elo<ContextoDeAtributos<R>> formato(String nome, String regex, String descricao, Function<R, String> campo) {
        return elo(campo, (c, v) -> {
            if (!v.matches(regex)) c.falhar(ErroDeAtributo.FORMATO, nome, descricao);
        });
    }

    public static <R> Elo<ContextoDeAtributos<R>> cpf(String nome, Function<R, String> campo) {
        return elo(campo, (c, v) -> {
            if (!DocumentoFiscal.cpfValido(v)) c.falhar(ErroDeAtributo.CPF_INVALIDO, nome);
        });
    }

    public static <R> Elo<ContextoDeAtributos<R>> cnpj(String nome, Function<R, String> campo) {
        return elo(campo, (c, v) -> {
            if (!DocumentoFiscal.cnpjValido(v)) c.falhar(ErroDeAtributo.CNPJ_INVALIDO, nome);
        });
    }

    public static <R> Elo<ContextoDeAtributos<R>> cpfOuCnpj(String nome, Function<R, String> campo) {
        return elo(campo, (c, v) -> {
            if (!DocumentoFiscal.cpfValido(v) && !DocumentoFiscal.cnpjValido(v)) {
                c.falhar(ErroDeAtributo.CPF_OU_CNPJ_INVALIDO, nome);
            }
        });
    }

    public static <R> Elo<ContextoDeAtributos<R>> maiorQueZero(String nome, Function<R, ? extends Number> campo) {
        return elo(campo, (c, v) -> {
            if (new BigDecimal(v.toString()).signum() <= 0) c.falhar(ErroDeAtributo.MAIOR_QUE_ZERO, nome);
        });
    }

    public static <R> Elo<ContextoDeAtributos<R>> naoNegativo(String nome, Function<R, ? extends Number> campo) {
        return elo(campo, (c, v) -> {
            if (new BigDecimal(v.toString()).signum() < 0) c.falhar(ErroDeAtributo.NAO_NEGATIVO, nome);
        });
    }

    public static <R> Elo<ContextoDeAtributos<R>> noMaximo(String nome, Number maximo, Function<R, ? extends Number> campo) {
        return elo(campo, (c, v) -> {
            if (new BigDecimal(v.toString()).compareTo(new BigDecimal(maximo.toString())) > 0) {
                c.falhar(ErroDeAtributo.NO_MAXIMO, nome, maximo);
            }
        });
    }

    public static <R> Elo<ContextoDeAtributos<R>> umDe(String nome, Set<String> aceitos, Function<R, String> campo) {
        return elo(campo, (c, v) -> {
            if (!aceitos.contains(v)) c.falhar(ErroDeAtributo.FORA_DA_LISTA, nome, String.join(", ", new java.util.TreeSet<>(aceitos)));
        });
    }

    public static <R> Elo<ContextoDeAtributos<R>> quantidadeMaxima(String nome, int maximo, Function<R, ? extends Collection<?>> campo) {
        return elo(campo, (c, v) -> {
            if (v.size() > maximo) c.falhar(ErroDeAtributo.QUANTIDADE_MAXIMA, nome, maximo);
        });
    }

    /** Confere cada item de uma lista com a cadeia do item; as falhas saem como "lista[i].campo". */
    public static <R, I> Elo<ContextoDeAtributos<R>> cadaItem(String nome, Function<R, List<I>> campo, CadeiaDeAtributos<I> cadeiaDoItem) {
        return elo(campo, (c, lista) -> {
            for (int i = 0; i < lista.size(); i++) {
                var item = lista.get(i);
                if (item == null) {
                    c.falhar(ErroDeAtributo.OBRIGATORIO, nome + "[" + i + "]");
                } else {
                    cadeiaDoItem.validarItem(c.doItem(item, nome + "[" + i + "]"));
                }
            }
        });
    }
}
