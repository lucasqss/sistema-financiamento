package br.com.financiamento.garantia.dominio;

/** Em que pé está o registro da garantia (GLO-0043). */
public enum SituacaoDoRegistro {
    SOLICITADO,
    EM_EXIGENCIA,
    RECUSADO,
    /** O órgão registrou outro bem: o analista confere e reenvia (RN-0029). */
    PENDENCIA_DE_IDENTIFICACAO,
    CONSTITUIDA,
    /** Recusado sem solução: o contrato foi desfeito (UC-0016). */
    DESFEITO
}
