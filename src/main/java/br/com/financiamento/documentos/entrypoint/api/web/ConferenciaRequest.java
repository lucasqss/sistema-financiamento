package br.com.financiamento.documentos.entrypoint.api.web;

import br.com.financiamento.documentos.dominio.MotivoDaRecusa;

/** UC-0024: aceito, ou recusado com o motivo (ilegível, vencido, divergente). */
public record ConferenciaRequest(boolean aceito, MotivoDaRecusa motivo, String observacao) {
}
