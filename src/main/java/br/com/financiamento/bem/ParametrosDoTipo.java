package br.com.financiamento.bem;

import java.math.BigDecimal;

/**
 * Uma coluna da tabela RN-0010 (parâmetros por tipo de bem).
 *
 * @param cotaMaxima         cota de financiamento máxima, em fração (0.80 = 80%)
 * @param idadeMaximaDoBem   idade máxima do bem ao fim do contrato, em anos; nula quando não se aplica
 */
public record ParametrosDoTipo(
        int prazoMinimoMeses,
        int prazoMaximoMeses,
        BigDecimal cotaMaxima,
        boolean aceitaPessoaJuridica,
        boolean aceitaFgtsNaEntrada,
        Integer idadeMaximaDoBem,
        FormaDeLiberacao liberacao) {

    public static ParametrosDoTipo de(int prazoMinimo, int prazoMaximo, String cota, boolean pj, boolean fgts,
                                      Integer idadeMaxima, FormaDeLiberacao liberacao) {
        return new ParametrosDoTipo(prazoMinimo, prazoMaximo, new BigDecimal(cota), pj, fgts, idadeMaxima, liberacao);
    }
}
