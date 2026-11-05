package br.com.financiamento.originacao.aplicacao;

import br.com.financiamento.originacao.dominio.documento.DocumentoEntregue;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/** Parte de UC-0002 e UC-0003: anexar os documentos do bem e dos participantes (RN-0009). */
@ApplicationScoped
public class RegistrarDocumentoService {

    @Inject
    Propostas propostas;

    @Transactional
    public DetalheProposta executar(Long propostaId, String titular, String tipo) {
        var proposta = propostas.buscar(propostaId);
        proposta.registrarDocumento(new DocumentoEntregue(titular, tipo));
        return propostas.salvar(proposta);
    }
}
