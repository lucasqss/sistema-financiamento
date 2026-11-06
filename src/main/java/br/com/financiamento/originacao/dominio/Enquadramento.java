package br.com.financiamento.originacao.dominio;

import br.com.financiamento.shared.validacao.Falha;

import java.util.List;

/** Enquadramento (GLO-0033): o que falta ou viola as regras; vazio quando a proposta enquadra. */
public record Enquadramento(List<Falha> falhas) {

    public Enquadramento {
        falhas = List.copyOf(falhas);
    }

    public boolean enquadrada() {
        return falhas.isEmpty();
    }
}
