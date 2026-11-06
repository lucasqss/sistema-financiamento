package br.com.financiamento.originacao.dominio.documento;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Locale;
import java.util.Objects;

/**
 * Um documento entregue para a proposta: do bem ou de um participante. O arquivo
 * e a conferência são do domínio Documentos; aqui fica o que a proposta precisa
 * saber: o que foi entregue (RN-0009) e se foi aceito ou recusado (RN-0019).
 */
@Embeddable
public class DocumentoEntregue {

    /** Titular dos documentos do bem. */
    public static final String DO_BEM = "bem";

    public enum Situacao { ENTREGUE, ACEITO, RECUSADO }

    /** "bem" ou o CPF/CNPJ do participante. */
    @Column(nullable = false)
    private String titular;

    @Column(nullable = false)
    private String tipo;

    /** O documento no módulo Documentos. */
    @Column(name = "documento_id")
    private Long documentoId;

    @Column(nullable = false)
    private String situacao = Situacao.ENTREGUE.name();

    /** Motivo da recusa na conferência (UC-0024). */
    private String motivo;

    protected DocumentoEntregue() {
    }

    public DocumentoEntregue(Long documentoId, String titular, String tipo) {
        this.documentoId = documentoId;
        this.titular = normalizarTitular(titular);
        this.tipo = tipo == null ? "" : tipo.trim();
    }

    public static String normalizarTitular(String titular) {
        return titular == null || titular.isBlank() || DO_BEM.equalsIgnoreCase(titular.trim())
                ? DO_BEM : titular.replaceAll("\\D", "");
    }

    /** O resultado da conferência, como um novo valor (a lista da proposta troca o elemento). */
    public DocumentoEntregue conferido(boolean aceito, String motivo) {
        var d = new DocumentoEntregue(documentoId, titular, tipo);
        d.situacao = (aceito ? Situacao.ACEITO : Situacao.RECUSADO).name();
        d.motivo = aceito ? null : motivo;
        return d;
    }

    public boolean mesmo(String titular, String tipo) {
        return this.titular.equals(titular) && normalizar(this.tipo).equals(normalizar(tipo));
    }

    private static String normalizar(String s) {
        return s == null ? "" : s.trim().toLowerCase(Locale.ROOT);
    }

    public String titular() { return titular; }
    public String tipo() { return tipo; }
    public Long documentoId() { return documentoId; }
    public Situacao situacao() { return Situacao.valueOf(situacao); }
    public String motivo() { return motivo; }

    @Override
    public boolean equals(Object o) {
        return o instanceof DocumentoEntregue d && mesmo(d.titular, d.tipo) && Objects.equals(documentoId, d.documentoId)
                && situacao.equals(d.situacao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(titular, normalizar(tipo), documentoId, situacao);
    }
}
