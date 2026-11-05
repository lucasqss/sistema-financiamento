package br.com.financiamento.contratacao.dominio;

import br.com.financiamento.bem.TipoDeBem;
import br.com.financiamento.contratacao.dominio.assinatura.Signatario;
import br.com.financiamento.contratacao.dominio.seguro.Seguro;
import br.com.financiamento.shared.dominio.RegraVioladaException;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
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
import java.util.Map;

/**
 * A preparação da emissão (UC-0014, passos 1 e 2): o proponente aceitou as
 * condições finais (EVT-0021) e os seguros exigidos são contratados na
 * seguradora (EVT-0022). Não é estado do contrato: o ciclo CV-0002 começa na
 * emissão. Uma proposta tem no máximo uma preparação, e por ela um contrato.
 */
@Entity
@Table(schema = "contratacao", name = "preparo")
public class PreparoDoContrato extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "proposta_id", nullable = false, updatable = false, unique = true)
    private Long propostaId;

    @Column(name = "tipo_de_bem", nullable = false, updatable = false)
    private String tipoDeBem;

    @Column(name = "aceito_em", nullable = false, updatable = false)
    private Instant aceitoEm;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(schema = "contratacao", name = "preparo_seguro", joinColumns = @JoinColumn(name = "preparo_id"))
    @OrderColumn(name = "ordem")
    private List<Seguro> seguros = new ArrayList<>();

    /** Cônjuges informados no aceite, que assinam no imóvel e no terreno (RN-0026). */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(schema = "contratacao", name = "preparo_conjuge", joinColumns = @JoinColumn(name = "preparo_id"))
    @OrderColumn(name = "ordem")
    private List<Signatario> conjuges = new ArrayList<>();

    /** Só no imóvel na planta: a previsão de entrega da obra informada no aceite (UC-0014). */
    @Column(name = "previsao_entrega_obra")
    private java.time.LocalDate previsaoDeEntregaDaObra;

    @Column(name = "contrato_id")
    private Long contratoId;

    /** Por que a emissão não aconteceu, quando os seguros já estão contratados. */
    private String impedimento;

    @Version
    @Column(name = "versao_registro")
    private long versaoDoRegistro;

    @Transient
    private final List<EventoRegistrado> eventos = new ArrayList<>();

    protected PreparoDoContrato() {
    }

    /** UC-0014, passo 1: o proponente aceita as condições finais. Emite CondicoesAceitas. */
    public static PreparoDoContrato aceitar(Long propostaId, TipoDeBem tipo, int condicoesVersao, List<String> exigidos,
                                            List<String> opcionaisEscolhidos, List<Signatario> conjuges,
                                            java.time.LocalDate previsaoDeEntregaDaObra, Instant agora) {
        var p = new PreparoDoContrato();
        p.propostaId = propostaId;
        p.tipoDeBem = tipo.codigo();
        p.aceitoEm = agora;
        exigidos.forEach(s -> p.seguros.add(new Seguro(s, true)));
        opcionaisEscolhidos.forEach(s -> p.seguros.add(new Seguro(s, false)));
        p.conjuges.addAll(conjuges);
        p.previsaoDeEntregaDaObra = previsaoDeEntregaDaObra;
        p.registrar("CondicoesAceitas", Map.of("propostaId", propostaId, "condicoesVersao", condicoesVersao,
                "seguros", p.seguros.stream().map(Seguro::nome).toList()));
        return p;
    }

    /**
     * Retorno da seguradora (ATR-0011). Idempotente: a mesma apólice repetida não
     * tem novo efeito e devolve false. Com o último seguro, emite SegurosContratados.
     */
    public boolean registrarApolice(String nomeDoSeguro, String apolice) {
        var seguro = seguros.stream().filter(s -> s.nome().equalsIgnoreCase(nomeDoSeguro)).findFirst()
                .orElseThrow(() -> new RegraVioladaException("RN-0025",
                        "O seguro " + nomeDoSeguro + " não faz parte desta contratação."));
        if (seguro.contratado()) {
            if (seguro.apolice().equals(apolice)) return false;
            throw new RegraVioladaException("RN-0025", "O seguro " + seguro.nome() + " já tem a apólice " + seguro.apolice() + ".");
        }
        seguro.contratar(apolice);
        if (segurosContratados()) {
            registrar("SegurosContratados", Map.of("propostaId", propostaId,
                    "apolices", seguros.stream().map(s -> s.nome() + ": " + s.apolice()).toList()));
        }
        return true;
    }

    /** RN-0025: os seguros escolhidos, exigidos e opcionais, têm apólice. */
    public boolean segurosContratados() {
        return seguros.stream().allMatch(Seguro::contratado);
    }

    public boolean emitido() {
        return contratoId != null;
    }

    public void contratoEmitido(Long contratoId) {
        this.contratoId = contratoId;
        this.impedimento = null;
    }

    public void impedir(String motivo) {
        this.impedimento = motivo;
    }

    private void registrar(String evento, Map<String, Object> dados) {
        eventos.add(new EventoRegistrado(evento, new LinkedHashMap<>(dados)));
    }

    public List<EventoRegistrado> retirarEventos() {
        var saida = List.copyOf(eventos);
        eventos.clear();
        return saida;
    }

    public Long id() { return id; }
    public Long propostaId() { return propostaId; }
    public TipoDeBem tipoDeBem() { return TipoDeBem.de(tipoDeBem); }
    public List<Seguro> seguros() { return List.copyOf(seguros); }
    public List<Signatario> conjuges() { return List.copyOf(conjuges); }
    public Long contratoId() { return contratoId; }
    public java.time.LocalDate previsaoDeEntregaDaObra() { return previsaoDeEntregaDaObra; }
    public String impedimento() { return impedimento; }
}
