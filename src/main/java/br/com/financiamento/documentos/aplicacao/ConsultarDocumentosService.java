package br.com.financiamento.documentos.aplicacao;

import br.com.financiamento.documentos.dominio.ArmazemDeArquivos;
import br.com.financiamento.documentos.dominio.Documento;
import br.com.financiamento.documentos.dominio.DocumentoRepository;
import br.com.financiamento.documentos.dominio.SituacaoDoDocumento;
import br.com.financiamento.shared.dominio.NaoEncontradoException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

/** Os documentos de uma proposta, a fila de conferência do analista e o arquivo de cada um. */
@ApplicationScoped
public class ConsultarDocumentosService {

    public record Arquivo(String nome, String tipoDeConteudo, byte[] conteudo) {
    }

    @Inject
    DocumentoRepository documentos;

    @Inject
    ArmazemDeArquivos armazem;

    @Transactional
    public List<Documento> daProposta(Long propostaId) {
        return documentos.daProposta(propostaId);
    }

    @Transactional
    public List<Documento> naSituacao(SituacaoDoDocumento situacao) {
        return documentos.naSituacao(situacao);
    }

    @Transactional
    public Arquivo arquivo(Long documentoId) {
        var d = documentos.findByIdOptional(documentoId)
                .orElseThrow(() -> new NaoEncontradoException("Documento " + documentoId + " não encontrado."));
        var conteudo = armazem.ler(d.chaveNoArmazem());
        if (!Documento.sha256(conteudo).equals(d.sha256())) {
            throw new IllegalStateException("O arquivo do documento " + documentoId + " não confere com o recebido.");
        }
        return new Arquivo(d.nomeDoArquivo(), d.tipoDeConteudo(), conteudo);
    }
}
