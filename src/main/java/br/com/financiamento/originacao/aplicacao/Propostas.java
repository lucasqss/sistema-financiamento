package br.com.financiamento.originacao.aplicacao;

import br.com.financiamento.avaliacao.AvaliacaoFacade;
import br.com.financiamento.avaliacao.SolicitacaoDeAvaliacao;
import br.com.financiamento.bem.BemFacade;
import br.com.financiamento.credito.CreditoFacade;
import br.com.financiamento.credito.SolicitacaoDeAnalise;
import br.com.financiamento.originacao.dominio.Enquadrador;
import br.com.financiamento.originacao.dominio.Enquadramento;
import br.com.financiamento.originacao.dominio.Proposta;
import br.com.financiamento.originacao.dominio.PropostaRepository;
import br.com.financiamento.originacao.dominio.participante.Participante;
import br.com.financiamento.shared.validacao.ErroDeDominioException;
import br.com.financiamento.shared.outbox.Outbox;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Set;

/** O que todos os casos de uso da proposta fazem: carregar, enquadrar, levar eventos ao outbox. */
@ApplicationScoped
class Propostas {

    static final String AGREGADO = "Proposta";

    @Inject
    PropostaRepository repositorio;

    @Inject
    Enquadrador enquadrador;

    @Inject
    Outbox outbox;

    @Inject
    CreditoFacade credito;

    @Inject
    AvaliacaoFacade avaliacao;

    @Inject
    BemFacade bens;

    @Inject
    Clock relogio;

    LocalDate hoje() {
        return LocalDate.now(relogio);
    }

    /** UC-0006: enquadra com o que chegou e decide; sem resultado suficiente, a proposta espera. */
    Enquadramento decidir(Proposta proposta) {
        var e = enquadrador.enquadrar(proposta);
        var tipo = proposta.tipoDeBem();
        proposta.decidir(e, hoje(), bens.categoria(tipo).validadeDaContraproposta());
        return e;
    }

    /**
     * Pede ao Crédito e à Avaliação o que a proposta ainda não tem (EVT-0002 dispara
     * UC-0011 e UC-0012), na mesma transação (ADR 0005).
     */
    void solicitarAnalises(Proposta p) {
        if (p.precisaDeAnaliseDeCredito()) {
            var c = p.condicoes();
            var renda = p.participantes().stream().map(Participante::rendaComprovada).reduce(BigDecimal.ZERO, BigDecimal::add);
            credito.solicitarAnalise(new SolicitacaoDeAnalise(p.id(), c.versao(),
                    p.participantes().stream().map(Participante::documento).toList(), renda,
                    c.valorFinanciado(p.bem().valorDeCompra()), c.prazoMeses(), c.parcela()));
            p.analiseDeCreditoSolicitada();
        }
        if (p.precisaDeAvaliacao(hoje())) {
            avaliacao.solicitarAvaliacao(new SolicitacaoDeAvaliacao(p.id(), p.tipoDeBem(), p.bem().identificacao(),
                    p.bem().valorDeCompra()));
            p.avaliacaoSolicitada();
        }
    }

    /** Recusa a operação se o enquadramento violar alguma das regras que o caso de uso confere na hora. */
    Enquadramento exigirRegras(Proposta proposta, Set<String> regras) {
        var e = enquadrador.enquadrar(proposta);
        var violadas = e.falhas().stream().filter(v -> regras.contains(v.regra())).toList();
        if (!violadas.isEmpty()) throw new ErroDeDominioException(violadas);
        return e;
    }

    /** Grava a proposta e os eventos dela no outbox, na mesma transação (ADR 0005). */
    DetalheProposta salvar(Proposta proposta, Enquadramento enquadramento) {
        repositorio.persistAndFlush(proposta);
        for (var evento : proposta.retirarEventos()) {
            outbox.registrar(AGREGADO, proposta.id(), evento.nome(), evento.dados());
        }
        return DetalheProposta.de(proposta, enquadramento);
    }

    DetalheProposta salvar(Proposta proposta) {
        return salvar(proposta, enquadrador.enquadrar(proposta));
    }
}
