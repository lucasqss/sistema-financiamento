package br.com.financiamento.desembolso.integracao;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/** O sistema do FGTS (ATR-0012), no formato dele. URL em quarkus.rest-client.fgts.url. */
@RegisterRestClient(configKey = "fgts")
interface ClienteDoFgts {

    @POST
    @Path("/fgts/saques")
    void sacar(PedidoDeSaque pedido);
}
