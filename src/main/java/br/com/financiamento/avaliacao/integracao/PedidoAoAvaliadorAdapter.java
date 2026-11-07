package br.com.financiamento.avaliacao.integracao;

import br.com.financiamento.shared.outbox.Envio;
import br.com.financiamento.shared.outbox.EventoDoOutbox;
import br.com.financiamento.shared.outbox.RotaDeSaida;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/** Camada anticorrupção do avaliador (UC-0012): o pedido de avaliação vira um pedido de laudo (AVA-&lt;avaliação&gt;). */
@ApplicationScoped
class PedidoAoAvaliadorAdapter implements RotaDeSaida {

    @RestClient
    ClienteDoAvaliador avaliador;

    @ConfigProperty(name = "financiamento.url-publica")
    String urlPublica;

    @Override
    public String externo() {
        return "avaliador";
    }

    @Override
    public boolean atende(EventoDoOutbox e) {
        return e.e("Avaliacao", "AvaliacaoSolicitada");
    }

    @Override
    public Envio enviar(EventoDoOutbox e) {
        var d = e.dados();
        var pedido = new PedidoDeLaudo("AVA-" + e.agregadoId(), d.path("tipoDeBem").asText(),
                d.path("identificacaoDoBem").asText(), d.path("valorDeCompra").decimalValue(), d.path("fonte").asText(),
                urlPublica + "/retornos/avaliador/" + e.agregadoId());
        return Envio.porHttp(() -> avaliador.pedirLaudo(pedido));
    }
}
