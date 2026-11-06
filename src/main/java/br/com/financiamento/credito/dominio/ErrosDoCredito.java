package br.com.financiamento.credito.dominio;

import br.com.financiamento.shared.validacao.ErroCatalogado;
import br.com.financiamento.shared.validacao.TipoDeErro;

/**
 * Catálogo de erros de domínio do módulo: Crédito (DOM-0002) (ADR 0011). O código é estável:
 * não reaproveite nem renumere; acrescente no fim. A regra é a do modelo de negócio.
 */
public enum ErrosDoCredito implements ErroCatalogado {
    ANALISE_NAO_ENCONTRADA("CRE-0001", null, 404, "Análise de crédito {0} não encontrada."),
    ANALISE_JA_DECIDIDA("CRE-0002", "UC-0011", 422, "A análise {0} já foi decidida como {1}."),
    RESSALVA_INCOMPLETA("CRE-0003", "RN-0020", 422, "Aprovação com ressalva precisa da ressalva e das condições sugeridas.");

    private final String codigo;
    private final String regra;
    private final int status;
    private final String modelo;

    ErrosDoCredito(String codigo, String regra, int status, String modelo) {
        this.codigo = codigo;
        this.regra = regra;
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
    public String regraDoModelo() {
        return regra;
    }

    @Override
    public int status() {
        return status;
    }
}
