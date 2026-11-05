package br.com.financiamento.avaliacao.dominio;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class AvaliacaoRepository implements PanacheRepositoryBase<Avaliacao, Long> {

    public List<Avaliacao> daProposta(Long propostaId) {
        return list("propostaId = ?1 order by id", propostaId);
    }
}
