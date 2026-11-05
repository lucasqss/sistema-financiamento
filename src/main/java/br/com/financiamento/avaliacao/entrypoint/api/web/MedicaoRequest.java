package br.com.financiamento.avaliacao.entrypoint.api.web;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MedicaoRequest(Long contratoId, BigDecimal percentualAcumulado, LocalDate medidaEm) {
}
