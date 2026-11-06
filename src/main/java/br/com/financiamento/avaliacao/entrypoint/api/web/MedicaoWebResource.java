package br.com.financiamento.avaliacao.entrypoint.api.web;

import br.com.financiamento.avaliacao.aplicacao.MedirObraService;
import br.com.financiamento.shared.seguranca.Papeis;
import jakarta.annotation.security.RolesAllowed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

import java.util.List;

import static br.com.financiamento.shared.validacao.atributo.ParametroDeConsulta.obrigatorio;

/** Canal web do engenheiro de medição (ATR-0006): UC-0013. */
@Tag(name = "Avaliação", description = "Avaliação do bem e medição da obra (DOM-0003).")
@Path("/web/medicoes")
public class MedicaoWebResource {

    @Inject MedicaoRequestValidador medicaoRequestValidador;

    @Inject MedirObraService medir;

    @POST
    @Operation(summary = "UC-0013 Medir obra")
    @RolesAllowed(Papeis.ENGENHEIRO)
    public MedicaoWebResponse medir(MedicaoRequest r) {
        medicaoRequestValidador.validar(r);
        return MedicaoWebResponse.de(medir.executar(r.contratoId(), r.percentualAcumulado(), r.medidaEm()));
    }

    @GET
    @Operation(summary = "Medições da obra de um contrato")
    @RolesAllowed({Papeis.ENGENHEIRO, Papeis.ANALISTA})
    public List<MedicaoWebResponse> doContrato(@Parameter(required = true) @QueryParam("contrato") Long contratoId) {
        return medir.doContrato(obrigatorio("contrato", contratoId)).stream().map(MedicaoWebResponse::de).toList();
    }
}
