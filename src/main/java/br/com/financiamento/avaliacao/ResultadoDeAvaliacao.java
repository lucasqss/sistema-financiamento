package br.com.financiamento.avaliacao;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * O bem foi avaliado (EVT-0017) ou recusado como garantia (EVT-0018), entregue
 * como fato a quem acompanha a avaliação (ADR 0009).
 *
 * @param valor     valor de avaliação (GLO-0014); nulo quando recusado
 * @param validaAte até quando a avaliação vale (RN-0023)
 */
public record ResultadoDeAvaliacao(Long avaliacaoId, Long propostaId, boolean aceitoComoGarantia, BigDecimal valor,
                                   String fonte, LocalDate avaliadaEm, LocalDate validaAte) {
}
