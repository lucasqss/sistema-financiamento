package br.com.financiamento.avaliacao.dominio;

import br.com.financiamento.shared.validacao.ErroCatalogado;
import br.com.financiamento.shared.validacao.TipoDeErro;

/**
 * Catálogo de erros de domínio do módulo: Avaliação (DOM-0003) (ADR 0011). O código é estável:
 * não reaproveite nem renumere; acrescente no fim. A regra é a do modelo de negócio.
 */
public enum ErrosDaAvaliacao implements ErroCatalogado {
    AVALIACAO_NAO_ENCONTRADA("AVA-0001", null, 404, "Avaliação {0} não encontrada."),
    VALOR_DE_AVALIACAO_OBRIGATORIO("AVA-0002", "UC-0012", 422, "Bem aceito como garantia precisa do valor de avaliação."),
    AVALIACAO_JA_CONCLUIDA("AVA-0003", "UC-0012", 422, "A avaliação {0} já foi concluída."),
    PERCENTUAL_FORA_DA_FAIXA("AVA-0004", "RN-0024", 422, "O percentual acumulado da obra fica entre 0 e 100%."),
    MEDICAO_DIMINUIU("AVA-0005", "RN-0024", 422, "A medição nunca diminui: a anterior apurou {0}%.");

    private final String codigo;
    private final String regra;
    private final int status;
    private final String modelo;

    ErrosDaAvaliacao(String codigo, String regra, int status, String modelo) {
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
