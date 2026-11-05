package br.com.financiamento.originacao.dominio.documento;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Locale;
import java.util.Objects;

/**
 * Um documento entregue para a proposta: do bem ou de um participante. A
 * conferência do conteúdo é do domínio Documentos; aqui só se sabe o que
 * foi entregue (RN-0009).
 */
@Embeddable
public class DocumentoEntregue {

    /** Titular dos documentos do bem. */
    public static final String DO_BEM = "bem";

    /** "bem" ou o CPF/CNPJ do participante. */
    @Column(nullable = false)
    private String titular;

    @Column(nullable = false)
    private String tipo;

    protected DocumentoEntregue() {
    }

    public DocumentoEntregue(String titular, String tipo) {
        this.titular = titular == null || titular.isBlank() || DO_BEM.equalsIgnoreCase(titular.trim())
                ? DO_BEM : titular.replaceAll("\\D", "");
        this.tipo = tipo == null ? "" : tipo.trim();
    }

    public boolean mesmo(String titular, String tipo) {
        return this.titular.equals(titular) && normalizar(this.tipo).equals(normalizar(tipo));
    }

    private static String normalizar(String s) {
        return s == null ? "" : s.trim().toLowerCase(Locale.ROOT);
    }

    public String titular() { return titular; }
    public String tipo() { return tipo; }

    @Override
    public boolean equals(Object o) {
        return o instanceof DocumentoEntregue d && mesmo(d.titular, d.tipo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(titular, normalizar(tipo));
    }
}
