package br.com.financiamento.desembolso.dominio;

import java.util.Map;

/** Um evento de domínio do desembolso, à espera de ir para o outbox. */
public record EventoRegistrado(String nome, Map<String, Object> dados) {
}
