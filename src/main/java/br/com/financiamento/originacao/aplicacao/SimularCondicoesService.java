package br.com.financiamento.originacao.aplicacao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;

/**
 * UC-0004: simular e escolher condições. Não recusa: devolve o enquadramento
 * para o proponente ajustar entrada e prazo.
 */
@ApplicationScoped
public class SimularCondicoesService {

    @Inject
    ElosDaProposta elos;

    public record NovasCondicoes(BigDecimal entradaPropria, BigDecimal entradaFgts, int prazoMeses, BigDecimal parcela) {
    }

    @Inject
    Propostas propostas;

    @Transactional
    public DetalheProposta executar(Long propostaId, NovasCondicoes c) {
        var proposta = elos.validar(propostaId, elos.aceitaAlteracao());
        proposta.informarCondicoes(c.entradaPropria(), c.entradaFgts(), c.prazoMeses(), c.parcela());
        return propostas.salvar(proposta);
    }
}
