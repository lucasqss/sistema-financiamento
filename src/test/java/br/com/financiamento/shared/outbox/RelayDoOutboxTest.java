package br.com.financiamento.shared.outbox;

import br.com.financiamento.ExternosNoWireMock;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * O relay do outbox (ADR 0013) contra o bureau no WireMock: entrega em ordem, falha temporária com recuo
 * exponencial sem passar a frente, recusa que desiste, evento interno só marcado e nada reenviado.
 */
@QuarkusTest
@QuarkusTestResource(value = ExternosNoWireMock.class, restrictToAnnotatedClass = true)
class RelayDoOutboxTest {

    @Inject
    RelayDoOutbox relay;

    @Inject
    Outbox outbox;

    @Inject
    EntityManager em;

    final List<StubMapping> stubs = new ArrayList<>();

    @BeforeEach
    void outboxVazio() {
        QuarkusTransaction.requiringNew().run(() -> em.createNativeQuery(
                "update shared.outbox set publicado_em = now() where publicado_em is null and desistido_em is null")
                .executeUpdate());
    }

    @AfterEach
    void stubsOriginais() {
        stubs.forEach(ExternosNoWireMock.servidor()::removeStub);
        stubs.clear();
    }

    /** O bureau responde com o status dado e sem webhook (prioridade acima da massa de teste). */
    void bureauResponde(int status) {
        stubsOriginais();
        stubs.add(ExternosNoWireMock.servidor().stubFor(post(urlPathEqualTo("/bureau/consultas")).atPriority(0)
                .willReturn(aResponse().withStatus(status))));
    }

    long pedidoAoBureau(long analise) {
        QuarkusTransaction.requiringNew().run(() -> outbox.registrar("AnaliseDeCredito", analise, "AnaliseCreditoSolicitada",
                Map.of("propostaId", 1, "participantes", List.of("52998224725"), "valorFinanciado", 1000,
                        "prazoMeses", 12, "parcela", 100)));
        return ultimoId();
    }

    /** Um evento interno (sem externo), do agregado dado. */
    long eventoInterno(String agregado, long id, String evento) {
        QuarkusTransaction.requiringNew().run(() -> outbox.registrar(agregado, id, evento, Map.of()));
        return ultimoId();
    }

    long ultimoId() {
        return QuarkusTransaction.requiringNew().call(() ->
                ((Number) em.createNativeQuery("select max(id) from shared.outbox").getSingleResult()).longValue());
    }

    Object[] situacao(long id) {
        return QuarkusTransaction.requiringNew().call(() -> (Object[]) em.createNativeQuery(
                "select publicado_em, desistido_em, tentativas, proxima_tentativa, ultimo_erro from shared.outbox where id = ?1")
                .setParameter(1, id).getSingleResult());
    }

    int pedidosDoProtocolo(String protocolo) {
        return ExternosNoWireMock.servidor().findAll(postRequestedFor(urlPathEqualTo("/bureau/consultas"))
                .withRequestBody(matchingJsonPath("$.protocolo", equalTo(protocolo)))).size();
    }

    @Test
    void foraDoArRecuaENaoPassaAFrenteNoMesmoAgregado() {
        bureauResponde(503);
        long pedido = pedidoAoBureau(880001);
        long interno = eventoInterno("AnaliseDeCredito", 880001, "CreditoAprovado");
        long deOutro = eventoInterno("Proposta", 880001, "PropostaIniciada");

        assertEquals(1, relay.enviarPendentes(), "só o de outro agregado sai");
        var s = situacao(pedido);
        assertNull(s[0], "não entregue");
        assertEquals(1, ((Number) s[2]).intValue());
        assertNotNull(s[3], "próxima tentativa agendada");
        assertTrue(s[4].toString().contains("503"), s[4].toString());
        assertNull(situacao(interno)[0], "o seguinte do mesmo agregado espera: a ordem se preserva");
        assertNotNull(situacao(deOutro)[0], "outro agregado não fica preso ao externo fora do ar");

        // Antes do recuo vencer, nada é tentado de novo.
        assertEquals(0, relay.enviarPendentes());
        assertEquals(1, pedidosDoProtocolo("CRE-880001"));

        // O bureau volta e o recuo vence: entrega, e o seguinte vai junto.
        bureauResponde(202);
        QuarkusTransaction.requiringNew().run(() -> em.createNativeQuery(
                "update shared.outbox set proxima_tentativa = now() - interval '1 second' where id = ?1")
                .setParameter(1, pedido).executeUpdate());
        assertEquals(2, relay.enviarPendentes());
        assertNotNull(situacao(pedido)[0]);
        assertNotNull(situacao(interno)[0]);
        assertEquals(2, pedidosDoProtocolo("CRE-880001"));

        // Nada é reenviado.
        assertEquals(0, relay.enviarPendentes());
        assertEquals(2, pedidosDoProtocolo("CRE-880001"));
    }

    @Test
    void recusaDoExternoDesisteESegue() {
        bureauResponde(400);
        long pedido = pedidoAoBureau(880002);
        long interno = eventoInterno("AnaliseDeCredito", 880002, "CreditoAprovado");

        assertEquals(1, relay.enviarPendentes(), "desistido o pedido, o seguinte do agregado segue");
        var s = situacao(pedido);
        assertNull(s[0]);
        assertNotNull(s[1], "desistido");
        assertTrue(s[4].toString().contains("400"), s[4].toString());
        assertNotNull(situacao(interno)[0]);

        // As métricas mostram o desistido, para alguém tratar (alerta de outbox, ADR 0005).
        QuarkusTransaction.requiringNew().run(relay::medir);
        given().get("/q/metrics").then().statusCode(200)
                .body(containsString("financiamento_outbox_desistidos"))
                .body(containsString("financiamento_outbox_pendentes"))
                .body(containsString("financiamento_outbox_falhas_total"));
    }

    @Test
    void recuoExponencialAteQuinzeMinutos() {
        assertEquals(Duration.ofSeconds(2), RelayDoOutbox.recuo(1));
        assertEquals(Duration.ofSeconds(8), RelayDoOutbox.recuo(3));
        assertEquals(Duration.ofSeconds(512), RelayDoOutbox.recuo(9));
        assertEquals(Duration.ofMinutes(15), RelayDoOutbox.recuo(10));
        assertEquals(Duration.ofMinutes(15), RelayDoOutbox.recuo(50));
    }
}
