package br.com.financiamento.bem;

import java.text.Normalizer;
import java.util.Locale;

/**
 * Um valor final da dimensão "Tipo de bem" (DIM-0001): combinação de categoria,
 * subtipo e condição, como "Carro usado" ou "Terreno rural".
 *
 * O núcleo não enumera os tipos: quem os conhece são os plug-ins de categoria.
 * O código é o nome do modelo em maiúsculas, sem acento: "Carro usado" -> CARRO_USADO.
 */
public record TipoDeBem(String codigo) {

    public TipoDeBem {
        if (codigo == null || !codigo.matches("[A-Z][A-Z_]*[A-Z]")) {
            throw new IllegalArgumentException("Código de tipo de bem inválido: " + codigo);
        }
    }

    public static TipoDeBem de(String codigo) {
        return new TipoDeBem(codigo == null ? null : codigo.trim().toUpperCase(Locale.ROOT));
    }

    /** O tipo correspondente a um nome do modelo de negócio ("Moto nova"). */
    public static TipoDeBem doNome(String nome) {
        var semAcento = Normalizer.normalize(nome, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return new TipoDeBem(semAcento.trim().replaceAll("[^A-Za-z]+", "_").toUpperCase(Locale.ROOT));
    }

    @Override
    public String toString() {
        return codigo;
    }
}
