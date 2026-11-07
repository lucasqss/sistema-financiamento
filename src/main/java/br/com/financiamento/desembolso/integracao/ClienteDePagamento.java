package br.com.financiamento.desembolso.integracao;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/** O sistema de pagamento, que transfere o dinheiro ao vendedor. URL em quarkus.rest-client.pagamento.url. */
@RegisterRestClient(configKey = "pagamento")
interface ClienteDePagamento {

    @POST
    @Path("/pagamento/transferencias")
    void transferir(PedidoDeTransferencia pedido);
}
