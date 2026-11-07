package br.com.financiamento.garantia.integracao;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/** O órgão de trânsito (ATR-0010), no formato dele. URL em quarkus.rest-client.orgao-de-transito.url. */
@RegisterRestClient(configKey = "orgao-de-transito")
interface ClienteDoOrgaoDeTransito {

    @POST
    @Path("/orgao-de-transito/gravames")
    void gravar(PedidoDeRegistro pedido);
}
