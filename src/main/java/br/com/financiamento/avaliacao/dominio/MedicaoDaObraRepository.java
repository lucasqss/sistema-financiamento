package br.com.financiamento.avaliacao.dominio;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MedicaoDaObraRepository implements PanacheRepositoryBase<MedicaoDaObra, Long> {

    public Optional<MedicaoDaObra> ultimaDoContrato(Long contratoId) {
        return find("contratoId = ?1 order by id desc", contratoId).firstResultOptional();
    }

    public List<MedicaoDaObra> doContrato(Long contratoId) {
        return list("contratoId = ?1 order by id", contratoId);
    }
}
