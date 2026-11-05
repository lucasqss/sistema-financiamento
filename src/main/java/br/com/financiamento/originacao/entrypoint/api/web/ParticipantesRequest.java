package br.com.financiamento.originacao.entrypoint.api.web;

import br.com.financiamento.bem.FormaDoParticipante;
import br.com.financiamento.originacao.aplicacao.InformarParticipantesService.NovoParticipante;
import br.com.financiamento.originacao.dominio.participante.Papel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ParticipantesRequest(List<Item> participantes) {

    public record Item(Papel papel, FormaDoParticipante forma, String documento, String nome,
                       LocalDate nascimento, BigDecimal rendaComprovada) {
    }

    List<NovoParticipante> comando() {
        return (participantes == null ? List.<Item>of() : participantes).stream()
                .map(i -> new NovoParticipante(i.papel(), i.forma(), i.documento(), i.nome(), i.nascimento(), i.rendaComprovada()))
                .toList();
    }
}
