package br.com.financiamento.contratacao.dominio.seguro;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/** Um seguro da contratação (GLO-0029, RN-0025) e a apólice emitida pela seguradora. */
@Embeddable
public class Seguro {

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private boolean obrigatorio;

    /** Nula até a seguradora emitir. */
    private String apolice;

    protected Seguro() {
    }

    public Seguro(String nome, boolean obrigatorio) {
        this.nome = nome;
        this.obrigatorio = obrigatorio;
    }

    public boolean contratado() {
        return apolice != null;
    }

    public void contratar(String apolice) {
        this.apolice = apolice;
    }

    public Seguro copia() {
        var s = new Seguro(nome, obrigatorio);
        s.apolice = apolice;
        return s;
    }

    public String nome() { return nome; }
    public boolean obrigatorio() { return obrigatorio; }
    public String apolice() { return apolice; }
}
