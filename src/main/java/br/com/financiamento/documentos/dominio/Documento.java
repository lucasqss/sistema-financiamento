package br.com.financiamento.documentos.dominio;

import br.com.financiamento.shared.validacao.ErroDeDominioException;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Map;

/**
 * Um documento enviado para a proposta (DOM-0008): o arquivo guardado, com a
 * integridade conferível, e o resultado da conferência do analista (UC-0024).
 */
@Entity
@Table(schema = "documentos", name = "documento")
public class Documento extends PanacheEntityBase {

    /** ADR 0010: até 10 MB. */
    public static final long TAMANHO_MAXIMO = 10L * 1024 * 1024;

    /** ADR 0010: PDF, JPEG e PNG, conferidos pelo começo do arquivo e não só pelo tipo declarado. */
    private static final Map<String, byte[]> ASSINATURAS = Map.of(
            "application/pdf", new byte[]{'%', 'P', 'D', 'F'},
            "image/png", new byte[]{(byte) 0x89, 'P', 'N', 'G'},
            "image/jpeg", new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF});

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "proposta_id", nullable = false, updatable = false)
    private Long propostaId;

    /** "bem" ou o CPF/CNPJ do participante. */
    @Column(nullable = false, updatable = false)
    private String titular;

    @Column(nullable = false, updatable = false)
    private String tipo;

    @Column(name = "nome_do_arquivo", nullable = false, updatable = false)
    private String nomeDoArquivo;

    @Column(name = "tipo_de_conteudo", nullable = false, updatable = false)
    private String tipoDeConteudo;

    @Column(nullable = false, updatable = false)
    private long tamanho;

    @Column(nullable = false, updatable = false)
    private String sha256;

    @Column(name = "chave_no_armazem", nullable = false, updatable = false)
    private String chaveNoArmazem;

    @Column(name = "enviado_em", nullable = false, updatable = false)
    private Instant enviadoEm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SituacaoDoDocumento situacao;

    @Enumerated(EnumType.STRING)
    private MotivoDaRecusa motivo;

    private String observacao;

    @Column(name = "conferido_em")
    private Instant conferidoEm;

    @Version
    @Column(name = "versao_registro")
    private long versaoDoRegistro;

    protected Documento() {
    }

    /** Confere o arquivo (tipo, tamanho, conteúdo) e calcula a integridade antes de guardar. */
    public static Documento enviar(Long propostaId, String titular, String tipo, String nomeDoArquivo, String tipoDeConteudo,
                                   byte[] conteudo, String chaveNoArmazem, Instant agora) {
        validar(tipo, tipoDeConteudo, conteudo);
        var d = new Documento();
        d.propostaId = propostaId;
        d.titular = titularNormalizado(titular);
        d.tipo = tipo.trim();
        d.nomeDoArquivo = nomeDoArquivo == null || nomeDoArquivo.isBlank() ? "documento" : nomeDoArquivo;
        d.tipoDeConteudo = tipoDeConteudo;
        d.tamanho = conteudo.length;
        d.sha256 = sha256(conteudo);
        d.chaveNoArmazem = chaveNoArmazem;
        d.enviadoEm = agora;
        d.situacao = SituacaoDoDocumento.ENVIADO;
        return d;
    }

    /** "bem", ou só os dígitos do CPF/CNPJ do participante. */
    public static String titularNormalizado(String titular) {
        return titular == null || titular.isBlank() || "bem".equalsIgnoreCase(titular.trim()) ? "bem"
                : titular.replaceAll("\\D", "");
    }

    /** Antes de guardar: o que o armazém recebe já passou por estas regras. */
    public static void validar(String tipo, String tipoDeConteudo, byte[] conteudo) {
        if (tipo == null || tipo.isBlank()) throw new ErroDeDominioException(ErrosDosDocumentos.TIPO_DE_DOCUMENTO_OBRIGATORIO);
        if (conteudo == null || conteudo.length == 0) throw new ErroDeDominioException(ErrosDosDocumentos.ARQUIVO_VAZIO);
        if (conteudo.length > TAMANHO_MAXIMO) throw new ErroDeDominioException(ErrosDosDocumentos.ARQUIVO_GRANDE_DEMAIS);
        var assinatura = ASSINATURAS.get(tipoDeConteudo);
        if (assinatura == null) throw new ErroDeDominioException(ErrosDosDocumentos.TIPO_DE_ARQUIVO);
        for (int i = 0; i < assinatura.length; i++) {
            if (conteudo.length <= i || conteudo[i] != assinatura[i]) {
                throw new ErroDeDominioException(ErrosDosDocumentos.CONTEUDO_DIVERGENTE, tipoDeConteudo);
            }
        }
    }

    /**
     * UC-0024: aceito, ou recusado com o motivo. A mesma conferência repetida não
     * tem novo efeito e devolve false; uma conferência diferente é recusada.
     */
    public boolean conferir(boolean aceito, MotivoDaRecusa motivo, String observacao, Instant agora) {
        var resultado = aceito ? SituacaoDoDocumento.ACEITO : SituacaoDoDocumento.RECUSADO;
        if (situacao == resultado && (aceito || this.motivo == motivo)) return false;
        if (situacao != SituacaoDoDocumento.ENVIADO) {
            throw new ErroDeDominioException(ErrosDosDocumentos.JA_CONFERIDO, id, situacao.name().toLowerCase());
        }
        if (!aceito && motivo == null) {
            throw new ErroDeDominioException(ErrosDosDocumentos.MOTIVO_OBRIGATORIO);
        }
        this.situacao = resultado;
        this.motivo = aceito ? null : motivo;
        this.observacao = observacao;
        this.conferidoEm = agora;
        return true;
    }

    public void substituir() {
        if (situacao == SituacaoDoDocumento.ACEITO) {
            throw new ErroDeDominioException(ErrosDosDocumentos.DOCUMENTO_JA_ACEITO, tipo);
        }
        this.situacao = SituacaoDoDocumento.SUBSTITUIDO;
    }

    public static String sha256(byte[] conteudo) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(conteudo));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    /** O motivo como o negócio o diz: "ilegível", com a observação do analista. */
    public String motivoDescrito() {
        if (motivo == null) return null;
        return observacao == null || observacao.isBlank() ? motivo.descricao() : motivo.descricao() + ": " + observacao;
    }

    public Long id() { return id; }
    public Long propostaId() { return propostaId; }
    public String titular() { return titular; }
    public String tipo() { return tipo; }
    public String nomeDoArquivo() { return nomeDoArquivo; }
    public String tipoDeConteudo() { return tipoDeConteudo; }
    public long tamanho() { return tamanho; }
    public String sha256() { return sha256; }
    public String chaveNoArmazem() { return chaveNoArmazem; }
    public Instant enviadoEm() { return enviadoEm; }
    public SituacaoDoDocumento situacao() { return situacao; }
    public MotivoDaRecusa motivo() { return motivo; }
    public String observacao() { return observacao; }
    public Instant conferidoEm() { return conferidoEm; }
}
