package br.com.financiamento.contratacao.integracao;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/** A seguradora (ATR-0011), no formato dela. A URL vem de quarkus.rest-client.seguradora.url. */
@RegisterRestClient(configKey = "seguradora")
interface ClienteDaSeguradora {

    @POST
    @Path("/seguradora/apolices")
    void pedirApolice(PedidoDeApolice pedido);
}
