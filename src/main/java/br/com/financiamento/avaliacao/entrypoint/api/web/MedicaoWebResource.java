package br.com.financiamento.avaliacao.entrypoint.api.web;

import br.com.financiamento.avaliacao.aplicacao.MedirObraService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

import java.util.List;

/** Canal web do engenheiro de medição (ATR-0006): UC-0013. */
@Path("/web/medicoes")
public class MedicaoWebResource {

    @Inject MedirObraService medir;

    @POST
    public MedicaoWebResponse medir(MedicaoRequest r) {
        return MedicaoWebResponse.de(medir.executar(r.contratoId(), r.percentualAcumulado(), r.medidaEm()));
    }

    @GET
    public List<MedicaoWebResponse> doContrato(@QueryParam("contrato") Long contratoId) {
        return medir.doContrato(contratoId).stream().map(MedicaoWebResponse::de).toList();
    }
}
