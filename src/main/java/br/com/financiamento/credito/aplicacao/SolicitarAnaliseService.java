package br.com.financiamento.credito.aplicacao;

import br.com.financiamento.credito.SolicitacaoDeAnalise;
import br.com.financiamento.credito.dominio.AnaliseDeCredito;
import br.com.financiamento.credito.dominio.AnaliseDeCreditoRepository;
import br.com.financiamento.shared.outbox.Outbox;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Clock;
import java.util.Map;

/** UC-0011, passo 1: registra a análise e pede a consulta ao bureau pelo outbox. Emite AnaliseCreditoSolicitada. */
@ApplicationScoped
public class SolicitarAnaliseService {

    @Inject
    AnaliseDeCreditoRepository analises;

    @Inject
    Outbox outbox;

    @Inject
    Clock relogio;

    @Transactional
    public Long executar(SolicitacaoDeAnalise s) {
        var analise = AnaliseDeCredito.solicitar(s.propostaId(), s.condicoesVersao(), s.valorFinanciado(),
                s.prazoMeses(), s.parcela(), s.rendaComprovadaSomada(), relogio.instant());
        analises.persistAndFlush(analise);
        outbox.registrar("AnaliseDeCredito", analise.id(), "AnaliseCreditoSolicitada", Map.of(
                "propostaId", s.propostaId(),
                "condicoesVersao", s.condicoesVersao(),
                "participantes", s.participantes(),
                "valorFinanciado", s.valorFinanciado(),
                "prazoMeses", s.prazoMeses(),
                "parcela", s.parcela()));
        return analise.id();
    }
}
