package br.com.financiamento.originacao.dominio;

import br.com.financiamento.bem.BemFacade;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Clock;
import java.time.LocalDate;

/** Aplica o enquadramento (RN-0018) com o plug-in do tipo de bem e as outras propostas. */
@ApplicationScoped
public class Enquadrador {

    @Inject
    BemFacade bens;

    @Inject
    PropostaRepository propostas;

    @Inject
    Clock relogio;

    public Enquadramento enquadrar(Proposta proposta) {
        var categoria = bens.categoria(proposta.tipoDeBem());
        var bem = proposta.bem();
        boolean bemOcupado = bem != null && propostas.bemEmOutraPropostaAtiva(bem.identificacao(), proposta.id());
        return RegrasDeEnquadramento.avaliar(proposta, categoria, LocalDate.now(relogio), bemOcupado);
    }
}
