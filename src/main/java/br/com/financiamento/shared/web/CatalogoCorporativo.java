package br.com.financiamento.shared.web;

import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Properties;

/**
 * O mapeamento do nosso código de erro para o código do catálogo corporativo
 * (ADR 0011), num lugar só: {@code catalogo-corporativo.properties}. Sem
 * mapeamento, o código corporativo fica vazio.
 */
@ApplicationScoped
class CatalogoCorporativo {

    private final Properties mapa = new Properties();

    CatalogoCorporativo() {
        try (var in = CatalogoCorporativo.class.getResourceAsStream("/catalogo-corporativo.properties")) {
            if (in != null) mapa.load(new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    String codigoCorporativo(String nosso) {
        return mapa.getProperty(nosso);
    }
}
