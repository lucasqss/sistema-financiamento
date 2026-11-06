package br.com.financiamento.desembolso.dominio;

import br.com.financiamento.shared.validacao.ErroCatalogado;
import br.com.financiamento.shared.validacao.TipoDeErro;

/**
 * Catálogo de erros de domínio do módulo: Desembolso (DOM-0006) (ADR 0011). O código é estável:
 * não reaproveite nem renumere; acrescente no fim. A regra é a do modelo de negócio.
 */
public enum ErrosDoDesembolso implements ErroCatalogado {
    DESEMBOLSO_NAO_ENCONTRADO("DES-0001", null, 404, "Desembolso {0} não encontrado."),
    CONTRATO_SEM_DESEMBOLSO("DES-0002", null, 404, "O contrato {0} ainda não tem desembolso."),
    PROTOCOLO_DESCONHECIDO("DES-0003", null, 404, "Protocolo de liberação {0} não é nosso."),
    SEM_GARANTIA("DES-0004", "RN-0030", 422, "Nenhuma liberação antes de a garantia estar registrada e confirmada."),
    SOMA_EXCEDE_FINANCIADO("DES-0005", "RN-0031", 422, "A soma das liberações passaria do valor financiado."),
    LIBERACAO_DESCONHECIDA("DES-0006", "RN-0031", 422, "Protocolo de liberação desconhecido: {0}."),
    RECOMPOR_SO_COM_FGTS_NEGADO("DES-0007", "UC-0022", 422, "Só se recompõe a entrada com o FGTS negado."),
    MEDICAO_SO_NA_PLANTA("DES-0008", "UC-0013", 422, "Medição de obra só no imóvel na planta."),
    MEDICAO_SEM_GARANTIA("DES-0009", "UC-0013", 422, "Medição só para contrato de imóvel na planta com a garantia constituída.");

    private final String codigo;
    private final String regra;
    private final int status;
    private final String modelo;

    ErrosDoDesembolso(String codigo, String regra, int status, String modelo) {
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
