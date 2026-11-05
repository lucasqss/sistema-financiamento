package br.com.financiamento.originacao.dominio;

import br.com.financiamento.ModeloDeNegocio;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * O ciclo de vida implementado é o do modelo aprovado: as transições do
 * EstadoProposta são as do diagrama CV-0001, nem mais nem menos.
 */
class CicloDeVidaDaPropostaTest {

    @Test
    void transicoesSaoAsDoCV0001() {
        var modelo = ModeloDeNegocio.cicloDeVida("CV-0001");
        var implementadas = new TreeSet<String>();
        for (var de : EstadoProposta.values()) {
            de.transicoes().forEach((evento, para) -> implementadas.add(de + " --" + evento + "--> " + para));
        }
        assertEquals(modelo.transicoes(), implementadas);
        assertEquals(modelo.inicial(), EstadoProposta.ESTADO_INICIAL + " por " + EstadoProposta.EVENTO_INICIAL);
        assertEquals(modelo.finais(), Arrays.stream(EstadoProposta.values()).filter(EstadoProposta::finalizado)
                .map(Enum::name).collect(Collectors.toCollection(TreeSet::new)));
    }

    @Test
    void nomesDosEventosSaoOsDoModelo() {
        assertEquals("PropostaComPendencia", EventoDaProposta.PROPOSTA_COM_PENDENCIA.nomeNoModelo());
    }
}
