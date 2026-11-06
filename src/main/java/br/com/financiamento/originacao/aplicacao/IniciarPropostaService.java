package br.com.financiamento.originacao.aplicacao;

import br.com.financiamento.bem.BemFacade;
import br.com.financiamento.bem.TipoDeBem;
import br.com.financiamento.originacao.dominio.Proposta;
import br.com.financiamento.shared.seguranca.Papeis;
import br.com.financiamento.shared.seguranca.UsuarioAtual;
import br.com.financiamento.shared.validacao.CadeiaDeValidacao;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Clock;

/** UC-0001: iniciar proposta. */
@ApplicationScoped
public class IniciarPropostaService {

    @Inject
    BemFacade bens;

    @Inject
    Propostas propostas;

    @Inject
    Clock relogio;

    @Inject
    UsuarioAtual usuarioAtual;

    @Transactional
    public DetalheProposta executar(String tipoDeBem) {
        var tipo = TipoDeBem.de(tipoDeBem);
        // Cadeia de domínio (ADR 0011): o tipo de bem é atendido por um plug-in (DIM-0001).
        CadeiaDeValidacao.<TipoDeBem>de((t, proximo) -> {
            bens.categoria(t);
            proximo.seguir(t);
        }).validar(tipo);
        var usuario = usuarioAtual.usuario();
        var papel = usuario.flatMap(u -> java.util.stream.Stream.of(Papeis.PROPONENTE, Papeis.VENDEDOR, Papeis.CORRESPONDENTE)
                .filter(u::tem).findFirst()).orElse(null);
        return propostas.salvar(Proposta.iniciar(tipo, relogio.instant(), usuario.map(u -> u.documento()).orElse(null), papel));
    }
}
