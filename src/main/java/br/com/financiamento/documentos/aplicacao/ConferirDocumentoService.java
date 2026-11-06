package br.com.financiamento.documentos.aplicacao;

import br.com.financiamento.documentos.DocumentoConferido;
import br.com.financiamento.documentos.OuvinteDosDocumentos;
import br.com.financiamento.documentos.dominio.Documento;
import br.com.financiamento.documentos.dominio.DocumentoRepository;
import br.com.financiamento.documentos.dominio.MotivoDaRecusa;
import br.com.financiamento.shared.dominio.NaoEncontradoException;
import br.com.financiamento.shared.outbox.Outbox;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Clock;
import java.util.HashMap;

/**
 * UC-0024: o analista (ATR-0005) confere o documento. Emite DocumentoAceito ou
 * DocumentoRecusado e avisa a proposta na mesma transação: a conferência em
 * proposta não submetida é recusada e desfeita.
 */
@ApplicationScoped
public class ConferirDocumentoService {

    @Inject
    DocumentoRepository documentos;

    @Inject
    Outbox outbox;

    @Inject
    @Any
    Instance<OuvinteDosDocumentos> ouvintes;

    @Inject
    Clock relogio;

    @Transactional
    public Documento executar(Long documentoId, boolean aceito, MotivoDaRecusa motivo, String observacao) {
        var documento = buscar(documentoId);
        if (!documento.conferir(aceito, motivo, observacao, relogio.instant())) return documento;
        var dados = new HashMap<String, Object>();
        dados.put("propostaId", documento.propostaId());
        dados.put("titular", documento.titular());
        dados.put("tipo", documento.tipo());
        if (!aceito) dados.put("motivo", documento.motivoDescrito());
        outbox.registrar("Documento", documento.id(), aceito ? "DocumentoAceito" : "DocumentoRecusado", dados);

        var conferido = new DocumentoConferido(documento.id(), documento.propostaId(), documento.titular(),
                documento.tipo(), aceito, documento.motivoDescrito());
        ouvintes.forEach(o -> o.documentoConferido(conferido));
        return documento;
    }

    Documento buscar(Long id) {
        return documentos.findByIdOptional(id)
                .orElseThrow(() -> new NaoEncontradoException("Documento " + id + " não encontrado."));
    }
}
