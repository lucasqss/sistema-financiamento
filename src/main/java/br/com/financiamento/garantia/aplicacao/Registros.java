package br.com.financiamento.garantia.aplicacao;

import br.com.financiamento.garantia.dominio.Registro;
import br.com.financiamento.garantia.dominio.RegistroRepository;
import br.com.financiamento.shared.dominio.NaoEncontradoException;
import br.com.financiamento.shared.outbox.Outbox;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/** O que os casos de uso da garantia fazem em comum: carregar e levar os eventos ao outbox. */
@ApplicationScoped
class Registros {

    @Inject
    RegistroRepository repositorio;

    @Inject
    Outbox outbox;

    Registro buscar(Long id) {
        return repositorio.findByIdOptional(id)
                .orElseThrow(() -> new NaoEncontradoException("Registro de garantia " + id + " não encontrado."));
    }

    Registro doProtocolo(String protocolo) {
        return Registro.contratoDoProtocolo(protocolo).flatMap(repositorio::doContrato)
                .orElseThrow(() -> new NaoEncontradoException("Protocolo " + protocolo + " não é nosso."));
    }

    void salvar(Registro registro) {
        repositorio.persistAndFlush(registro);
        for (var e : registro.retirarEventos()) outbox.registrar("Garantia", registro.id(), e.nome(), e.dados());
    }
}
