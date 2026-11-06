package br.com.financiamento.desembolso.aplicacao;

import br.com.financiamento.desembolso.dominio.Desembolso;
import br.com.financiamento.desembolso.dominio.DesembolsoRepository;
import br.com.financiamento.shared.validacao.ContextoDeDominio;
import br.com.financiamento.shared.validacao.Elo;
import br.com.financiamento.desembolso.dominio.ErrosDoDesembolso;
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

    /** Cadeia de domínio (ADR 0011): o desembolso existe, depois os elos do caso de uso. */
    @SafeVarargs
    final Desembolso buscar(Long id, Elo<ContextoDeDominio<Long, Desembolso>>... doCasoDeUso) {
        return ContextoDeDominio.validar(id, ContextoDeDominio.carregar(repositorio::findByIdOptional,
                ErrosDoDesembolso.DESEMBOLSO_NAO_ENCONTRADO), doCasoDeUso);
    }

    /** Cadeia de domínio do pagamento: o protocolo de liberação é nosso. */
    Desembolso doProtocolo(String protocolo) {
        return ContextoDeDominio.<String, Desembolso>validar(protocolo, ContextoDeDominio.carregar(
                p -> Desembolso.contratoDoProtocolo(p).flatMap(repositorio::doContrato), ErrosDoDesembolso.PROTOCOLO_DESCONHECIDO));
    }

    /** Cadeia de domínio da medição (UC-0013): o contrato tem desembolso, isto é, garantia constituída. */
    Desembolso doContratoComGarantia(Long contratoId) {
        return ContextoDeDominio.<Long, Desembolso>validar(contratoId, ContextoDeDominio.carregar(
                repositorio::doContrato, ErrosDoDesembolso.MEDICAO_SEM_GARANTIA));
    }

    void salvar(Desembolso desembolso) {
        repositorio.persistAndFlush(desembolso);
        for (var e : desembolso.retirarEventos()) outbox.registrar("Desembolso", desembolso.id(), e.nome(), e.dados());
    }
}
