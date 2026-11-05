package br.com.financiamento.garantia.dominio;

import java.util.Map;

/** Um evento de domínio da garantia, à espera de ir para o outbox. */
public record EventoRegistrado(String nome, Map<String, Object> dados) {
}
