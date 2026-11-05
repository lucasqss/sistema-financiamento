package br.com.financiamento.contratacao.dominio;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

/** Os eventos que mudam o estado do contrato, como no ciclo de vida CV-0002. */
enum EventoDoContrato {
    CONTRATO_EMITIDO,
    CONTRATO_ASSINADO,
    CONTRATO_CANCELADO,
    REGISTRO_SOLICITADO,
    EXIGENCIA_EMITIDA,
    EXIGENCIA_CUMPRIDA,
    REGISTRO_RECUSADO,
    GARANTIA_REGISTRADA,
    RECURSO_LIBERADO,
    PARCELA_DA_OBRA_LIBERADA,
    MEDICAO_REALIZADA,
    PRAZO_DA_OBRA_PRORROGADO;

    /** CONTRATO_EMITIDO -> ContratoEmitido. */
    String nomeNoModelo() {
        return Arrays.stream(name().split("_"))
                .map(p -> p.charAt(0) + p.substring(1).toLowerCase(Locale.ROOT))
                .collect(Collectors.joining());
    }
}
