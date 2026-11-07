package br.com.financiamento.garantia.integracao;

import br.com.financiamento.shared.outbox.Envio;
import br.com.financiamento.shared.outbox.EventoDoOutbox;
import br.com.financiamento.shared.outbox.RotaDeSaida;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * Camada anticorrupção do registro da garantia (UC-0018, RN-0028): o pedido vai ao órgão que o plug-in do
 * tipo de bem escolheu, cartório (imóvel e terreno) ou órgão de trânsito (veículo), com o protocolo nosso
 * (GAR-&lt;contrato&gt;-&lt;tentativa&gt;).
 */
@ApplicationScoped
class PedidoDeRegistroAdapter implements RotaDeSaida {

    static final String CARTORIO = "cartório de registro de imóveis";

    @RestClient
    ClienteDoCartorio cartorio;

    @RestClient
    ClienteDoOrgaoDeTransito orgaoDeTransito;

    @ConfigProperty(name = "financiamento.url-publica")
    String urlPublica;

    @Override
    public String externo() {
        return "registro";
    }

    @Override
    public boolean atende(EventoDoOutbox e) {
        return e.e("Garantia", "RegistroSolicitado");
    }

    @Override
    public Envio enviar(EventoDoOutbox e) {
        var d = e.dados();
        var pedido = new PedidoDeRegistro(d.path("protocolo").asText(), d.path("identificacaoDoBem").asText(),
                d.path("tentativa").asInt(), urlPublica + "/retornos/orgao-de-registro");
        return CARTORIO.equals(d.path("orgao").asText())
                ? Envio.porHttp(() -> cartorio.registrar(pedido))
                : Envio.porHttp(() -> orgaoDeTransito.gravar(pedido));
    }
}
