package br.com.financiamento;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Leitura do modelo de negócio aprovado (docs/dominio/especificacao.md, gerado
 * pelo episteme). Os testes conferem o código contra ele: se o modelo mudar,
 * o código precisa acompanhar.
 */
public final class ModeloDeNegocio {

    public static final Path ESPECIFICACAO = Path.of("docs/dominio/especificacao.md");

    private ModeloDeNegocio() {
    }

    /** As linhas da seção de um artefato (ex.: DIM-0001), do título até o próximo título. */
    public static List<String> secao(String codigo) {
        List<String> linhas;
        try {
            linhas = Files.readAllLines(ESPECIFICACAO);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        var saida = new ArrayList<String>();
        boolean dentro = false;
        for (var linha : linhas) {
            if (linha.startsWith("#")) {
                if (dentro) break;
                dentro = linha.matches("#+ .*" + codigo + " - .*");
                continue;
            }
            if (dentro) saida.add(linha);
        }
        if (saida.isEmpty()) throw new IllegalStateException(codigo + " não encontrado em " + ESPECIFICACAO);
        return saida;
    }

    /** Os valores finais (folhas) da lista de uma dimensão. */
    public static List<String> folhasDaDimensao(String codigo) {
        var itens = new ArrayList<String[]>(); // {recuo, nome}
        for (var linha : secao(codigo)) {
            var m = java.util.regex.Pattern.compile("^(\\s*)[-*+]\\s+(.+?)\\s*$").matcher(linha);
            if (m.matches()) itens.add(new String[]{String.valueOf(m.group(1).length()), m.group(2)});
        }
        var folhas = new ArrayList<String>();
        for (int i = 0; i < itens.size(); i++) {
            int recuo = Integer.parseInt(itens.get(i)[0]);
            boolean temFilho = i + 1 < itens.size() && Integer.parseInt(itens.get(i + 1)[0]) > recuo;
            if (!temFilho) folhas.add(itens.get(i)[1]);
        }
        return folhas;
    }

    /** As folhas da dimensão abaixo de um valor (ele mesmo, se for folha), como "Veículo novo" ou "Terreno". */
    public static List<String> folhasAbaixoDe(String codigo, String valor) {
        var itens = new ArrayList<String[]>();
        for (var linha : secao(codigo)) {
            var m = java.util.regex.Pattern.compile("^(\\s*)[-*+]\\s+(.+?)\\s*$").matcher(linha);
            if (m.matches()) itens.add(new String[]{String.valueOf(m.group(1).length()), m.group(2)});
        }
        var folhas = folhasDaDimensao(codigo);
        var saida = new ArrayList<String>();
        for (int i = 0; i < itens.size(); i++) {
            if (!itens.get(i)[1].equalsIgnoreCase(valor)) continue;
            int recuo = Integer.parseInt(itens.get(i)[0]);
            if (folhas.contains(itens.get(i)[1])) saida.add(itens.get(i)[1]);
            for (int j = i + 1; j < itens.size() && Integer.parseInt(itens.get(j)[0]) > recuo; j++) {
                if (folhas.contains(itens.get(j)[1])) saida.add(itens.get(j)[1]);
            }
        }
        return saida;
    }

    /** Texto de uma célula sem a marcação de link: "[laudo](#glo-0017) de x" -> "laudo de x". */
    public static String semLinks(String texto) {
        return texto.replaceAll("\\[([^\\]]+)]\\([^)]*\\)", "$1").replaceAll("\\s*\\([^)]*\\)", "").trim();
    }

    /** A primeira tabela da seção: linha (1ª coluna) -> coluna (cabeçalho) -> valor. */
    public static Map<String, Map<String, String>> tabela(String codigo) {
        var linhas = secao(codigo).stream().filter(l -> l.trim().startsWith("|")).toList();
        var cabecalho = celulas(linhas.get(0));
        var tabela = new LinkedHashMap<String, Map<String, String>>();
        for (var linha : linhas.subList(2, linhas.size())) {
            var c = celulas(linha);
            var valores = new LinkedHashMap<String, String>();
            for (int i = 1; i < cabecalho.size(); i++) valores.put(cabecalho.get(i), c.get(i));
            tabela.put(c.get(0), valores);
        }
        return tabela;
    }

    private static List<String> celulas(String linha) {
        var corpo = linha.trim().replaceAll("^\\|", "").replaceAll("\\|$", "");
        return java.util.Arrays.stream(corpo.split("\\|")).map(String::trim).toList();
    }

    /** O bloco mermaid da seção (ex.: o stateDiagram de um ciclo de vida). */
    public static List<String> diagrama(String codigo) {
        var saida = new ArrayList<String>();
        boolean dentro = false;
        for (var linha : secao(codigo)) {
            if (linha.startsWith("```mermaid")) { dentro = true; continue; }
            if (dentro && linha.startsWith("```")) break;
            if (dentro) saida.add(linha.trim());
        }
        return saida;
    }
}
