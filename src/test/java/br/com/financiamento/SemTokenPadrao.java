package br.com.financiamento;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** O teste escolhe o token de cada chamada: o token padrão do operador não é aplicado. */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SemTokenPadrao {
}
