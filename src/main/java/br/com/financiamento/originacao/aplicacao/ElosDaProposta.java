package br.com.financiamento.originacao.aplicacao;

import br.com.financiamento.originacao.dominio.ErrosDaOriginacao;
import br.com.financiamento.originacao.dominio.Proposta;
import br.com.financiamento.originacao.dominio.PropostaRepository;
import br.com.financiamento.shared.seguranca.ErroDeSeguranca;
import br.com.financiamento.shared.seguranca.UsuarioAtual;
import br.com.financiamento.shared.validacao.CadeiaDeValidacao;
import br.com.financiamento.shared.validacao.Elo;
import br.com.financiamento.shared.validacao.ErroDeDominioException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

/**
 * Os elos de domínio dos casos de uso da proposta (ADR 0011). Cada caso de uso
 * monta a sua cadeia: a proposta existe e, conforme o caso, aceita alteração ou o
 * evento do ciclo de vida. A cadeia para no primeiro elo que falhar.
 */
@ApplicationScoped
class ElosDaProposta {

    @Inject
    PropostaRepository repositorio;

    @Inject
    UsuarioAtual usuarioAtual;

    /** Roda a cadeia "a proposta existe" seguida dos elos do caso de uso e devolve a proposta carregada. */
    @SafeVarargs
    final Proposta validar(Long propostaId, Elo<ContextoDaProposta>... doCasoDeUso) {
        var elos = new ArrayList<Elo<ContextoDaProposta>>();
        elos.add(existe());
        elos.add(usuarioParticipa());
        elos.addAll(List.of(doCasoDeUso));
        var contexto = new ContextoDaProposta(propostaId);
        CadeiaDeValidacao.de(elos).validar(contexto);
        return contexto.proposta();
    }

    /** A proposta existe (404). */
    Elo<ContextoDaProposta> existe() {
        return (contexto, proximo) -> {
            contexto.carregou(repositorio.findByIdOptional(contexto.propostaId()).orElseThrow(() ->
                    new ErroDeDominioException(ErrosDaOriginacao.PROPOSTA_NAO_ENCONTRADA, contexto.propostaId())));
            proximo.seguir(contexto);
        };
    }

    /**
     * ADR 0012: proponente, coobrigado, vendedor e correspondente só agem na proposta de que participam
     * (403). Analista, engenheiro e sistemas não têm essa restrição.
     */
    Elo<ContextoDaProposta> usuarioParticipa() {
        return (contexto, proximo) -> {
            if (!usuarioAtual.participaDe(contexto.proposta().documentosEnvolvidos())) {
                throw new ErroDeDominioException(ErroDeSeguranca.NAO_PARTICIPA, "proposta");
            }
            proximo.seguir(contexto);
        };
    }

    /** Invariante 2 do AGR-0001: só o rascunho e a proposta com pendência aceitam alteração. */
    Elo<ContextoDaProposta> aceitaAlteracao() {
        return (contexto, proximo) -> {
            var estado = contexto.proposta().estado();
            if (!estado.aceitaAlteracao()) {
                throw new ErroDeDominioException(ErrosDaOriginacao.NAO_ACEITA_ALTERACAO, nome(contexto.proposta()));
            }
            proximo.seguir(contexto);
        };
    }

    /** CV-0001: o estado atual aceita o evento do caso de uso (ex.: "PropostaSubmetida"). */
    Elo<ContextoDaProposta> aceita(String eventoNoModelo) {
        return (contexto, proximo) -> {
            if (!contexto.proposta().estado().aceitaEvento(eventoNoModelo)) {
                throw new ErroDeDominioException(ErrosDaOriginacao.TRANSICAO_INVALIDA, nome(contexto.proposta()), eventoNoModelo);
            }
            proximo.seguir(contexto);
        };
    }

    private static String nome(Proposta p) {
        return p.estado().name().toLowerCase().replace('_', ' ');
    }
}
