package br.com.financiamento.contratacao.aplicacao;

import br.com.financiamento.contratacao.dominio.PreparoDoContrato;
import br.com.financiamento.contratacao.dominio.seguro.Seguro;

import java.util.List;

/**
 * Resultado do aceite e da emissão. Impedida (proposta fora da validade, avaliação
 * vencida), a contratação não é recusada como erro: o impedimento é um fato que
 * já mudou a proposta, e volta aqui para o canal mostrar.
 */
public record DetalheDaContratacao(Long id, Long propostaId, List<Seguro> seguros, Long contratoId, String impedimento) {

    static DetalheDaContratacao de(PreparoDoContrato p) {
        return new DetalheDaContratacao(p.id(), p.propostaId(), p.seguros(), p.contratoId(), p.impedimento());
    }

    static DetalheDaContratacao impedida(Long propostaId, String impedimento) {
        return new DetalheDaContratacao(null, propostaId, List.of(), null, impedimento);
    }
}
