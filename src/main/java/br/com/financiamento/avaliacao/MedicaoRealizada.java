package br.com.financiamento.avaliacao;

import java.math.BigDecimal;
import java.time.LocalDate;

/** O engenheiro apurou o percentual acumulado executado da obra (EVT-0020, RN-0024). */
public record MedicaoRealizada(Long medicaoId, Long contratoId, BigDecimal percentualAcumulado, LocalDate medidaEm) {
}
