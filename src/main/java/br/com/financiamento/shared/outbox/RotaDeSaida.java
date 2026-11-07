package br.com.financiamento.shared.outbox;

/**
 * Porta de saída para um sistema externo (ADR 0013). A camada anticorrupção do módulo dono implementa:
 * reconhece o evento que é pedido ao externo, traduz para o formato dele e envia.
 */
public interface RotaDeSaida {

    /** O nome do sistema externo, para log e métrica (ex.: "bureau"). */
    String externo();

    boolean atende(EventoDoOutbox evento);

    /** Envia o pedido e diz se o externo recebeu, se vale tentar de novo ou se ele recusou. */
    Envio enviar(EventoDoOutbox evento);
}
