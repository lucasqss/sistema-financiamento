package br.com.financiamento.shared.validacao;

import java.util.List;

/** Encadeia os elos na ordem dada e passa o contexto do primeiro ao último (ADR 0011). */
public final class CadeiaDeValidacao<C> {

    private final List<Elo<C>> elos;

    private CadeiaDeValidacao(List<Elo<C>> elos) {
        this.elos = List.copyOf(elos);
    }

    @SafeVarargs
    public static <C> CadeiaDeValidacao<C> de(Elo<C>... elos) {
        return new CadeiaDeValidacao<>(List.of(elos));
    }

    public static <C> CadeiaDeValidacao<C> de(List<Elo<C>> elos) {
        return new CadeiaDeValidacao<>(elos);
    }

    public void validar(C contexto) {
        seguir(0, contexto);
    }

    private void seguir(int indice, C contexto) {
        if (indice < elos.size()) elos.get(indice).validar(contexto, c -> seguir(indice + 1, c));
    }
}
