package br.com.financiamento.documentos.aplicacao;

import br.com.financiamento.documentos.DocumentoRecebido;
import br.com.financiamento.documentos.OuvinteDosDocumentos;
import br.com.financiamento.documentos.dominio.ArmazemDeArquivos;
import br.com.financiamento.documentos.dominio.Documento;
import br.com.financiamento.documentos.dominio.DocumentoRepository;
import br.com.financiamento.shared.validacao.CadeiaDeValidacao;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Clock;

/**
 * Recebe e guarda o arquivo de um documento da proposta. Um novo envio do mesmo
 * documento substitui o anterior ainda não aceito. Quem acompanha os documentos
 * (a Originação) é avisado na mesma transação e pode recusar o envio (RN-0009,
 * invariante 2 da proposta), o que desfaz tudo.
 */
@ApplicationScoped
public class EnviarDocumentoService {

    public record Envio(Long propostaId, String titular, String tipo, String nomeDoArquivo, String tipoDeConteudo,
                        byte[] conteudo) {
    }

    @Inject
    DocumentoRepository documentos;

    @Inject
    ArmazemDeArquivos armazem;

    @Inject
    @Any
    Instance<OuvinteDosDocumentos> ouvintes;

    @Inject
    Clock relogio;

    @Transactional
    public Documento executar(Envio e) {
        // Cadeia de domínio (ADR 0011): o conteúdo do arquivo é íntegro e do tipo declarado (ADR 0010).
        CadeiaDeValidacao.<Envio>de((envio, proximo) -> {
            Documento.validar(envio.tipo(), envio.tipoDeConteudo(), envio.conteudo());
            proximo.seguir(envio);
        }).validar(e);
        var titular = Documento.titularNormalizado(e.titular());
        documentos.vigentes(e.propostaId(), titular, e.tipo()).forEach(Documento::substituir);
        var chave = armazem.guardar(e.conteudo());
        var documento = Documento.enviar(e.propostaId(), titular, e.tipo(), e.nomeDoArquivo(), e.tipoDeConteudo(),
                e.conteudo(), chave, relogio.instant());
        documentos.persistAndFlush(documento);
        var recebido = new DocumentoRecebido(documento.id(), documento.propostaId(), documento.titular(), documento.tipo());
        ouvintes.forEach(o -> o.documentoRecebido(recebido));
        return documento;
    }
}
