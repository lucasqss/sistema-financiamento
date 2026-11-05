package br.com.financiamento.contratacao.entrypoint.api.web;

import br.com.financiamento.contratacao.aplicacao.DetalheDoContrato;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record ContratoWebResponse(Long id, Long propostaId, String tipoDeBem, String estado, Condicoes condicoes,
                                  List<Seguro> seguros, List<Signatario> signatarios, Garantia garantia,
                                  Obra obra) {

    public record Obra(LocalDate previsaoOriginal, LocalDate previsaoAtual, long diasProrrogados,
                       LocalDate prorrogacaoPendente) {
    }

    public record Garantia(String orgao, String protocolo, String situacao) {
    }

    public record Condicoes(int versao, String identificacaoDoBem, BigDecimal valorDeCompra, BigDecimal valorFinanciado,
                            BigDecimal entradaPropria, BigDecimal entradaFgts, int prazoMeses, BigDecimal parcela) {
    }

    public record Seguro(String nome, boolean obrigatorio, String apolice) {
    }

    public record Signatario(String papel, String documento, String nome, Instant assinadoEm) {
    }

    public static ContratoWebResponse de(DetalheDoContrato d) {
        var c = d.condicoes();
        return new ContratoWebResponse(d.id(), d.propostaId(), d.tipoDeBem(), d.estado().name(),
                new Condicoes(c.versao(), c.identificacaoDoBem(), c.valorDeCompra(), c.valorFinanciado(),
                        c.entradaPropria(), c.entradaFgts(), c.prazoMeses(), c.parcela()),
                d.seguros().stream().map(s -> new Seguro(s.nome(), s.obrigatorio(), s.apolice())).toList(),
                d.signatarios().stream().map(s -> new Signatario(s.papel(), s.documento(), s.nome(), s.assinadoEm())).toList(),
                d.orgaoDaGarantia() == null ? null
                        : new Garantia(d.orgaoDaGarantia(), d.protocoloDaGarantia(), d.situacaoDaGarantia()),
                d.cronograma() == null ? null : new Obra(d.cronograma().previsaoOriginal(), d.cronograma().previsaoAtual(),
                        d.cronograma().diasProrrogados(), d.cronograma().pendente()));
    }
}
