package br.com.financiamento.originacao.dominio;

import br.com.financiamento.ModeloDeNegocio;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * O ciclo de vida implementado é o do modelo aprovado: as transições do
 * EstadoProposta são as do diagrama CV-0001, nem mais nem menos.
 */
class CicloDeVidaDaPropostaTest {

    static final Pattern TRANSICAO = Pattern.compile("^(\\[\\*]|\\w+)\\s*-->\\s*(\\[\\*]|\\w+)(?:\\s*:\\s*(\\w+).*)?$");

    @Test
    void transicoesSaoAsDoCV0001() {
        var doModelo = new HashSet<String>();
        String inicial = null;
        var finais = new HashSet<String>();
        for (var linha : ModeloDeNegocio.diagrama("CV-0001")) {
            var m = TRANSICAO.matcher(linha);
            if (!m.matches()) continue;
            if (m.group(1).equals("[*]")) inicial = constante(m.group(2)) + " por " + constante(m.group(3));
            else if (m.group(2).equals("[*]")) finais.add(constante(m.group(1)));
            else doModelo.add(constante(m.group(1)) + " --" + constante(m.group(3)) + "--> " + constante(m.group(2)));
        }

        Set<String> implementadas = new HashSet<>();
        for (var de : EstadoProposta.values()) {
            de.transicoes().forEach((evento, para) -> implementadas.add(de + " --" + evento + "--> " + para));
        }
        assertEquals(new java.util.TreeSet<>(doModelo), new java.util.TreeSet<>(implementadas));
        assertEquals(inicial, EstadoProposta.ESTADO_INICIAL + " por " + EstadoProposta.EVENTO_INICIAL);
        assertEquals(finais, Arrays.stream(EstadoProposta.values()).filter(EstadoProposta::finalizado)
                .map(Enum::name).collect(Collectors.toSet()));
    }

    @Test
    void nomesDosEventosSaoOsDoModelo() {
        assertEquals("PropostaComPendencia", EventoDaProposta.PROPOSTA_COM_PENDENCIA.nomeNoModelo());
    }

    /** "EmAnalise" -> EM_ANALISE. */
    static String constante(String nome) {
        return nome.replaceAll("([a-z])([A-Z])", "$1_$2").toUpperCase();
    }
}
