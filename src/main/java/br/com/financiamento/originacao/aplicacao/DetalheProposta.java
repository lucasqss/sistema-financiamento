package br.com.financiamento.originacao.aplicacao;

import br.com.financiamento.originacao.dominio.Enquadramento;
import br.com.financiamento.originacao.dominio.EstadoProposta;
import br.com.financiamento.originacao.dominio.Proposta;
import br.com.financiamento.originacao.dominio.bem.BemDaProposta;
import br.com.financiamento.originacao.dominio.condicoes.Condicoes;
import br.com.financiamento.originacao.dominio.documento.DocumentoEntregue;
import br.com.financiamento.originacao.dominio.participante.Participante;
import br.com.financiamento.originacao.dominio.resultado.AvaliacaoDaProposta;
import br.com.financiamento.originacao.dominio.resultado.Contraproposta;
import br.com.financiamento.originacao.dominio.resultado.CreditoDaProposta;
import br.com.financiamento.shared.dominio.Violacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Resultado dos casos de uso da proposta, independente de canal. */
public record DetalheProposta(
        Long id,
        String tipoDeBem,
        EstadoProposta estado,
        List<Participante> participantes,
        BemDaProposta bem,
        Condicoes condicoes,
        BigDecimal valorFinanciado,
        List<DocumentoEntregue> documentos,
        List<Violacao> enquadramento,
        CreditoDaProposta credito,
        AvaliacaoDaProposta avaliacao,
        Contraproposta contraproposta,
        String pendencia,
        LocalDate aprovacaoValidaAte) {

    static DetalheProposta de(Proposta p, Enquadramento e) {
        var financiado = p.condicoes() == null || p.bem() == null || p.bem().valorDeCompra() == null
                ? null : p.condicoes().valorFinanciado(p.bem().valorDeCompra());
        return new DetalheProposta(p.id(), p.tipoDeBem().codigo(), p.estado(), p.participantes(), p.bem(),
                p.condicoes(), financiado, p.documentos(), e.violacoes(), p.credito(), p.avaliacao(),
                p.contraproposta(), p.pendencia(), p.aprovacaoValidaAte());
    }
}
