package br.com.financiamento.originacao.dominio.participante;

import br.com.financiamento.bem.FormaDoParticipante;
import br.com.financiamento.shared.dominio.DocumentoFiscal;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Participante da proposta (GLO-0001): proponente ou coobrigado, pessoa física
 * ou jurídica. A pessoa em si é do Cadastro de pessoas; aqui fica o que a
 * proposta precisa dela.
 */
@Embeddable
public class Participante {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Papel papel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FormaDoParticipante forma;

    /** CPF ou CNPJ, só os dígitos. */
    @Column(nullable = false)
    private String documento;

    @Column(nullable = false)
    private String nome;

    /** Só pessoa física. */
    private LocalDate nascimento;

    /** Renda comprovada mensal (GLO-0004). */
    @Column(name = "renda_comprovada", nullable = false)
    private BigDecimal rendaComprovada;

    protected Participante() {
    }

    public Participante(Papel papel, FormaDoParticipante forma, String documento, String nome,
                        LocalDate nascimento, BigDecimal rendaComprovada) {
        this.papel = papel;
        this.forma = forma;
        this.documento = DocumentoFiscal.digitos(documento);
        this.nome = nome;
        this.nascimento = nascimento;
        this.rendaComprovada = rendaComprovada == null ? BigDecimal.ZERO : rendaComprovada;
    }

    public boolean proponente() {
        return papel == Papel.PROPONENTE;
    }

    public boolean pessoaFisica() {
        return forma == FormaDoParticipante.PESSOA_FISICA;
    }

    /** RN-0033: CPF para pessoa física, CNPJ para jurídica. */
    public boolean documentoValido() {
        return pessoaFisica() ? DocumentoFiscal.cpfValido(documento) : DocumentoFiscal.cnpjValido(documento);
    }

    public Papel papel() { return papel; }
    public FormaDoParticipante forma() { return forma; }
    public String documento() { return documento; }
    public String nome() { return nome; }
    public LocalDate nascimento() { return nascimento; }
    public BigDecimal rendaComprovada() { return rendaComprovada; }
}
