package br.com.financiamento.avaliacao.entrypoint.retorno;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LaudoRequest(boolean aceitoComoGarantia, BigDecimal valor, LocalDate avaliadaEm) {
}
