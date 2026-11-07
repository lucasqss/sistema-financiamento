package br.com.financiamento;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import java.util.Map;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

/**
 * Os 7 sistemas externos simulados no WireMock, embutido no teste, com os MESMOS stubs do ambiente local
 * (docker/wiremock): cada externo recebe o pedido e devolve o resultado por webhook nos /retornos da aplicação,
 * com o token do sistema assinado pelo emissor de teste.
 */
public class ExternosNoWireMock implements QuarkusTestResourceLifecycleManager {

    static final String[] SISTEMAS = {"sistema-bureau", "sistema-avaliador", "sistema-seguradora", "sistema-registro",
            "sistema-fgts", "sistema-pagamento"};

    private static WireMockServer servidor;

    /** O WireMock em execução, para conferir os pedidos recebidos e trocar stubs num teste. */
    public static WireMockServer servidor() {
        return servidor;
    }

    @Override
    public Map<String, String> start() {
        for (var s : SISTEMAS) System.setProperty("token." + s, TokensParaTeste.sistema(s));
        servidor = new WireMockServer(options().dynamicPort().usingFilesUnderDirectory("docker/wiremock")
                .globalTemplating(true).withPermittedSystemKeys("token.*"));
        servidor.start();
        return Map.of(
                "externos.url", servidor.baseUrl(),
                "financiamento.url-publica", "http://localhost:8081");
    }

    @Override
    public void stop() {
        if (servidor != null) servidor.stop();
    }
}
