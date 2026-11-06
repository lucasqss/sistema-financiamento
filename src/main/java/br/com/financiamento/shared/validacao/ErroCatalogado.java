package br.com.financiamento.shared.validacao;

import java.text.MessageFormat;
import java.util.Arrays;

/**
 * Uma entrada do catálogo de erros (ADR 0011): código estável, tipo, status HTTP,
 * mensagem com parâmetros e, quando há, a regra do modelo de negócio que a origina.
 * O código é o que se mapeia para o catálogo corporativo.
 */
public interface ErroCatalogado {

    /** Estável e único: prefixo do módulo e número (ORI-0001, ATR-0003). */
    String codigo();

    TipoDeErro tipo();

    /** Modelo com parâmetros {0}, {1}…; os parâmetros entram como texto, sem formatação de número. */
    String modelo();

    /** A regra do modelo de negócio (RN-0009, CV-0001, AGR-0001…), ou null. */
    default String regraDoModelo() {
        return null;
    }

    /** 400 para atributo, 422 para domínio; "não encontrado" sobrescreve com 404. */
    default int status() {
        return tipo() == TipoDeErro.ATRIBUTO ? 400 : 422;
    }

    default String mensagem(Object... parametros) {
        var textos = Arrays.stream(parametros).map(String::valueOf).toArray();
        return new MessageFormat(modelo().replace("'", "''")).format(textos);
    }
}
