package br.com.financiamento.originacao.dominio;

import br.com.financiamento.bem.CategoriaDeBem;
import br.com.financiamento.bem.FormaDoParticipante;
import br.com.financiamento.bem.TipoDeBem;
import br.com.financiamento.originacao.dominio.bem.BemDaProposta;
import br.com.financiamento.originacao.dominio.documento.DocumentoEntregue;
import br.com.financiamento.originacao.dominio.participante.Papel;
import br.com.financiamento.originacao.dominio.participante.Participante;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static br.com.financiamento.DocumentosParaTeste.cpf;

/** Monta propostas completas e enquadradas, para cada teste mudar só o que examina. */
final class PropostaParaTeste {

    static final LocalDate HOJE = LocalDate.of(2026, 11, 5);
    static final String PROPONENTE = cpf(1);
    static final String VENDEDOR = cpf(2);

    private PropostaParaTeste() {
    }

    static Proposta rascunho(String tipo) {
        return Proposta.iniciar(TipoDeBem.de(tipo), Instant.parse("2026-11-05T10:00:00Z"));
    }

    static Participante proponentePf(BigDecimal renda) {
        return new Participante(Papel.PROPONENTE, FormaDoParticipante.PESSOA_FISICA, PROPONENTE, "Ana",
                LocalDate.of(1990, 5, 20), renda);
    }

    /** Proposta completa: participantes, bem, condições e todos os documentos exigidos. */
    static Proposta completa(String tipo, CategoriaDeBem categoria, BigDecimal compra, BigDecimal entrada, int prazo,
                             BigDecimal parcela, Integer anoModelo, Boolean construcao) {
        var p = rascunho(tipo);
        p.informarParticipantes(List.of(proponentePf(new BigDecimal("20000"))));
        p.informarBem(new BemDaProposta("BEM-1", compra, VENDEDOR, anoModelo, construcao));
        p.informarCondicoes(entrada, BigDecimal.ZERO, prazo, parcela);
        entregarDocumentos(p, categoria);
        return p;
    }

    /** Entrega os documentos exigidos que ainda faltam. */
    static void entregarDocumentos(Proposta p, CategoriaDeBem categoria) {
        for (var d : categoria.documentosDoBem(p.tipoDeBem())) {
            entregar(p, new DocumentoEntregue(DocumentoEntregue.DO_BEM, d));
        }
        for (var participante : p.participantes()) {
            for (var d : categoria.documentosDoParticipante(p.tipoDeBem(), participante.forma())) {
                entregar(p, new DocumentoEntregue(participante.documento(), d));
            }
        }
    }

    private static void entregar(Proposta p, DocumentoEntregue d) {
        if (!p.documentos().contains(d)) p.registrarDocumento(d);
    }

    static Enquadramento enquadrar(Proposta p, CategoriaDeBem categoria) {
        return RegrasDeEnquadramento.avaliar(p, categoria, HOJE, false);
    }

    static List<String> regras(Enquadramento e) {
        return e.violacoes().stream().map(v -> v.regra()).distinct().sorted().toList();
    }
}
