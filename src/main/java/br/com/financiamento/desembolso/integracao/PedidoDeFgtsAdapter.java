package br.com.financiamento.desembolso.integracao;

import br.com.financiamento.shared.outbox.Envio;
import br.com.financiamento.shared.outbox.EventoDoOutbox;
import br.com.financiamento.shared.outbox.RotaDeSaida;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/** Camada anticorrupção do FGTS (UC-0022): o FGTS da entrada vira um pedido de saque a favor do vendedor. */
@ApplicationScoped
class PedidoDeFgtsAdapter implements RotaDeSaida {

    @RestClient
    ClienteDoFgts fgts;

    @ConfigProperty(name = "financiamento.url-publica")
    String urlPublica;

    @Override
    public String externo() {
        return "fgts";
    }

    @Override
    public boolean atende(EventoDoOutbox e) {
        return e.e("Desembolso", "FgtsSolicitado");
    }

    @Override
    public Envio enviar(EventoDoOutbox e) {
        var d = e.dados();
        var pedido = new PedidoDeSaque(e.agregadoId(), d.path("valor").decimalValue(),
                d.path("favorecido").asText(), urlPublica + "/retornos/fgts/" + e.agregadoId());
        return Envio.porHttp(() -> fgts.sacar(pedido));
    }
}
