package br.com.financiamento.avaliacao;

/**
 * Porta de saída dos resultados da Avaliação (ADR 0009). Quem depende da
 * Avaliação (a Originação) a implementa; a Avaliação chama todos os ouvintes
 * na mesma transação, sem conhecer quem são.
 */
public interface OuvinteDaAvaliacao {

    void bemAvaliado(ResultadoDeAvaliacao resultado);

    /** MedicaoRealizada dispara a liberação da parcela da obra (UC-0023). Quem não a acompanha não faz nada. */
    default void medicaoRealizada(MedicaoRealizada medicao) {
    }
}
