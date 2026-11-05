package br.com.financiamento.desembolso.aplicacao;

import br.com.financiamento.desembolso.dominio.Desembolso;
import br.com.financiamento.desembolso.dominio.DesembolsoRepository;
import br.com.financiamento.shared.dominio.NaoEncontradoException;
import br.com.financiamento.shared.outbox.Outbox;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/** O que os casos de uso do desembolso fazem em comum: carregar e levar os eventos ao outbox. */
@ApplicationScoped
class Desembolsos {

    @Inject
    DesembolsoRepository repositorio;

    @Inject
    Outbox outbox;

    Desembolso buscar(Long id) {
        return repositorio.findByIdOptional(id)
                .orElseThrow(() -> new NaoEncontradoException("Desembolso " + id + " não encontrado."));
    }

    void salvar(Desembolso desembolso) {
        repositorio.persistAndFlush(desembolso);
        for (var e : desembolso.retirarEventos()) outbox.registrar("Desembolso", desembolso.id(), e.nome(), e.dados());
    }
}
