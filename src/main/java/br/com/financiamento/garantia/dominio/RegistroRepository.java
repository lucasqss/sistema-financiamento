package br.com.financiamento.garantia.dominio;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class RegistroRepository implements PanacheRepositoryBase<Registro, Long> {

    public Optional<Registro> doContrato(Long contratoId) {
        return find("contratoId", contratoId).firstResultOptional();
    }
}
