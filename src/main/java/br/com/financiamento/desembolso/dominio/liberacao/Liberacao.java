package br.com.financiamento.desembolso.dominio.liberacao;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;
import java.time.Instant;

/** Uma liberação ao vendedor (GLO-0044): única, ou uma parcela da obra; paga uma vez, pelo protocolo (RN-0031). */
@Embeddable
public class Liberacao {

    @Column(nullable = false)
    private String protocolo;

    @Column(nullable = false)
    private BigDecimal valor;

    /** Na obra: o percentual acumulado que esta parcela cobre. */
    @Column(name = "percentual_ate")
    private BigDecimal percentualAte;

    @Column(nullable = false)
    private boolean ultima;

    @Column(name = "solicitada_em", nullable = false)
    private Instant solicitadaEm;

    @Column(name = "paga_em")
    private Instant pagaEm;

    protected Liberacao() {
    }

    public Liberacao(String protocolo, BigDecimal valor, BigDecimal percentualAte, boolean ultima, Instant solicitadaEm) {
        this.protocolo = protocolo;
        this.valor = valor;
        this.percentualAte = percentualAte;
        this.ultima = ultima;
        this.solicitadaEm = solicitadaEm;
    }

    public boolean paga() {
        return pagaEm != null;
    }

    public void pagar(Instant agora) {
        this.pagaEm = agora;
    }

    public String protocolo() { return protocolo; }
    public BigDecimal valor() { return valor; }
    public BigDecimal percentualAte() { return percentualAte; }
    public boolean ultima() { return ultima; }
    public Instant solicitadaEm() { return solicitadaEm; }
    public Instant pagaEm() { return pagaEm; }
}
