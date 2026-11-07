package br.com.financiamento.shared.outbox;

import jakarta.ws.rs.WebApplicationException;

/**
 * O resultado de um envio a um sistema externo (ADR 0013): entregue; falha temporária (fora do ar, tempo
 * esgotado, 5xx), e o relay tenta de novo com recuo; ou recusa (4xx), e o relay desiste e avisa.
 */
public record Envio(Resultado resultado, String motivo) {

    public enum Resultado { ENTREGUE, TEMPORARIA, RECUSADA }

    public static Envio entregue() {
        return new Envio(Resultado.ENTREGUE, null);
    }

    public static Envio temporaria(String motivo) {
        return new Envio(Resultado.TEMPORARIA, motivo);
    }

    public static Envio recusada(String motivo) {
        return new Envio(Resultado.RECUSADA, motivo);
    }

    /** Faz a chamada HTTP ao externo e classifica o que der errado: 4xx é recusa, o resto é temporário. */
    public static Envio porHttp(Runnable chamada) {
        try {
            chamada.run();
            return entregue();
        } catch (WebApplicationException e) {
            int status = e.getResponse().getStatus();
            var motivo = "HTTP " + status + ": " + e.getMessage();
            return status >= 400 && status < 500 ? recusada(motivo) : temporaria(motivo);
        } catch (RuntimeException e) {
            return temporaria(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}
