package br.com.financiamento.originacao.dominio;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

import static br.com.financiamento.originacao.dominio.EventoDaProposta.*;

/**
 * Estados da proposta e as transições do ciclo de vida CV-0001. Uma transição
 * fora desta tabela é recusada. O teste CicloDeVidaDaPropostaTest confere a
 * tabela contra o diagrama do modelo de negócio.
 */
public enum EstadoProposta {
    RASCUNHO,
    EM_ANALISE,
    COM_PENDENCIA,
    EM_CONTRAPROPOSTA,
    APROVADA,
    REPROVADA,
    CANCELADA,
    EXPIRADA,
    CONTRATADA;

    private static final Map<EstadoProposta, Map<EventoDaProposta, EstadoProposta>> TRANSICOES =
            new EnumMap<>(EstadoProposta.class);

    static {
        transicao(RASCUNHO, PROPOSTA_SUBMETIDA, EM_ANALISE);
        transicao(RASCUNHO, PROPOSTA_CANCELADA, CANCELADA);
        transicao(EM_ANALISE, PROPOSTA_COM_PENDENCIA, COM_PENDENCIA);
        transicao(EM_ANALISE, CONTRAPROPOSTA_EMITIDA, EM_CONTRAPROPOSTA);
        transicao(EM_ANALISE, PROPOSTA_APROVADA, APROVADA);
        transicao(EM_ANALISE, PROPOSTA_REPROVADA, REPROVADA);
        transicao(COM_PENDENCIA, PENDENCIA_RESOLVIDA, EM_ANALISE);
        transicao(COM_PENDENCIA, PROPOSTA_CANCELADA, CANCELADA);
        transicao(EM_CONTRAPROPOSTA, CONTRAPROPOSTA_ACEITA, EM_ANALISE);
        transicao(EM_CONTRAPROPOSTA, CONTRAPROPOSTA_RECUSADA, REPROVADA);
        transicao(APROVADA, AVALIACAO_VENCIDA, COM_PENDENCIA);
        transicao(APROVADA, CONTRATO_EMITIDO, CONTRATADA);
        transicao(APROVADA, PROPOSTA_EXPIRADA, EXPIRADA);
        transicao(APROVADA, PROPOSTA_CANCELADA, CANCELADA);
    }

    private static void transicao(EstadoProposta de, EventoDaProposta evento, EstadoProposta para) {
        TRANSICOES.computeIfAbsent(de, e -> new EnumMap<>(EventoDaProposta.class)).put(evento, para);
    }

    /** O evento que cria a proposta e o estado em que ela nasce. */
    static final EventoDaProposta EVENTO_INICIAL = PROPOSTA_INICIADA;
    static final EstadoProposta ESTADO_INICIAL = RASCUNHO;

    Optional<EstadoProposta> apos(EventoDaProposta evento) {
        return Optional.ofNullable(transicoes().get(evento));
    }

    Map<EventoDaProposta, EstadoProposta> transicoes() {
        return Collections.unmodifiableMap(TRANSICOES.getOrDefault(this, Map.of()));
    }

    public boolean finalizado() {
        return transicoes().isEmpty();
    }

    /** Estados em que a proposta ainda "segura" o bem (RN-0004). */
    public boolean ativa() {
        return !finalizado() && this != CONTRATADA;
    }

    /** Invariante 2 do AGR-0001: só o rascunho e a proposta com pendência aceitam alteração de dados. */
    public boolean aceitaAlteracao() {
        return this == RASCUNHO || this == COM_PENDENCIA;
    }
}
