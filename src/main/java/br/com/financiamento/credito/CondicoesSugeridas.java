package br.com.financiamento.credito;

import java.math.BigDecimal;

/** As condições com que o crédito aprova quando há ressalva (base da contraproposta, RN-0020). */
public record CondicoesSugeridas(BigDecimal entradaPropria, BigDecimal entradaFgts, int prazoMeses, BigDecimal parcela) {
}
