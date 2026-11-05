package br.com.financiamento.originacao.entrypoint.agenda;

import br.com.financiamento.originacao.aplicacao.VerificarPrazosService;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Clock;
import java.time.LocalDate;

/** A passagem do tempo como entrada: uma vez por dia, logo após a meia-noite. */
@ApplicationScoped
class PrazosDasPropostas {

    @Inject
    VerificarPrazosService verificar;

    @Inject
    Clock relogio;

    @Scheduled(cron = "{originacao.prazos.cron}", identity = "prazos-das-propostas")
    void verificarPrazos() {
        verificar.executar(LocalDate.now(relogio));
    }
}
