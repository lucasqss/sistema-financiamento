package br.com.financiamento;

import io.restassured.filter.Filter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Grava cada chamada que os testes fazem à API (método, caminho e status) em
 * {@code target/cobertura-da-api/chamadas.csv}. O portão {@code CoberturaDosEndpointsIT} cruza o arquivo
 * com o contrato (docs/api/openapi.json) e exige, por operação, as respostas que ela pode dar.
 */
public final class RegistroDeChamadas {

    public static final Path ARQUIVO = Path.of("target", "cobertura-da-api", "chamadas.csv");

    /** O arquivo é zerado uma vez por JVM: cada execução da suíte começa do zero. */
    private static boolean zerado;

    public static final Filter FILTRO = (requisicao, resposta, contexto) -> {
        var r = contexto.next(requisicao, resposta);
        gravar(requisicao.getMethod(), URI.create(requisicao.getURI()).getPath(), r.getStatusCode());
        return r;
    };

    private RegistroDeChamadas() {
    }

    private static synchronized void gravar(String metodo, String caminho, int status) {
        try {
            if (!zerado) {
                Files.createDirectories(ARQUIVO.getParent());
                Files.deleteIfExists(ARQUIVO);
                zerado = true;
            }
            Files.writeString(ARQUIVO, metodo + ";" + caminho + ";" + status + "\n", StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
