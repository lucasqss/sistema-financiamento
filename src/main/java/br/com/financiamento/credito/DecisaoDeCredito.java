package br.com.financiamento.credito;

/**
 * A decisão de crédito, entregue como fato a quem acompanha (ADR 0009).
 *
 * @param condicoesVersao a versão das condições que foi analisada
 * @param sugeridas       só na aprovação com ressalva
 */
public record DecisaoDeCredito(
        Long analiseId,
        Long propostaId,
        int condicoesVersao,
        ResultadoDeCredito resultado,
        String ressalva,
        CondicoesSugeridas sugeridas) {
}
