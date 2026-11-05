package br.com.financiamento.shared.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Clock;
import java.util.Map;

/**
 * Registra eventos de domínio no outbox. Exige transação aberta: o evento só
 * existe se a mudança do agregado for confirmada junto (ADR 0005).
 */
@ApplicationScoped
public class Outbox {

    @Inject
    ObjectMapper json;

    @Inject
    Clock relogio;

    @Transactional(Transactional.TxType.MANDATORY)
    public void registrar(String agregado, Object agregadoId, String evento, Map<String, ?> dados) {
        var m = new MensagemOutbox();
        m.agregado = agregado;
        m.agregadoId = String.valueOf(agregadoId);
        m.evento = evento;
        try {
            m.dados = json.writeValueAsString(dados);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Dados do evento " + evento + " não serializáveis", e);
        }
        m.ocorridoEm = relogio.instant();
        m.persist();
    }
}
