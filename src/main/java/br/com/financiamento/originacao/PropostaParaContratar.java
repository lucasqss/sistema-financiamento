package br.com.financiamento.originacao;

import br.com.financiamento.bem.TipoDeBem;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * A proposta aprovada como a Contratação a recebe: as condições finais, que o
 * contrato congela (AGR-0002), e quem participa.
 */
public record PropostaParaContratar(
        Long propostaId,
        TipoDeBem tipoDeBem,
        String identificacaoDoBem,
        BigDecimal valorDeCompra,
        BigDecimal valorDeAvaliacao,
        String documentoDoVendedor,
        int condicoesVersao,
        BigDecimal entradaPropria,
        BigDecimal entradaFgts,
        BigDecimal valorFinanciado,
        int prazoMeses,
        BigDecimal parcela,
        List<ParticipanteDoContrato> participantes,
        LocalDate aprovacaoValidaAte) {

    /** @param papel PROPONENTE ou COOBRIGADO */
    public record ParticipanteDoContrato(String papel, String documento, String nome) {
    }
}
