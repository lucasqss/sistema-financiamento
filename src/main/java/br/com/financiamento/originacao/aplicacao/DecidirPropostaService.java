package br.com.financiamento.originacao.aplicacao;

import br.com.financiamento.avaliacao.ResultadoDeAvaliacao;
import br.com.financiamento.credito.DecisaoDeCredito;
import br.com.financiamento.originacao.dominio.resultado.AvaliacaoDaProposta;
import br.com.financiamento.originacao.dominio.resultado.CreditoDaProposta;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * UC-0006: decidir proposta. Automático, disparado pelos resultados do crédito
 * (EVT-0013 a EVT-0015) e da avaliação (EVT-0017, EVT-0018).
 */
@ApplicationScoped
public class DecidirPropostaService {

    @Inject
    ElosDaProposta elos;

    @Inject
    Propostas propostas;

    @Transactional
    public void creditoDecidido(DecisaoDeCredito d) {
        var proposta = elos.validar(d.propostaId());
        if (!proposta.registrarCredito(new CreditoDaProposta(d.resultado(), d.condicoesVersao(), d.ressalva(), d.sugeridas()))) {
            return;
        }
        propostas.salvar(proposta, propostas.decidir(proposta));
    }

    @Transactional
    public void bemAvaliado(ResultadoDeAvaliacao r) {
        var proposta = elos.validar(r.propostaId());
        if (!proposta.registrarAvaliacao(new AvaliacaoDaProposta(r.aceitoComoGarantia(), r.valor(), r.fonte(),
                r.avaliadaEm(), r.validaAte()))) {
            return;
        }
        propostas.salvar(proposta, propostas.decidir(proposta));
    }
}
