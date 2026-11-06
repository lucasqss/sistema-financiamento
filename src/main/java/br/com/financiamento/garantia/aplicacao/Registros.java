package br.com.financiamento.garantia.aplicacao;

import br.com.financiamento.garantia.dominio.Registro;
import br.com.financiamento.garantia.dominio.RegistroRepository;
import br.com.financiamento.shared.validacao.ContextoDeDominio;
import br.com.financiamento.shared.validacao.Elo;
import br.com.financiamento.garantia.dominio.SituacaoDoRegistro;
import br.com.financiamento.garantia.dominio.ErrosDaGarantia;
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

    /** Cadeia de domínio (ADR 0011): o registro existe, depois os elos do caso de uso. */
    @SafeVarargs
    final Registro buscar(Long id, Elo<ContextoDeDominio<Long, Registro>>... doCasoDeUso) {
        return ContextoDeDominio.validar(id, ContextoDeDominio.carregar(repositorio::findByIdOptional,
                ErrosDaGarantia.REGISTRO_NAO_ENCONTRADO), doCasoDeUso);
    }

    /** Cadeia de domínio do retorno: o protocolo é nosso (RN-0029 classifica, mas só o que é nosso). */
    Registro doProtocolo(String protocolo) {
        return ContextoDeDominio.<String, Registro>validar(protocolo, ContextoDeDominio.carregar(
                p -> Registro.contratoDoProtocolo(p).flatMap(repositorio::doContrato), ErrosDaGarantia.PROTOCOLO_DESCONHECIDO));
    }

    /** A situação do registro é a que o caso de uso exige. */
    static Elo<ContextoDeDominio<Long, Registro>> naSituacao(ErrosDaGarantia erro, SituacaoDoRegistro... aceitas) {
        var lista = java.util.List.of(aceitas);
        return ContextoDeDominio.exigir(r -> lista.contains(r.situacao()), erro, r -> new Object[0]);
    }

    void salvar(Registro registro) {
        repositorio.persistAndFlush(registro);
        for (var e : registro.retirarEventos()) outbox.registrar("Garantia", registro.id(), e.nome(), e.dados());
    }
}
