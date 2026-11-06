package br.com.financiamento.garantia.entrypoint.api.web;

import br.com.financiamento.garantia.aplicacao.ConsultarRegistrosService;
import br.com.financiamento.garantia.aplicacao.TratarRegistroService;
import br.com.financiamento.shared.validacao.ErroDeDominioException;
import br.com.financiamento.garantia.dominio.ErrosDaGarantia;
import br.com.financiamento.shared.seguranca.Papeis;
import jakarta.annotation.security.RolesAllowed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

import static br.com.financiamento.shared.validacao.atributo.ParametroDeConsulta.obrigatorio;

/** Canal web do analista (ATR-0005) para o registro da garantia. */
@Tag(name = "Garantia", description = "Registro da alienação fiduciária (DOM-0005).")
@Path("/web/registros")
public class RegistroWebResource {

    @Inject DesfazimentoRequestValidador desfazimentoRequestValidador;

    @Inject ConsultarRegistrosService consultar;
    @Inject TratarRegistroService tratar;

    @GET
    @Operation(summary = "Registro da garantia de um contrato")
    @RolesAllowed(Papeis.ANALISTA)
    public RegistroWebResponse doContrato(@Parameter(required = true) @QueryParam("contrato") Long contratoId) {
        return consultar.doContrato(obrigatorio("contrato", contratoId)).map(RegistroWebResponse::de)
                .orElseThrow(() -> new ErroDeDominioException(ErrosDaGarantia.CONTRATO_SEM_REGISTRO, contratoId));
    }

    @POST
    @Path("/{id}/exigencia-cumprida")
    @Operation(summary = "UC-0020 Cumprir exigência")
    @RolesAllowed(Papeis.ANALISTA)
    public RegistroWebResponse cumprirExigencia(@PathParam("id") Long id) {
        return RegistroWebResponse.de(tratar.cumprirExigencia(id));
    }

    @POST
    @Path("/{id}/reenvio")
    @Operation(summary = "UC-0019 Reenviar registro")
    @RolesAllowed(Papeis.ANALISTA)
    public RegistroWebResponse reenviar(@PathParam("id") Long id) {
        return RegistroWebResponse.de(tratar.reenviar(id));
    }

    @POST
    @Path("/{id}/desfazimento")
    @Operation(summary = "UC-0019 Desfazer o contrato sem solução")
    @RolesAllowed(Papeis.ANALISTA)
    public RegistroWebResponse desfazer(@PathParam("id") Long id, DesfazimentoRequest r) {
        desfazimentoRequestValidador.validar(r);
        return RegistroWebResponse.de(tratar.desfazer(id, r == null ? null : r.motivo()));
    }
}
