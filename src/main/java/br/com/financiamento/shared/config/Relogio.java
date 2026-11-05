package br.com.financiamento.shared.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import java.time.Clock;

/** O relógio do sistema, injetável para que regras com prazo sejam testáveis. */
class Relogio {

    @Produces
    @ApplicationScoped
    Clock relogio() {
        return Clock.systemDefaultZone();
    }
}
