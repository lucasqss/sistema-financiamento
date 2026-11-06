package br.com.financiamento.documentos.dominio;

/**
 * Porta para guardar e ler os arquivos dos documentos (ADR 0010). O domínio só
 * conhece a chave; onde o arquivo mora (sistema de arquivos, armazenamento de
 * objetos) é do adaptador.
 */
public interface ArmazemDeArquivos {

    /** Guarda o conteúdo e devolve a chave para lê-lo depois. */
    String guardar(byte[] conteudo);

    byte[] ler(String chave);
}
