package br.com.financiamento.originacao.dominio;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Arrays;
import java.util.List;

@ApplicationScoped
public class PropostaRepository implements PanacheRepositoryBase<Proposta, Long> {

    private static final List<EstadoProposta> ATIVOS =
            Arrays.stream(EstadoProposta.values()).filter(EstadoProposta::ativa).toList();

    /** RN-0004: o bem já está em outra proposta ativa? */
    public boolean bemEmOutraPropostaAtiva(String identificacao, Long exceto) {
        if (identificacao == null || identificacao.isBlank()) return false;
        return count("bem.identificacao = ?1 and estado in ?2 and id <> ?3",
                identificacao, ATIVOS, exceto == null ? -1L : exceto) > 0;
    }
}
