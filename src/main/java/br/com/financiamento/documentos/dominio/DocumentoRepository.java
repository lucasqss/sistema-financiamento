package br.com.financiamento.documentos.dominio;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class DocumentoRepository implements PanacheRepositoryBase<Documento, Long> {

    public List<Documento> daProposta(Long propostaId) {
        return list("propostaId = ?1 order by id", propostaId);
    }

    public List<Documento> naSituacao(SituacaoDoDocumento situacao) {
        return list("situacao = ?1 order by id", situacao);
    }

    /** Os envios ainda vigentes do mesmo documento (mesma proposta, titular e tipo). */
    public List<Documento> vigentes(Long propostaId, String titular, String tipo) {
        return list("propostaId = ?1 and titular = ?2 and lower(tipo) = lower(?3) and situacao <> ?4",
                propostaId, titular, tipo.trim(), SituacaoDoDocumento.SUBSTITUIDO);
    }
}
