package br.com.financiamento.originacao.entrypoint.api.web;

import br.com.financiamento.originacao.aplicacao.DetalheProposta;
import br.com.financiamento.shared.dominio.Violacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DetalhePropostaWebResponse(
        Long id,
        String tipoDeBem,
        String estado,
        List<Participante> participantes,
        Bem bem,
        Condicoes condicoes,
        List<Documento> documentos,
        boolean enquadrada,
        List<Violacao> pendenciasDeEnquadramento) {

    public record Participante(String papel, String forma, String documento, String nome,
                               LocalDate nascimento, BigDecimal rendaComprovada) {
    }

    public record Bem(String identificacao, BigDecimal valorDeCompra, BigDecimal valorDeAvaliacao,
                      String documentoDoVendedor, Integer anoModelo, Boolean construcaoAverbada) {
    }

    public record Condicoes(int versao, BigDecimal entradaPropria, BigDecimal entradaFgts, int prazoMeses,
                            BigDecimal parcela, BigDecimal valorFinanciado) {
    }

    public record Documento(String titular, String tipo) {
    }

    public static DetalhePropostaWebResponse de(DetalheProposta d) {
        var b = d.bem();
        var c = d.condicoes();
        return new DetalhePropostaWebResponse(
                d.id(),
                d.tipoDeBem(),
                d.estado().name(),
                d.participantes().stream().map(p -> new Participante(p.papel().name(), p.forma().name(),
                        p.documento(), p.nome(), p.nascimento(), p.rendaComprovada())).toList(),
                b == null ? null : new Bem(b.identificacao(), b.valorDeCompra(), b.valorDeAvaliacao(),
                        b.documentoDoVendedor(), b.anoModelo(), b.construcaoAverbada()),
                c == null ? null : new Condicoes(c.versao(), c.entradaPropria(), c.entradaFgts(), c.prazoMeses(),
                        c.parcela(), d.valorFinanciado()),
                d.documentos().stream().map(x -> new Documento(x.titular(), x.tipo())).toList(),
                d.enquadramento().isEmpty(),
                d.enquadramento());
    }
}
