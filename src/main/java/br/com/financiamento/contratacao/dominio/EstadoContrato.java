package br.com.financiamento.contratacao.dominio;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

import static br.com.financiamento.contratacao.dominio.EventoDoContrato.*;

/**
 * Estados do contrato e as transições do ciclo de vida CV-0002. "Em obra" só
 * existe no imóvel na planta. O teste CicloDeVidaDoContratoTest confere a
 * tabela contra o diagrama do modelo de negócio.
 */
public enum EstadoContrato {
    AGUARDANDO_ASSINATURA,
    ASSINADO,
    EM_REGISTRO,
    EM_EXIGENCIA,
    REGISTRO_RECUSADO,
    GARANTIA_CONSTITUIDA,
    EM_OBRA,
    FORMALIZADO,
    CANCELADO;

    private static final Map<EstadoContrato, Map<EventoDoContrato, EstadoContrato>> TRANSICOES =
            new EnumMap<>(EstadoContrato.class);

    static {
        transicao(AGUARDANDO_ASSINATURA, CONTRATO_ASSINADO, ASSINADO);
        transicao(AGUARDANDO_ASSINATURA, CONTRATO_CANCELADO, CANCELADO);
        transicao(ASSINADO, REGISTRO_SOLICITADO, EM_REGISTRO);
        transicao(EM_REGISTRO, EXIGENCIA_EMITIDA, EM_EXIGENCIA);
        transicao(EM_EXIGENCIA, EXIGENCIA_CUMPRIDA, EM_REGISTRO);
        transicao(EM_REGISTRO, EventoDoContrato.REGISTRO_RECUSADO, REGISTRO_RECUSADO);
        transicao(REGISTRO_RECUSADO, REGISTRO_SOLICITADO, EM_REGISTRO);
        transicao(REGISTRO_RECUSADO, CONTRATO_CANCELADO, CANCELADO);
        transicao(EM_REGISTRO, GARANTIA_REGISTRADA, GARANTIA_CONSTITUIDA);
        transicao(GARANTIA_CONSTITUIDA, RECURSO_LIBERADO, FORMALIZADO);
        transicao(GARANTIA_CONSTITUIDA, PARCELA_DA_OBRA_LIBERADA, EM_OBRA);
        transicao(EM_OBRA, MEDICAO_REALIZADA, EM_OBRA);
        transicao(EM_OBRA, PRAZO_DA_OBRA_PRORROGADO, EM_OBRA);
        transicao(EM_OBRA, PARCELA_DA_OBRA_LIBERADA, FORMALIZADO);
    }

    private static void transicao(EstadoContrato de, EventoDoContrato evento, EstadoContrato para) {
        TRANSICOES.computeIfAbsent(de, e -> new EnumMap<>(EventoDoContrato.class)).put(evento, para);
    }

    static final EventoDoContrato EVENTO_INICIAL = CONTRATO_EMITIDO;
    static final EstadoContrato ESTADO_INICIAL = AGUARDANDO_ASSINATURA;

    Optional<EstadoContrato> apos(EventoDoContrato evento) {
        return Optional.ofNullable(transicoes().get(evento));
    }

    Map<EventoDoContrato, EstadoContrato> transicoes() {
        return Collections.unmodifiableMap(TRANSICOES.getOrDefault(this, Map.of()));
    }

    boolean finalizado() {
        return transicoes().isEmpty();
    }
}
