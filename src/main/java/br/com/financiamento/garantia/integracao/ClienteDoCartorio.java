package br.com.financiamento.garantia.integracao;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/** O cartório de registro de imóveis (ATR-0009), no formato dele. URL em quarkus.rest-client.cartorio.url. */
@RegisterRestClient(configKey = "cartorio")
interface ClienteDoCartorio {

    @POST
    @Path("/cartorio/registros")
    void registrar(PedidoDeRegistro pedido);
}
