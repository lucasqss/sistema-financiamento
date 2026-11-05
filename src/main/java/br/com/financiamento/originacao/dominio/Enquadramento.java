package br.com.financiamento.originacao.dominio;

import br.com.financiamento.shared.dominio.Violacao;

import java.util.List;

/** Enquadramento (GLO-0033): o que falta ou viola as regras; vazio quando a proposta enquadra. */
public record Enquadramento(List<Violacao> violacoes) {

    public Enquadramento {
        violacoes = List.copyOf(violacoes);
    }

    public boolean enquadrada() {
        return violacoes.isEmpty();
    }
}
