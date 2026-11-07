package br.com.financiamento.integracao;

import br.com.financiamento.shared.outbox.EventoDoOutbox;
import br.com.financiamento.shared.outbox.RotaDeSaida;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Todo pedido a um sistema externo do modelo tem exatamente uma rota de saída (ADR 0013): nenhum fica parado no
 * outbox, e nenhum vai duas vezes. Os demais eventos são internos e não têm rota.
 */
@QuarkusTest
class RotasDeSaidaTest {

    /** Agregado e evento de cada pedido a um externo, como o outbox os grava. */
    static final List<String[]> PEDIDOS_A_EXTERNOS = List.of(
            new String[]{"AnaliseDeCredito", "AnaliseCreditoSolicitada"},
            new String[]{"Avaliacao", "AvaliacaoSolicitada"},
            new String[]{"Contratacao", "CondicoesAceitas"},
            new String[]{"Garantia", "RegistroSolicitado"},
            new String[]{"Desembolso", "FgtsSolicitado"},
            new String[]{"Desembolso", "LiberacaoSolicitada"});

    @Inject
    @Any
    Instance<RotaDeSaida> rotas;

    static EventoDoOutbox evento(String agregado, String evento) {
        return new EventoDoOutbox(1L, agregado, "1", evento, "{}", Instant.now());
    }

    @Test
    void cadaPedidoTemUmaRota() {
        var erradas = new ArrayList<String>();
        for (var p : PEDIDOS_A_EXTERNOS) {
            long quantas = rotas.stream().filter(r -> r.atende(evento(p[0], p[1]))).count();
            if (quantas != 1) erradas.add(p[0] + "/" + p[1] + ": " + quantas + " rotas");
        }
        assertTrue(erradas.isEmpty(), erradas.toString());
        assertEquals(PEDIDOS_A_EXTERNOS.size(), rotas.stream().count(), "uma rota por pedido");
    }

    @Test
    void eventosInternosNaoTemRota() {
        for (var interno : List.of(new String[]{"Proposta", "PropostaAprovada"}, new String[]{"Contrato", "ContratoAssinado"},
                new String[]{"Garantia", "GarantiaRegistrada"}, new String[]{"MedicaoDaObra", "MedicaoRealizada"})) {
            assertTrue(rotas.stream().noneMatch(r -> r.atende(evento(interno[0], interno[1]))), interno[1]);
        }
    }
}
