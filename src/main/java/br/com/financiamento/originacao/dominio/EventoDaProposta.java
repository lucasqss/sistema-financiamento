package br.com.financiamento.originacao.dominio;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Os eventos que mudam o estado da proposta, como no ciclo de vida CV-0001.
 * Os que vêm de outro domínio (AvaliacaoVencida, ContratoEmitido) também
 * aparecem aqui porque movem a proposta.
 */
enum EventoDaProposta {
    PROPOSTA_INICIADA,
    PROPOSTA_SUBMETIDA,
    PROPOSTA_COM_PENDENCIA,
    PENDENCIA_RESOLVIDA,
    CONTRAPROPOSTA_EMITIDA,
    CONTRAPROPOSTA_ACEITA,
    CONTRAPROPOSTA_RECUSADA,
    PROPOSTA_APROVADA,
    PROPOSTA_REPROVADA,
    PROPOSTA_CANCELADA,
    PROPOSTA_EXPIRADA,
    AVALIACAO_VENCIDA,
    CONTRATO_EMITIDO;

    /** O nome do evento no modelo de negócio: PROPOSTA_INICIADA -> PropostaIniciada. */
    String nomeNoModelo() {
        return Arrays.stream(name().split("_"))
                .map(p -> p.charAt(0) + p.substring(1).toLowerCase(Locale.ROOT))
                .collect(Collectors.joining());
    }
}
