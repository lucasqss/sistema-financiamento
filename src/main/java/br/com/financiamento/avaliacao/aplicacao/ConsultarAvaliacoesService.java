package br.com.financiamento.avaliacao.aplicacao;

import br.com.financiamento.avaliacao.dominio.Avaliacao;
import br.com.financiamento.avaliacao.dominio.AvaliacaoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ConsultarAvaliacoesService {

    @Inject
    AvaliacaoRepository avaliacoes;

    public List<Avaliacao> daProposta(Long propostaId) {
        return avaliacoes.daProposta(propostaId);
    }
}
