package br.com.financiamento.documentos.dominio;

/** Por que um documento é recusado na conferência (UC-0024, EVT-0038). */
public enum MotivoDaRecusa {
    ILEGIVEL("ilegível"),
    VENCIDO("vencido"),
    DIVERGENTE("divergente");

    private final String descricao;

    MotivoDaRecusa(String descricao) {
        this.descricao = descricao;
    }

    public String descricao() {
        return descricao;
    }
}
