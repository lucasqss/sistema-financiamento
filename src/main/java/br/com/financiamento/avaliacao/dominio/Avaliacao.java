package br.com.financiamento.avaliacao.dominio;

import br.com.financiamento.bem.TipoDeBem;
import br.com.financiamento.shared.validacao.ErroDeDominioException;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Uma avaliação do bem de uma proposta (UC-0012): pedida na fonte do tipo de
 * bem (RN-0023) e concluída com valor e aceitação, ou com a recusa como garantia.
 */
@Entity
@Table(schema = "avaliacao", name = "avaliacao")
public class Avaliacao extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "proposta_id", nullable = false, updatable = false)
    private Long propostaId;

    @Column(name = "tipo_de_bem", nullable = false, updatable = false)
    private String tipoDeBem;

    @Column(name = "identificacao_do_bem", nullable = false)
    private String identificacaoDoBem;

    @Column(name = "valor_de_compra", nullable = false)
    private BigDecimal valorDeCompra;

    @Column(nullable = false)
    private String fonte;

    @Column(name = "solicitada_em", nullable = false)
    private Instant solicitadaEm;

    /** Nulo enquanto o avaliador não respondeu. */
    @Column(name = "aceito_como_garantia")
    private Boolean aceitoComoGarantia;

    private BigDecimal valor;

    @Column(name = "avaliada_em")
    private LocalDate avaliadaEm;

    @Column(name = "valida_ate")
    private LocalDate validaAte;

    protected Avaliacao() {
    }

    public static Avaliacao solicitar(Long propostaId, TipoDeBem tipo, String identificacao, BigDecimal valorDeCompra,
                                      String fonte, Instant agora) {
        var a = new Avaliacao();
        a.propostaId = propostaId;
        a.tipoDeBem = tipo.codigo();
        a.identificacaoDoBem = identificacao;
        a.valorDeCompra = valorDeCompra;
        a.fonte = fonte;
        a.solicitadaEm = agora;
        return a;
    }

    /**
     * Registra o laudo. Idempotente: o mesmo laudo repetido não tem novo efeito
     * e devolve false; um laudo diferente do já registrado é recusado.
     */
    public boolean concluir(boolean aceito, BigDecimal valor, LocalDate avaliadaEm, LocalDate validaAte) {
        if (aceito && (valor == null || valor.signum() <= 0)) {
            throw new ErroDeDominioException(ErrosDaAvaliacao.VALOR_DE_AVALIACAO_OBRIGATORIO);
        }
        if (this.aceitoComoGarantia != null) {
            boolean mesmo = this.aceitoComoGarantia == aceito && Objects.equals(this.avaliadaEm, avaliadaEm)
                    && (valor == null ? this.valor == null : this.valor != null && this.valor.compareTo(valor) == 0);
            if (mesmo) return false;
            throw new ErroDeDominioException(ErrosDaAvaliacao.AVALIACAO_JA_CONCLUIDA, id);
        }
        this.aceitoComoGarantia = aceito;
        this.valor = aceito ? valor : null;
        this.avaliadaEm = avaliadaEm;
        this.validaAte = aceito ? validaAte : null;
        return true;
    }

    public Long id() { return id; }
    public Long propostaId() { return propostaId; }
    public TipoDeBem tipoDeBem() { return TipoDeBem.de(tipoDeBem); }
    public String identificacaoDoBem() { return identificacaoDoBem; }
    public String fonte() { return fonte; }
    public Boolean aceitoComoGarantia() { return aceitoComoGarantia; }
    public BigDecimal valor() { return valor; }
    public LocalDate avaliadaEm() { return avaliadaEm; }
    public LocalDate validaAte() { return validaAte; }
}
