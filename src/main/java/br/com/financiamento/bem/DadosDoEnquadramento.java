package br.com.financiamento.bem;

import java.time.LocalDate;

/**
 * O que um plug-in de categoria precisa saber da proposta para aplicar as
 * regras próprias da categoria. Campos que não se aplicam vêm nulos.
 */
public record DadosDoEnquadramento(
        TipoDeBem tipo,
        LocalDate dataDeReferencia,
        Integer prazoMeses,
        Integer anoModelo,
        Boolean construcaoAverbada,
        FormaDoParticipante formaDoProponente,
        LocalDate nascimentoDoProponente) {
}
