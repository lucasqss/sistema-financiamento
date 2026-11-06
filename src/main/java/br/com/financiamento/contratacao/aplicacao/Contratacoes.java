package br.com.financiamento.contratacao.aplicacao;

import br.com.financiamento.contratacao.dominio.Contrato;
import br.com.financiamento.contratacao.dominio.ContratoRepository;
import br.com.financiamento.contratacao.dominio.PreparoDoContrato;
import br.com.financiamento.contratacao.dominio.PreparoDoContratoRepository;
import br.com.financiamento.shared.validacao.ContextoDeDominio;
import br.com.financiamento.shared.validacao.Elo;
import br.com.financiamento.contratacao.dominio.ErrosDaContratacao;
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

    /** Cadeia de domínio (ADR 0011): a contratação existe, depois os elos do caso de uso. */
    @SafeVarargs
    final PreparoDoContrato preparo(Long id, Elo<ContextoDeDominio<Long, PreparoDoContrato>>... doCasoDeUso) {
        return ContextoDeDominio.validar(id, ContextoDeDominio.carregar(preparos::findByIdOptional,
                ErrosDaContratacao.CONTRATACAO_NAO_ENCONTRADA), doCasoDeUso);
    }

    /** Cadeia de domínio (ADR 0011): o contrato existe, depois os elos do caso de uso. */
    @SafeVarargs
    final Contrato contrato(Long id, Elo<ContextoDeDominio<Long, Contrato>>... doCasoDeUso) {
        return ContextoDeDominio.validar(id, ContextoDeDominio.carregar(contratos::findByIdOptional,
                ErrosDaContratacao.CONTRATO_NAO_ENCONTRADO), doCasoDeUso);
    }

    /** CV-0002: o estado do contrato aceita o evento do caso de uso. */
    static Elo<ContextoDeDominio<Long, Contrato>> aceita(String eventoNoModelo) {
        return ContextoDeDominio.exigir(c -> c.estado().aceitaEvento(eventoNoModelo), ErrosDaContratacao.TRANSICAO_INVALIDA,
                c -> new Object[]{c.estado().name().toLowerCase().replace('_', ' '), eventoNoModelo});
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
