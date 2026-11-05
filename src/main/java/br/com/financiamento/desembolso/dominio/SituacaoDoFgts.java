package br.com.financiamento.desembolso.dominio;

/** O FGTS da entrada (RN-0015) no desembolso. */
public enum SituacaoDoFgts {
    NAO_SE_APLICA,
    SOLICITADO,
    LIBERADO,
    NEGADO,
    /** Negado, o proponente pagou essa parte com recurso próprio (UC-0022). */
    RECOMPOSTO;

    /** A entrada está resolvida e a liberação do financiamento pode seguir? */
    boolean resolvido() {
        return this == NAO_SE_APLICA || this == LIBERADO || this == RECOMPOSTO;
    }
}
