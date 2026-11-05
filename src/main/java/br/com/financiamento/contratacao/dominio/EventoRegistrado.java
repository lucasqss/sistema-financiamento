package br.com.financiamento.contratacao.dominio;

import java.util.Map;

/** Um evento de domínio da contratação, à espera de ir para o outbox. */
public record EventoRegistrado(String nome, Map<String, Object> dados) {
}
