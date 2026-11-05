package br.com.financiamento.originacao.aplicacao;

import br.com.financiamento.bem.BemFacade;
import br.com.financiamento.bem.TipoDeBem;
import br.com.financiamento.originacao.dominio.Proposta;
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

    @Transactional
    public DetalheProposta executar(String tipoDeBem) {
        var tipo = TipoDeBem.de(tipoDeBem);
        bens.categoria(tipo); // recusa tipo que nenhum plug-in atende
        return propostas.salvar(Proposta.iniciar(tipo, relogio.instant()));
    }
}
