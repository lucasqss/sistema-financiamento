package br.com.financiamento.shared.validacao;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * O que uma cadeia de domínio confere e carrega (ADR 0011): a chave pedida e a
 * entidade que o primeiro elo carregou. O caso de uso usa a entidade carregada,
 * sem recarregar, o que fecha a janela "validei, depois mudou".
 */
public final class ContextoDeDominio<K, T> {

    private final K chave;
    private T entidade;

    public ContextoDeDominio(K chave) {
        this.chave = chave;
    }

    public K chave() {
        return chave;
    }

    public T entidade() {
        return entidade;
    }

    public void carregou(T entidade) {
        this.entidade = entidade;
    }

    /** Elo que carrega a entidade pela chave e para com o erro dado (em geral um 404) se não houver. */
    public static <K, T> Elo<ContextoDeDominio<K, T>> carregar(Function<K, java.util.Optional<T>> busca,
                                                               ErroCatalogado seNaoExiste) {
        return (contexto, proximo) -> {
            contexto.carregou(busca.apply(contexto.chave())
                    .orElseThrow(() -> new ErroDeDominioException(seNaoExiste, contexto.chave())));
            proximo.seguir(contexto);
        };
    }

    /** Elo que confere uma condição sobre a entidade carregada e, se falhar, para com o erro e os parâmetros. */
    public static <K, T> Elo<ContextoDeDominio<K, T>> exigir(java.util.function.Predicate<T> condicao,
                                                             ErroCatalogado erro, Function<T, Object[]> parametros) {
        return (contexto, proximo) -> {
            if (!condicao.test(contexto.entidade())) {
                throw new ErroDeDominioException(erro, parametros.apply(contexto.entidade()));
            }
            proximo.seguir(contexto);
        };
    }

    /** Roda a cadeia: o carregamento, depois os elos do caso de uso. Devolve a entidade carregada. */
    @SafeVarargs
    public static <K, T> T validar(K chave, Elo<ContextoDeDominio<K, T>> carregamento, Elo<ContextoDeDominio<K, T>>... doCasoDeUso) {
        var elos = new ArrayList<Elo<ContextoDeDominio<K, T>>>();
        elos.add(carregamento);
        elos.addAll(List.of(doCasoDeUso));
        var contexto = new ContextoDeDominio<K, T>(chave);
        CadeiaDeValidacao.de(elos).validar(contexto);
        return contexto.entidade();
    }
}
