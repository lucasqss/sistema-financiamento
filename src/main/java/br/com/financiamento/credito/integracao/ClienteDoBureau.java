package br.com.financiamento.credito.integracao;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/** O bureau de crédito (ATR-0008), no formato dele. A URL vem de quarkus.rest-client.bureau.url. */
@RegisterRestClient(configKey = "bureau")
interface ClienteDoBureau {

    @POST
    @Path("/bureau/consultas")
    void consultar(PedidoDeConsulta pedido);
}
