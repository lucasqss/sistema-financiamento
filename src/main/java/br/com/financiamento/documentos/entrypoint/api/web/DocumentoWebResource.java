package br.com.financiamento.documentos.entrypoint.api.web;

import br.com.financiamento.documentos.aplicacao.ConferirDocumentoService;
import br.com.financiamento.documentos.aplicacao.ConsultarDocumentosService;
import br.com.financiamento.documentos.aplicacao.EnviarDocumentoService;
import br.com.financiamento.documentos.aplicacao.EnviarDocumentoService.Envio;
import br.com.financiamento.documentos.dominio.SituacaoDoDocumento;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.List;

/**
 * Canal web dos documentos: o proponente ou o correspondente enviam os arquivos;
 * o analista (ATR-0005) os confere (UC-0024).
 */
@Path("/web/documentos")
public class DocumentoWebResource {

    @Inject EnviarDocumentoService enviar;
    @Inject ConferirDocumentoService conferir;
    @Inject ConsultarDocumentosService consultar;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response enviar(@RestForm Long propostaId, @RestForm String titular, @RestForm String tipo,
                           @RestForm("arquivo") FileUpload arquivo) {
        if (propostaId == null || arquivo == null) throw new BadRequestException("Informe a proposta e o arquivo.");
        byte[] conteudo;
        try {
            conteudo = Files.readAllBytes(arquivo.uploadedFile());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        var d = enviar.executar(new Envio(propostaId, titular, tipo, arquivo.fileName(), arquivo.contentType(), conteudo));
        return Response.created(URI.create("/web/documentos/" + d.id())).entity(DocumentoWebResponse.de(d)).build();
    }

    /** Os documentos de uma proposta, ou a fila do analista (situacao=ENVIADO). */
    @GET
    public List<DocumentoWebResponse> listar(@QueryParam("proposta") Long propostaId,
                                             @QueryParam("situacao") SituacaoDoDocumento situacao) {
        var lista = propostaId != null ? consultar.daProposta(propostaId)
                : consultar.naSituacao(situacao == null ? SituacaoDoDocumento.ENVIADO : situacao);
        return lista.stream().map(DocumentoWebResponse::de).toList();
    }

    @GET
    @Path("/{id}/arquivo")
    public Response arquivo(@PathParam("id") Long id) {
        var a = consultar.arquivo(id);
        return Response.ok(a.conteudo(), a.tipoDeConteudo())
                .header("Content-Disposition", "inline; filename=\"" + a.nome().replace("\"", "") + "\"").build();
    }

    @POST
    @Path("/{id}/conferencia")
    public DocumentoWebResponse conferir(@PathParam("id") Long id, ConferenciaRequest r) {
        return DocumentoWebResponse.de(conferir.executar(id, r.aceito(), r.motivo(), r.observacao()));
    }
}
