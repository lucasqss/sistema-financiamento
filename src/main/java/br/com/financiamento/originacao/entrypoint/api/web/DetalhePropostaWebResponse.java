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
        List<Violacao> pendenciasDeEnquadramento,
        Credito credito,
        Avaliacao avaliacao,
        Contraproposta contraproposta,
        String pendencia,
        LocalDate aprovacaoValidaAte) {

    public record Credito(String resultado, int condicoesVersao, String ressalva) {
    }

    public record Avaliacao(boolean aceitoComoGarantia, BigDecimal valor, String fonte, LocalDate avaliadaEm,
                            LocalDate validaAte) {
    }

    public record Contraproposta(BigDecimal entradaPropria, BigDecimal entradaFgts, int prazoMeses, BigDecimal parcela,
                                 String ressalva, LocalDate validaAte) {
    }

    public record Participante(String papel, String forma, String documento, String nome,
                               LocalDate nascimento, BigDecimal rendaComprovada) {
    }

    public record Bem(String identificacao, BigDecimal valorDeCompra, BigDecimal valorDeAvaliacao,
                      String documentoDoVendedor, Integer anoModelo, Boolean construcaoAverbada) {
    }

    public record Condicoes(int versao, BigDecimal entradaPropria, BigDecimal entradaFgts, int prazoMeses,
                            BigDecimal parcela, BigDecimal valorFinanciado) {
    }

    public record Documento(Long documentoId, String titular, String tipo, String situacao, String motivo) {
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
                d.documentos().stream().map(x -> new Documento(x.documentoId(), x.titular(), x.tipo(), x.situacao().name(), x.motivo())).toList(),
                d.enquadramento().isEmpty(),
                d.enquadramento(),
                d.credito() == null ? null : new Credito(d.credito().resultado().name(), d.credito().condicoesVersao(),
                        d.credito().ressalva()),
                d.avaliacao() == null ? null : new Avaliacao(d.avaliacao().aceitoComoGarantia(), d.avaliacao().valor(),
                        d.avaliacao().fonte(), d.avaliacao().avaliadaEm(), d.avaliacao().validaAte()),
                d.contraproposta() == null ? null : new Contraproposta(d.contraproposta().entradaPropria(),
                        d.contraproposta().entradaFgts(), d.contraproposta().prazoMeses(), d.contraproposta().parcela(),
                        d.contraproposta().ressalva(), d.contraproposta().validaAte()),
                d.pendencia(),
                d.aprovacaoValidaAte());
    }
}
