package br.com.financiamento.garantia.dominio;

import br.com.financiamento.bem.TipoDeBem;
import br.com.financiamento.garantia.dominio.retorno.ResultadoDoOrgao;
import br.com.financiamento.garantia.dominio.retorno.RetornoRecebido;
import br.com.financiamento.shared.validacao.ErroDeDominioException;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * O registro da garantia de um contrato (UC-0018): a alienação fiduciária
 * enviada ao órgão da categoria do bem (RN-0028), com um protocolo nosso por
 * tentativa, e os retornos do órgão classificados como fato (RN-0029).
 */
@Entity
@Table(schema = "garantia", name = "registro")
public class Registro extends PanacheEntityBase {

    /** O que um retorno fez com o registro. */
    public enum Efeito {
        /** Retorno repetido do mesmo protocolo: nenhum novo efeito. */
        REPETIDO,
        /** De tentativa anterior, ou chegado depois de o registro já ter seguido: guardado, sem efeito. */
        SEM_EFEITO,
        CONSTITUIDA,
        PENDENCIA_DE_IDENTIFICACAO,
        EXIGENCIA,
        RECUSADO
    }

    private static final Pattern PROTOCOLO = Pattern.compile("GAR-(\\d+)-(\\d+)");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contrato_id", nullable = false, updatable = false, unique = true)
    private Long contratoId;

    @Column(name = "proposta_id", nullable = false, updatable = false)
    private Long propostaId;

    @Column(name = "tipo_de_bem", nullable = false, updatable = false)
    private String tipoDeBem;

    @Column(nullable = false, updatable = false)
    private String orgao;

    @Column(name = "identificacao_do_bem", nullable = false, updatable = false)
    private String identificacaoDoBem;

    @Column(nullable = false)
    private int tentativa;

    @Column(nullable = false)
    private String protocolo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SituacaoDoRegistro situacao;

    /** A exigência, o motivo da recusa ou a divergência de identificação em aberto. */
    private String observacao;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(schema = "garantia", name = "registro_retorno", joinColumns = @JoinColumn(name = "registro_id"))
    @OrderColumn(name = "ordem")
    private List<RetornoRecebido> retornos = new ArrayList<>();

    @Version
    @Column(name = "versao_registro")
    private long versaoDoRegistro;

    @Transient
    private final List<EventoRegistrado> eventos = new ArrayList<>();

    protected Registro() {
    }

    /** UC-0018, passo 1: envia o contrato assinado ao órgão. Emite RegistroSolicitado. */
    public static Registro solicitar(Long contratoId, Long propostaId, TipoDeBem tipo, String identificacaoDoBem, String orgao) {
        var r = new Registro();
        r.contratoId = contratoId;
        r.propostaId = propostaId;
        r.tipoDeBem = tipo.codigo();
        r.identificacaoDoBem = identificacaoDoBem;
        r.orgao = orgao;
        r.novaTentativa();
        return r;
    }

    /** O contrato de um protocolo nosso ("GAR-<contrato>-<tentativa>"). */
    public static Optional<Long> contratoDoProtocolo(String protocolo) {
        var m = PROTOCOLO.matcher(protocolo == null ? "" : protocolo.trim());
        return m.matches() ? Optional.of(Long.valueOf(m.group(1))) : Optional.empty();
    }

    /** UC-0018, passo 2 e RN-0029: o retorno é classificado, nunca recusado. */
    public Efeito classificar(String protocoloDoRetorno, ResultadoDoOrgao resultado, String identificacaoRegistrada,
                             String texto, Instant agora) {
        if (retornos.stream().anyMatch(x -> x.mesmo(protocoloDoRetorno, resultado, identificacaoRegistrada))) {
            return Efeito.REPETIDO;
        }
        var efeito = efeitoDe(protocoloDoRetorno, resultado, identificacaoRegistrada, texto);
        retornos.add(new RetornoRecebido(protocoloDoRetorno, resultado, identificacaoRegistrada, texto, agora, efeito.name()));
        return efeito;
    }

