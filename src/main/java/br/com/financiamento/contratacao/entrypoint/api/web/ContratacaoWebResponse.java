package br.com.financiamento.contratacao.entrypoint.api.web;

import br.com.financiamento.contratacao.aplicacao.DetalheDaContratacao;

import java.util.List;

public record ContratacaoWebResponse(Long id, Long propostaId, List<Seguro> seguros, Long contratoId,
                                     String impedimento) {

    public record Seguro(String nome, boolean obrigatorio, String apolice) {
    }

    public static ContratacaoWebResponse de(DetalheDaContratacao d) {
        return new ContratacaoWebResponse(d.id(), d.propostaId(),
                d.seguros().stream().map(s -> new Seguro(s.nome(), s.obrigatorio(), s.apolice())).toList(),
                d.contratoId(), d.impedimento());
    }
}
