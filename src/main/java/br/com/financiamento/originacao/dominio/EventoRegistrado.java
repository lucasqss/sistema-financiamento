package br.com.financiamento.originacao.dominio;

import java.util.Map;

/** Um evento de domínio emitido pela proposta, à espera de ir para o outbox. */
public record EventoRegistrado(String nome, Map<String, Object> dados) {
}
