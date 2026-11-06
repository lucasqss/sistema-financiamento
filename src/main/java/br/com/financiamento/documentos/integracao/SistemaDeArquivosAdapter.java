package br.com.financiamento.documentos.integracao;

import br.com.financiamento.documentos.dominio.ArmazemDeArquivos;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Armazém de arquivos no sistema de arquivos (ADR 0010): para dev, teste e um MVP
 * de uma instância. Em produção com várias instâncias, um adaptador de
 * armazenamento de objetos implementa a mesma porta.
 */
@ApplicationScoped
class SistemaDeArquivosAdapter implements ArmazemDeArquivos {

    @ConfigProperty(name = "documentos.armazem.diretorio")
    Path diretorio;

    @Override
    public String guardar(byte[] conteudo) {
        var chave = UUID.randomUUID().toString();
        try {
            Files.createDirectories(diretorio);
            Files.write(diretorio.resolve(chave), conteudo);
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível guardar o arquivo", e);
        }
        return chave;
    }

    @Override
    public byte[] ler(String chave) {
        if (!chave.matches("[0-9a-f-]{36}")) throw new IllegalArgumentException("Chave inválida: " + chave);
        try {
            return Files.readAllBytes(diretorio.resolve(chave));
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível ler o arquivo " + chave, e);
        }
    }
}
