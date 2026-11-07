package br.com.financiamento.avaliacao.integracao;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/** O avaliador (ATR-0007), no formato dele. A URL vem de quarkus.rest-client.avaliador.url. */
@RegisterRestClient(configKey = "avaliador")
interface ClienteDoAvaliador {

    @POST
    @Path("/avaliador/laudos")
    void pedirLaudo(PedidoDeLaudo pedido);
}
