package br.com.financiamento.garantia.dominio.retorno;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.Instant;
import java.util.Objects;

/** Um retorno do órgão como chegou, guardado como fato (RN-0029), com o efeito que teve. */
@Embeddable
public class RetornoRecebido {

    @Column(nullable = false)
    private String protocolo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResultadoDoOrgao resultado;

    @Column(name = "identificacao_registrada")
    private String identificacaoRegistrada;

    private String texto;

    @Column(name = "recebido_em", nullable = false)
    private Instant recebidoEm;

    @Column(nullable = false)
    private String efeito;

    protected RetornoRecebido() {
    }

    public RetornoRecebido(String protocolo, ResultadoDoOrgao resultado, String identificacaoRegistrada, String texto,
                           Instant recebidoEm, String efeito) {
        this.protocolo = protocolo;
        this.resultado = resultado;
        this.identificacaoRegistrada = identificacaoRegistrada;
        this.texto = texto;
        this.recebidoEm = recebidoEm;
        this.efeito = efeito;
    }

    /** O mesmo retorno repetido: mesmo protocolo, resultado e identificação. */
    public boolean mesmo(String protocolo, ResultadoDoOrgao resultado, String identificacaoRegistrada) {
        return this.protocolo.equals(protocolo) && this.resultado == resultado
                && Objects.equals(this.identificacaoRegistrada, identificacaoRegistrada);
    }

    public String protocolo() { return protocolo; }
    public ResultadoDoOrgao resultado() { return resultado; }
    public String identificacaoRegistrada() { return identificacaoRegistrada; }
    public String texto() { return texto; }
    public Instant recebidoEm() { return recebidoEm; }
    public String efeito() { return efeito; }
}
