package br.com.financiamento.originacao.aplicacao;

import br.com.financiamento.originacao.dominio.Enquadrador;
import br.com.financiamento.originacao.dominio.Enquadramento;
import br.com.financiamento.originacao.dominio.Proposta;
import br.com.financiamento.originacao.dominio.PropostaRepository;
import br.com.financiamento.shared.dominio.NaoEncontradoException;
import br.com.financiamento.shared.dominio.RegraVioladaException;
import br.com.financiamento.shared.outbox.Outbox;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

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

    Proposta buscar(Long id) {
        return repositorio.findByIdOptional(id)
                .orElseThrow(() -> new NaoEncontradoException("Proposta " + id + " não encontrada."));
    }

    /** Recusa a operação se o enquadramento violar alguma das regras que o caso de uso confere na hora. */
    Enquadramento exigirRegras(Proposta proposta, Set<String> regras) {
        var e = enquadrador.enquadrar(proposta);
        var violadas = e.violacoes().stream().filter(v -> regras.contains(v.regra())).toList();
        if (!violadas.isEmpty()) throw new RegraVioladaException(violadas);
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
