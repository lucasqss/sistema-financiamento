package br.com.financiamento.credito.integracao;

import br.com.financiamento.shared.outbox.Envio;
import br.com.financiamento.shared.outbox.EventoDoOutbox;
import br.com.financiamento.shared.outbox.RotaDeSaida;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.ArrayList;

/**
 * Camada anticorrupção do bureau (UC-0011): o pedido de análise vira uma consulta no formato do bureau,
 * com o protocolo nosso (CRE-&lt;análise&gt;) e o endereço onde ele devolve o resultado.
 */
@ApplicationScoped
class PedidoAoBureauAdapter implements RotaDeSaida {

    @RestClient
    ClienteDoBureau bureau;

    @ConfigProperty(name = "financiamento.url-publica")
    String urlPublica;

    @Override
    public String externo() {
        return "bureau";
    }

    @Override
    public boolean atende(EventoDoOutbox e) {
        return e.e("AnaliseDeCredito", "AnaliseCreditoSolicitada");
    }

    @Override
    public Envio enviar(EventoDoOutbox e) {
        var d = e.dados();
        var documentos = new ArrayList<String>();
        d.path("participantes").forEach(p -> documentos.add(p.asText()));
        var pedido = new PedidoDeConsulta("CRE-" + e.agregadoId(), documentos,
                d.path("valorFinanciado").decimalValue(), d.path("prazoMeses").asInt(), d.path("parcela").decimalValue(),
                urlPublica + "/retornos/bureau-de-credito/" + e.agregadoId());
        return Envio.porHttp(() -> bureau.consultar(pedido));
    }
}
