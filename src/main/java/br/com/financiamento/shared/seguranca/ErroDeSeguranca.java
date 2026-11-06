package br.com.financiamento.shared.seguranca;

import br.com.financiamento.shared.validacao.ErroCatalogado;
import br.com.financiamento.shared.validacao.TipoDeErro;

/** Os erros de acesso, no catálogo (ADR 0011, ADR 0012). */
public enum ErroDeSeguranca implements ErroCatalogado {
    NAO_AUTENTICADO("SEG-0001", 401, "Autenticação necessária: envie um token válido do emissor."),
    SEM_PAPEL("SEG-0002", 403, "Seu papel não permite esta operação."),
    NAO_PARTICIPA("SEG-0003", 403, "Você não participa deste {0}."),
    ASSINATURA_DE_OUTRO("SEG-0004", 403, "Cada signatário assina por si: o documento informado não é o seu.");

    private final String codigo;
    private final int status;
    private final String modelo;

    ErroDeSeguranca(String codigo, int status, String modelo) {
        this.codigo = codigo;
        this.status = status;
        this.modelo = modelo;
    }

    @Override
    public String codigo() {
        return codigo;
    }

    @Override
    public TipoDeErro tipo() {
        return TipoDeErro.DOMINIO;
    }

    @Override
    public String modelo() {
        return modelo;
    }

    @Override
    public int status() {
        return status;
    }
}
