package br.com.financiamento.shared.outbox;

import io.quarkus.arc.Arc;

/** Dá aos testes de outros pacotes o relay do outbox, que no código é restrito ao pacote. */
public final class RelayParaTeste {

    private RelayParaTeste() {
    }

    /** Roda o relay uma vez, como o agendamento faria. */
    public static int enviarPendentes() {
        return Arc.container().instance(RelayDoOutbox.class).get().enviarPendentes();
    }
}
