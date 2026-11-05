package br.com.financiamento.contratacao.dominio;

import br.com.financiamento.shared.dominio.RegraVioladaException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Cronograma da obra (AGR-0002, objeto de valor), só no imóvel na planta: o prazo
 * previsto informado no aceite (UC-0014) e as prorrogações (RN-0027).
 */
@Embeddable
public class CronogramaDaObra {

    /** RN-0027 e DEC-0014: prorrogações somadas até 180 dias além do prazo previsto. */
    public static final int LIMITE_DE_PRORROGACAO_DIAS = 180;

    @Column(name = "obra_previsao_original")
    private LocalDate previsaoOriginal;

    @Column(name = "obra_previsao_atual")
    private LocalDate previsaoAtual;

    @Column(name = "obra_prorrogacao_pendente")
    private LocalDate prorrogacaoPendente;

    protected CronogramaDaObra() {
    }

    public CronogramaDaObra(LocalDate previsao) {
        this.previsaoOriginal = previsao;
        this.previsaoAtual = previsao;
    }

    boolean dentroDoLimite(LocalDate novaData) {
        if (!novaData.isAfter(previsaoAtual)) {
            throw new RegraVioladaException("RN-0027", "A nova data precisa ser posterior à previsão atual (" + previsaoAtual + ").");
        }
        return diasAlemDoPrevisto(novaData) <= LIMITE_DE_PRORROGACAO_DIAS;
    }

    /** Aplica e devolve quantos dias esta prorrogação acrescentou. */
    long prorrogar(LocalDate novaData) {
        long dias = ChronoUnit.DAYS.between(previsaoAtual, novaData);
        this.previsaoAtual = novaData;
        this.prorrogacaoPendente = null;
        return dias;
    }

    void deixarPendente(LocalDate novaData) {
        this.prorrogacaoPendente = novaData;
    }

    void negarPendente() {
        this.prorrogacaoPendente = null;
    }

    public long diasProrrogados() {
        return diasAlemDoPrevisto(previsaoAtual);
    }

    private long diasAlemDoPrevisto(LocalDate data) {
        return ChronoUnit.DAYS.between(previsaoOriginal, data);
    }

    public LocalDate previsaoOriginal() { return previsaoOriginal; }
    public LocalDate previsaoAtual() { return previsaoAtual; }
    public LocalDate pendente() { return prorrogacaoPendente; }
}
