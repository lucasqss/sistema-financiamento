package br.com.financiamento.credito.aplicacao;

import br.com.financiamento.credito.dominio.AnaliseDeCredito;
import br.com.financiamento.credito.dominio.AnaliseDeCreditoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

/** O analista acompanha as análises de uma proposta. */
@ApplicationScoped
public class ConsultarAnalisesService {

    @Inject
    AnaliseDeCreditoRepository analises;

    public List<AnaliseDeCredito> daProposta(Long propostaId) {
        return analises.daProposta(propostaId);
    }
}
