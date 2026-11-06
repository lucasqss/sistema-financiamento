package br.com.financiamento.originacao.aplicacao;

import br.com.financiamento.originacao.Contratabilidade;
import br.com.financiamento.originacao.PropostaParaContratar;
import br.com.financiamento.originacao.PropostaParaContratar.ParticipanteDoContrato;
import br.com.financiamento.originacao.dominio.Proposta;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/** O lado da Originação na emissão do contrato (UC-0014). */
@ApplicationScoped
public class ContratacaoDaPropostaService {

    @Inject
    ElosDaProposta elos;

    @Inject
    Propostas propostas;

    @Transactional
    public Contratabilidade contratabilidade(Long propostaId) {
        var proposta = elos.validar(propostaId);
        var impedimento = proposta.impedimentoParaContratar(propostas.hoje());
        if (impedimento != null) {
            if (proposta.precisaDeAvaliacao(propostas.hoje())) propostas.solicitarAnalises(proposta);
            propostas.salvar(proposta);
            return new Contratabilidade(null, impedimento.mensagem(), impedimento.codigo());
        }
        return new Contratabilidade(paraContratar(proposta), null, null);
    }

    @Transactional
    public void contratoEmitido(Long propostaId, Long contratoId) {
        var proposta = elos.validar(propostaId);
        proposta.registrarContrato(contratoId, propostas.hoje());
        propostas.salvar(proposta);
    }

    private static PropostaParaContratar paraContratar(Proposta p) {
        var c = p.condicoes();
        var bem = p.bem();
        return new PropostaParaContratar(p.id(), p.tipoDeBem(), bem.identificacao(), bem.valorDeCompra(),
                bem.valorDeAvaliacao(), bem.documentoDoVendedor(), c.versao(), c.entradaPropria(), c.entradaFgts(),
                c.valorFinanciado(bem.valorDeCompra()), c.prazoMeses(), c.parcela(),
                p.participantes().stream().map(x -> new ParticipanteDoContrato(x.papel().name(), x.documento(), x.nome())).toList(),
                p.aprovacaoValidaAte());
    }
}
