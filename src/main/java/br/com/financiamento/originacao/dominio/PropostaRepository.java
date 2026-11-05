package br.com.financiamento.originacao.dominio;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Arrays;
import java.util.List;

@ApplicationScoped
public class PropostaRepository implements PanacheRepositoryBase<Proposta, Long> {

    private static final List<EstadoProposta> ATIVOS =
            Arrays.stream(EstadoProposta.values()).filter(EstadoProposta::ativa).toList();

    /** Propostas com prazo correndo: aprovadas (RN-0021, RN-0022) e em contraproposta (RN-0020). */
    public List<Proposta> comPrazoCorrendo() {
        return list("estado in ?1", List.of(EstadoProposta.APROVADA, EstadoProposta.EM_CONTRAPROPOSTA));
    }

    /** RN-0004: o bem já está em outra proposta ativa? */
    public boolean bemEmOutraPropostaAtiva(String identificacao, Long exceto) {
        if (identificacao == null || identificacao.isBlank()) return false;
        return count("bem.identificacao = ?1 and estado in ?2 and id <> ?3",
                identificacao, ATIVOS, exceto == null ? -1L : exceto) > 0;
    }
}