    private Efeito efeitoDe(String protocoloDoRetorno, ResultadoDoOrgao resultado, String identificacaoRegistrada, String texto) {
        if (!protocolo.equals(protocoloDoRetorno) || situacao != SituacaoDoRegistro.SOLICITADO) return Efeito.SEM_EFEITO;
        switch (resultado) {
            case REGISTRADO -> {
                if (normalizar(identificacaoRegistrada).equals(normalizar(identificacaoDoBem))) {
                    situacao = SituacaoDoRegistro.CONSTITUIDA;
                    observacao = null;
                    registrar("GarantiaRegistrada", Map.of("protocolo", protocolo));
                    return Efeito.CONSTITUIDA;
                }
                situacao = SituacaoDoRegistro.PENDENCIA_DE_IDENTIFICACAO;
                observacao = "O órgão registrou o bem " + identificacaoRegistrada + ", e o do contrato é " + identificacaoDoBem + ".";
                return Efeito.PENDENCIA_DE_IDENTIFICACAO;
            }
            case EXIGENCIA -> {
                situacao = SituacaoDoRegistro.EM_EXIGENCIA;
                observacao = texto;
                registrar("ExigenciaEmitida", Map.of("protocolo", protocolo, "exigencia", texto == null ? "" : texto));
                return Efeito.EXIGENCIA;
            }
            default -> {
                situacao = SituacaoDoRegistro.RECUSADO;
                observacao = texto;
                registrar("RegistroRecusado", Map.of("protocolo", protocolo, "motivo", texto == null ? "" : texto));
                return Efeito.RECUSADO;
            }
        }
    }

    /** UC-0020: o analista cumpriu a exigência; reenvia com novo protocolo. Emite ExigenciaCumprida. */
    public void cumprirExigencia() {
        exigir(SituacaoDoRegistro.EM_EXIGENCIA, ErrosDaGarantia.SEM_EXIGENCIA);
        registrar("ExigenciaCumprida", Map.of("protocolo", protocolo));
        novaTentativa();
    }

    /**
     * UC-0019 e divergência de identificação: o analista corrige e reenvia com
     * novo protocolo. Devolve a situação de antes, que diz se o contrato muda.
     */
    public SituacaoDoRegistro reenviar() {
        var antes = situacao;
        if (antes != SituacaoDoRegistro.RECUSADO && antes != SituacaoDoRegistro.PENDENCIA_DE_IDENTIFICACAO) {
            throw new ErroDeDominioException(ErrosDaGarantia.REENVIO_INVALIDO);
        }
        novaTentativa();
        return antes;
    }

    /** UC-0019, fluxo alternativo: recusado sem solução, o contrato é desfeito (UC-0016). */
    public void desfazer() {
        exigir(SituacaoDoRegistro.RECUSADO, ErrosDaGarantia.DESFAZER_SO_RECUSADO);
        situacao = SituacaoDoRegistro.DESFEITO;
    }

    private void novaTentativa() {
        tentativa++;
        protocolo = "GAR-" + contratoId + "-" + tentativa;
        situacao = SituacaoDoRegistro.SOLICITADO;
        observacao = null;
        registrar("RegistroSolicitado", Map.of("contratoId", contratoId, "orgao", orgao, "protocolo", protocolo,
                "identificacaoDoBem", identificacaoDoBem, "tentativa", tentativa));
    }

    private void exigir(SituacaoDoRegistro esperada, ErrosDaGarantia erro) {
        if (situacao != esperada) throw new ErroDeDominioException(erro);
    }

    private static String normalizar(String identificacao) {
        return identificacao == null ? "" : identificacao.replaceAll("[^A-Za-z0-9]", "").toUpperCase(Locale.ROOT);
    }

    private void registrar(String evento, Map<String, Object> dados) {
        var completos = new LinkedHashMap<String, Object>(dados);
        completos.put("contratoId", contratoId);
        eventos.add(new EventoRegistrado(evento, completos));
    }

    public List<EventoRegistrado> retirarEventos() {
        var saida = List.copyOf(eventos);
        eventos.clear();
        return saida;
    }

    public Long id() { return id; }
    public Long contratoId() { return contratoId; }
    public Long propostaId() { return propostaId; }
    public TipoDeBem tipoDeBem() { return TipoDeBem.de(tipoDeBem); }
    public String orgao() { return orgao; }
    public String identificacaoDoBem() { return identificacaoDoBem; }
    public int tentativa() { return tentativa; }
    public String protocolo() { return protocolo; }
    public SituacaoDoRegistro situacao() { return situacao; }
    public String observacao() { return observacao; }
    public List<RetornoRecebido> retornos() { return List.copyOf(retornos); }
}
