package br.com.financiamento.contratacao.integracao;

import br.com.financiamento.shared.outbox.Envio;
import br.com.financiamento.shared.outbox.EventoDoOutbox;
import br.com.financiamento.shared.outbox.RotaDeSaida;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * Camada anticorrupção da seguradora (UC-0014, RN-0025): as condições aceitas viram um pedido de apólice por
 * seguro escolhido. Se um pedido falha, todos são refeitos na nova tentativa; a seguradora devolve a mesma
 * apólice, e o retorno repetido não tem efeito.
 */
@ApplicationScoped
class PedidoASeguradoraAdapter implements RotaDeSaida {

    @RestClient
    ClienteDaSeguradora seguradora;

    @ConfigProperty(name = "financiamento.url-publica")
    String urlPublica;

    @Override
    public String externo() {
        return "seguradora";
    }

    @Override
    public boolean atende(EventoDoOutbox e) {
        return e.e("Contratacao", "CondicoesAceitas");
    }

    @Override
    public Envio enviar(EventoDoOutbox e) {
        var d = e.dados();
        var retorno = urlPublica + "/retornos/seguradora/" + e.agregadoId();
        for (var seguro : d.path("seguros")) {
            var pedido = new PedidoDeApolice(e.agregadoId(), d.path("propostaId").asLong(),
                    seguro.asText(), retorno);
            var envio = Envio.porHttp(() -> seguradora.pedirApolice(pedido));
            if (envio.resultado() != Envio.Resultado.ENTREGUE) return envio;
        }
        return Envio.entregue();
    }
}
