package br.com.financiamento.contratacao.dominio.assinatura;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.Instant;

/** Quem precisa assinar o contrato (RN-0026), e quando assinou. */
@Embeddable
public class Signatario {

    /** PROPONENTE, COOBRIGADO, VENDEDOR ou CONJUGE. */
    @Column(nullable = false)
    private String papel;

    @Column(nullable = false)
    private String documento;

    @Column(nullable = false)
    private String nome;

    @Column(name = "assinado_em")
    private Instant assinadoEm;

    protected Signatario() {
    }

    public Signatario(String papel, String documento, String nome) {
        this.papel = papel;
        this.documento = documento == null ? "" : documento.replaceAll("\\D", "");
        this.nome = nome;
    }

    public boolean assinou() {
        return assinadoEm != null;
    }

    public void assinar(Instant agora) {
        this.assinadoEm = agora;
    }

    public String papel() { return papel; }
    public String documento() { return documento; }
    public String nome() { return nome; }
    public Instant assinadoEm() { return assinadoEm; }
}
