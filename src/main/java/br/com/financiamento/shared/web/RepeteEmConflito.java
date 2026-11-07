package br.com.financiamento.shared.web;

import jakarta.interceptor.InterceptorBinding;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Retornos de sistemas externos chegam ao mesmo tempo e mudam o mesmo agregado: o crédito e a avaliação da mesma
 * proposta, por exemplo. Quem perde no controle de concorrência é repetido do zero, numa transação nova (ADR 0013);
 * como o retorno é idempotente, repetir é seguro.
 */
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RepeteEmConflito {
}
