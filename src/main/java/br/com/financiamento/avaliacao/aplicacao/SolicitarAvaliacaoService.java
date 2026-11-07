package br.com.financiamento.avaliacao.aplicacao;

import br.com.financiamento.avaliacao.SolicitacaoDeAvaliacao;
import br.com.financiamento.avaliacao.dominio.Avaliacao;
import br.com.financiamento.avaliacao.dominio.AvaliacaoRepository;
import br.com.financiamento.bem.BemFacade;
import br.com.financiamento.shared.outbox.Outbox;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Clock;
import java.util.Map;

/** UC-0012, passo 1: pede a avaliação na fonte do tipo de bem (RN-0023). Emite AvaliacaoSolicitada. */
@ApplicationScoped
public class SolicitarAvaliacaoService {

    @Inject
    AvaliacaoRepository avaliacoes;

    @Inject
    BemFacade bens;

    @Inject
    Outbox outbox;

    @Inject
    Clock relogio;

    @Transactional
    public Long executar(SolicitacaoDeAvaliacao s) {
        var fonte = bens.categoria(s.tipoDeBem()).fonteDaAvaliacao(s.tipoDeBem());
        var avaliacao = Avaliacao.solicitar(s.propostaId(), s.tipoDeBem(), s.identificacaoDoBem(), s.valorDeCompra(),
                fonte, relogio.instant());
        avaliacoes.persistAndFlush(avaliacao);
        outbox.registrar("Avaliacao", avaliacao.id(), "AvaliacaoSolicitada", Map.of(
                "propostaId", s.propostaId(),
                "tipoDeBem", s.tipoDeBem().codigo(),
                "identificacaoDoBem", s.identificacaoDoBem(),
                "valorDeCompra", s.valorDeCompra(),
                "fonte", fonte));
        return avaliacao.id();
    }
}
