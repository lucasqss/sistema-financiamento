package br.com.financiamento.avaliacao.entrypoint.api.web;

import br.com.financiamento.avaliacao.aplicacao.MedirObraService;
import br.com.financiamento.shared.seguranca.Papeis;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

import java.util.List;

/** Canal web do engenheiro de medição (ATR-0006): UC-0013. */
@Path("/web/medicoes")
public class MedicaoWebResource {

    @Inject MedicaoRequestValidador medicaoRequestValidador;

    @Inject MedirObraService medir;

    @POST
    @RolesAllowed(Papeis.ENGENHEIRO)
    public MedicaoWebResponse medir(MedicaoRequest r) {
        medicaoRequestValidador.validar(r);
        return MedicaoWebResponse.de(medir.executar(r.contratoId(), r.percentualAcumulado(), r.medidaEm()));
    }

    @GET
    @RolesAllowed({Papeis.ENGENHEIRO, Papeis.ANALISTA})
    public List<MedicaoWebResponse> doContrato(@QueryParam("contrato") Long contratoId) {
        return medir.doContrato(contratoId).stream().map(MedicaoWebResponse::de).toList();
    }
}
