package br.com.financiamento.contratacao.aplicacao;

import br.com.financiamento.contratacao.dominio.Contrato;
import br.com.financiamento.contratacao.dominio.ContratoRepository;
import br.com.financiamento.contratacao.dominio.PreparoDoContrato;
import br.com.financiamento.contratacao.dominio.PreparoDoContratoRepository;
import br.com.financiamento.shared.dominio.NaoEncontradoException;
import br.com.financiamento.shared.outbox.Outbox;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/** O que os casos de uso da contratação fazem em comum: carregar e levar os eventos ao outbox. */
@ApplicationScoped
class Contratacoes {

    @Inject
    PreparoDoContratoRepository preparos;

    @Inject
    ContratoRepository contratos;

    @Inject
    Outbox outbox;

    PreparoDoContrato preparo(Long id) {
        return preparos.findByIdOptional(id)
                .orElseThrow(() -> new NaoEncontradoException("Contratação " + id + " não encontrada."));
    }

    Contrato contrato(Long id) {
        return contratos.findByIdOptional(id)
                .orElseThrow(() -> new NaoEncontradoException("Contrato " + id + " não encontrado."));
    }

    void salvar(PreparoDoContrato preparo) {
        preparos.persistAndFlush(preparo);
        for (var e : preparo.retirarEventos()) outbox.registrar("Contratacao", preparo.id(), e.nome(), e.dados());
    }

    void salvar(Contrato contrato) {
        contratos.persistAndFlush(contrato);
        for (var e : contrato.retirarEventos()) outbox.registrar("Contrato", contrato.id(), e.nome(), e.dados());
    }
}
