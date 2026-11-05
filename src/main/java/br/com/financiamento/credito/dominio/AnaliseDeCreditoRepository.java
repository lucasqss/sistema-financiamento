package br.com.financiamento.credito.dominio;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class AnaliseDeCreditoRepository implements PanacheRepositoryBase<AnaliseDeCredito, Long> {

    public List<AnaliseDeCredito> daProposta(Long propostaId) {
        return list("propostaId = ?1 order by id", propostaId);
    }
}
