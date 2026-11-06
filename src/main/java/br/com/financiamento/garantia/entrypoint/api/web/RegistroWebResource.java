package br.com.financiamento.garantia.entrypoint.api.web;

import br.com.financiamento.garantia.aplicacao.ConsultarRegistrosService;
import br.com.financiamento.garantia.aplicacao.TratarRegistroService;
import br.com.financiamento.shared.validacao.ErroDeDominioException;
import br.com.financiamento.garantia.dominio.ErrosDaGarantia;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

/** Canal web do analista (ATR-0005) para o registro da garantia. */
@Path("/web/registros")
public class RegistroWebResource {

    @Inject DesfazimentoRequestValidador desfazimentoRequestValidador;

    @Inject ConsultarRegistrosService consultar;
    @Inject TratarRegistroService tratar;

    @GET
    public RegistroWebResponse doContrato(@QueryParam("contrato") Long contratoId) {
        return consultar.doContrato(contratoId).map(RegistroWebResponse::de)
                .orElseThrow(() -> new ErroDeDominioException(ErrosDaGarantia.CONTRATO_SEM_REGISTRO, contratoId));
    }

    @POST
    @Path("/{id}/exigencia-cumprida")
    public RegistroWebResponse cumprirExigencia(@PathParam("id") Long id) {
        return RegistroWebResponse.de(tratar.cumprirExigencia(id));
    }

    @POST
    @Path("/{id}/reenvio")
    public RegistroWebResponse reenviar(@PathParam("id") Long id) {
        return RegistroWebResponse.de(tratar.reenviar(id));
    }

    @POST
    @Path("/{id}/desfazimento")
    public RegistroWebResponse desfazer(@PathParam("id") Long id, DesfazimentoRequest r) {
        desfazimentoRequestValidador.validar(r);
        return RegistroWebResponse.de(tratar.desfazer(id, r == null ? null : r.motivo()));
    }
}
