package br.com.financiamento.shared.outbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.UncheckedIOException;
import java.time.Instant;

/** Um evento gravado no outbox, como o relay o entrega às rotas de saída (ADR 0013). */
public record EventoDoOutbox(Long id, String agregado, String agregadoId, String evento, String dadosJson,
                             Instant ocorridoEm) {

    private static final ObjectMapper JSON = new ObjectMapper();

    public boolean e(String agregado, String evento) {
        return this.agregado.equals(agregado) && this.evento.equals(evento);
    }

    public long idDoAgregado() {
        return Long.parseLong(agregadoId);
    }

    public JsonNode dados() {
        try {
            return JSON.readTree(dadosJson);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }
}
