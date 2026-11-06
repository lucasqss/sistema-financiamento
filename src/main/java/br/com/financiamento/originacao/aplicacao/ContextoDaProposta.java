package br.com.financiamento.originacao.aplicacao;

import br.com.financiamento.originacao.dominio.Proposta;

/**
 * O que a cadeia de domínio de um caso de uso da proposta confere e carrega
 * (ADR 0011): o caso de uso usa a proposta que a cadeia carregou, sem recarregar.
 */
final class ContextoDaProposta {

    private final Long propostaId;
    private Proposta proposta;

    ContextoDaProposta(Long propostaId) {
        this.propostaId = propostaId;
    }

    Long propostaId() {
        return propostaId;
    }

    Proposta proposta() {
        return proposta;
    }

    void carregou(Proposta proposta) {
        this.proposta = proposta;
    }
}
