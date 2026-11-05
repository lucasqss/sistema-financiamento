package br.com.financiamento.credito;

import java.math.BigDecimal;
import java.util.List;

/**
 * O que a Originação manda ao Crédito na submissão: as condições de uma versão
 * e os participantes, por CPF/CNPJ.
 */
public record SolicitacaoDeAnalise(
        Long propostaId,
        int condicoesVersao,
        List<String> participantes,
        BigDecimal rendaComprovadaSomada,
        BigDecimal valorFinanciado,
        int prazoMeses,
        BigDecimal parcela) {
}
