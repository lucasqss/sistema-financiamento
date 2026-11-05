package br.com.financiamento.desembolso.dominio;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class DesembolsoRepository implements PanacheRepositoryBase<Desembolso, Long> {

    public Optional<Desembolso> doContrato(Long contratoId) {
        return find("contratoId", contratoId).firstResultOptional();
    }
}
