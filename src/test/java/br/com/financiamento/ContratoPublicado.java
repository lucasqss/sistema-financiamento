package br.com.financiamento;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * As operações do contrato publicado (docs/api/openapi.json), para os testes que percorrem a API inteira:
 * uma operação nova entra neles sozinha.
 */
public final class ContratoPublicado {

    public static final Path ARQUIVO = Path.of("docs", "api", "openapi.json");

    public record Operacao(String metodo, String caminho, List<String> papeis, boolean temCorpo, boolean multipart,
                           List<String> consultasObrigatorias, boolean temIdNoCaminho) {

        public String nome() {
            return metodo + " " + caminho;
        }

        /** O caminho com cada parâmetro trocado pelo valor dado. */
        public String caminhoCom(Object id) {
            return caminho.replaceAll("\\{[^}]+}", String.valueOf(id));
        }

        public boolean atende(String metodo, String caminhoChamado) {
            return this.metodo.equals(metodo)
                    && Pattern.matches(caminho.replaceAll("\\{[^}]+}", "[^/]+"), caminhoChamado);
        }

        @Override
        public String toString() {
            return nome();
        }
    }

    private ContratoPublicado() {
    }

    public static List<Operacao> operacoes() {
        JsonNode raiz;
        try {
            raiz = new ObjectMapper().readTree(ARQUIVO.toFile());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        var lista = new ArrayList<Operacao>();
        raiz.get("paths").properties().forEach(caminho -> caminho.getValue().properties().forEach(m -> {
            var metodo = m.getKey().toUpperCase();
            if (!Set.of("GET", "POST", "PUT", "DELETE", "PATCH").contains(metodo)) return;
            var op = m.getValue();
            var consultas = new ArrayList<String>();
            boolean idNoCaminho = false;
            for (var p : op.path("parameters")) {
                if (p.path("in").asText().equals("query") && p.path("required").asBoolean()) consultas.add(p.path("name").asText());
                if (p.path("in").asText().equals("path")) idNoCaminho = true;
            }
            var papeis = new ArrayList<String>();
            for (var s : op.path("security")) s.path("jwt").forEach(p -> papeis.add(p.asText()));
            lista.add(new Operacao(metodo, caminho.getKey(), List.copyOf(papeis), op.has("requestBody"),
                    op.path("requestBody").path("content").has("multipart/form-data"), List.copyOf(consultas), idNoCaminho));
        }));
        return lista;
    }
}
