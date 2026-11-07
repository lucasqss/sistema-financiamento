package br.com.financiamento.desembolso.integracao;

import br.com.financiamento.shared.outbox.Envio;
import br.com.financiamento.shared.outbox.EventoDoOutbox;
import br.com.financiamento.shared.outbox.RotaDeSaida;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * Camada anticorrupção do pagamento (UC-0021, UC-0023): cada liberação vira uma transferência ao vendedor,
 * com o protocolo nosso (LIB-&lt;contrato&gt;-&lt;n&gt;), que o pagamento devolve na confirmação.
 */
@ApplicationScoped
class PedidoDePagamentoAdapter implements RotaDeSaida {

    @RestClient
    ClienteDePagamento pagamento;

    @ConfigProperty(name = "financiamento.url-publica")
    String urlPublica;

    @Override
    public String externo() {
        return "pagamento";
    }

    @Override
    public boolean atende(EventoDoOutbox e) {
        return e.e("Desembolso", "LiberacaoSolicitada");
    }

    @Override
    public Envio enviar(EventoDoOutbox e) {
        var d = e.dados();
        var pedido = new PedidoDeTransferencia(d.path("protocolo").asText(),
                d.path("valor").decimalValue(), d.path("favorecido").asText(), urlPublica + "/retornos/pagamento");
        return Envio.porHttp(() -> pagamento.transferir(pedido));
    }
}
