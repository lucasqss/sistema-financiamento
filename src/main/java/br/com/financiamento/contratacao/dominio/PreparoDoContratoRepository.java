package br.com.financiamento.contratacao.dominio;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class PreparoDoContratoRepository implements PanacheRepositoryBase<PreparoDoContrato, Long> {

    public Optional<PreparoDoContrato> daProposta(Long propostaId) {
        return find("propostaId", propostaId).firstResultOptional();
    }
}
