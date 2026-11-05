package br.com.financiamento.contratacao.dominio;

import br.com.financiamento.ModeloDeNegocio;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** As transições do EstadoContrato são as do diagrama CV-0002 do modelo aprovado. */
class CicloDeVidaDoContratoTest {

    @Test
    void transicoesSaoAsDoCV0002() {
        var modelo = ModeloDeNegocio.cicloDeVida("CV-0002");
        var implementadas = new TreeSet<String>();
        for (var de : EstadoContrato.values()) {
            de.transicoes().forEach((evento, destinos) ->
                    destinos.forEach(para -> implementadas.add(de + " --" + evento + "--> " + para)));
        }
        assertEquals(modelo.transicoes(), implementadas);
        assertEquals(modelo.inicial(), EstadoContrato.ESTADO_INICIAL + " por " + EstadoContrato.EVENTO_INICIAL);
        assertEquals(modelo.finais(), Arrays.stream(EstadoContrato.values()).filter(EstadoContrato::finalizado)
                .map(Enum::name).collect(Collectors.toCollection(TreeSet::new)));
    }
}
