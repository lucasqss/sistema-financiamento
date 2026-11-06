package br.com.financiamento.arquitetura;

import br.com.financiamento.ContratoPublicado;
import br.com.financiamento.RegistroDeChamadas;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Portão de cobertura dos endpoints: roda depois de todos os testes (failsafe, fase verify) e cruza as
 * chamadas que eles fizeram ({@link RegistroDeChamadas}) com o contrato publicado (docs/api/openapi.json).
 * Toda operação precisa ter sido testada em cada resposta que pode dar:
 * <ul>
 *   <li>2xx, 401 (sem token) e 403 (sem papel) em todas;</li>
 *   <li>400 nas que recebem corpo ou parâmetro de consulta obrigatório;</li>
 *   <li>404 nas que apontam um recurso pelo caminho ({id}) ou pelo protocolo no corpo;</li>
 *   <li>422 em todo POST e PUT, salvo as exceções justificadas abaixo.</li>
 * </ul>
 * Operação nova sem teste derruba a build.
 */
class CoberturaDosEndpointsIT {

    /** Operações que apontam o recurso por um protocolo nosso no corpo: o protocolo desconhecido é 404. */
    static final Set<String> RECURSO_PELO_CORPO = Set.of(
            "POST /retornos/orgao-de-registro",
            "POST /retornos/pagamento");

    /** POST/PUT que não têm regra de negócio que recuse a chamada (não há 422 possível), e por quê. */
    static final Map<String, String> SEM_422 = Map.of(
            "POST /retornos/orgao-de-registro",
            "RN-0029: o retorno do órgão nunca é recusado; é classificado como fato (constituída, pendência, repetido…).",
            "POST /retornos/fgts/{desembolsoId}",
            "Retorno idempotente: a resposta do FGTS repetida ou fora de hora não tem efeito, e não há regra que a recuse.");

    @Test
    void todaOperacaoTestadaEmCadaRespostaQuePodeDar() throws Exception {
        assertTrue(Files.exists(RegistroDeChamadas.ARQUIVO),
                "Sem " + RegistroDeChamadas.ARQUIVO + ": a suíte de testes não rodou antes do portão.");
        var operacoes = ContratoPublicado.operacoes();
        var vistas = new java.util.HashMap<String, Set<Integer>>();
        for (var linha : Files.readAllLines(RegistroDeChamadas.ARQUIVO)) {
            var p = linha.split(";");
            for (var op : operacoes) {
                if (op.atende(p[0], p[1])) {
                    vistas.computeIfAbsent(op.nome(), k -> new TreeSet<>()).add(Integer.parseInt(p[2]));
                }
            }
        }

        var faltas = new ArrayList<String>();
        for (var op : operacoes) {
            var status = vistas.getOrDefault(op.nome(), Set.of());
            var falta = new ArrayList<String>();
            if (status.stream().noneMatch(s -> s >= 200 && s < 300)) falta.add("2xx");
            if (!status.contains(401)) falta.add("401");
            if (!status.contains(403)) falta.add("403");
            if ((op.temCorpo() || !op.consultasObrigatorias().isEmpty()) && !status.contains(400)) falta.add("400");
            if ((op.temIdNoCaminho() || RECURSO_PELO_CORPO.contains(op.nome())) && !status.contains(404)) falta.add("404");
            if (Set.of("POST", "PUT").contains(op.metodo()) && !SEM_422.containsKey(op.nome()) && !status.contains(422)) {
                falta.add("422");
            }
            if (!falta.isEmpty()) faltas.add(op.nome() + " sem teste de " + falta + " (testados: " + status + ")");
        }
        assertTrue(faltas.isEmpty(), "Operações sem cobertura adequada (" + faltas.size() + "):\n  "
                + String.join("\n  ", faltas));
        assertTrue(operacoes.size() > 30, "contrato lido: " + operacoes.size() + " operações");
    }

}
