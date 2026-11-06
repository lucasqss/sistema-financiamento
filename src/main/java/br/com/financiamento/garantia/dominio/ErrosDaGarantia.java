package br.com.financiamento.garantia.dominio;

import br.com.financiamento.shared.validacao.ErroCatalogado;
import br.com.financiamento.shared.validacao.TipoDeErro;

/**
 * Catálogo de erros de domínio do módulo: Garantia (DOM-0005) (ADR 0011). O código é estável:
 * não reaproveite nem renumere; acrescente no fim. A regra é a do modelo de negócio.
 */
public enum ErrosDaGarantia implements ErroCatalogado {
    REGISTRO_NAO_ENCONTRADO("GAR-0001", null, 404, "Registro de garantia {0} não encontrado."),
    PROTOCOLO_DESCONHECIDO("GAR-0002", null, 404, "Protocolo {0} não é nosso."),
    CONTRATO_SEM_REGISTRO("GAR-0003", null, 404, "O contrato {0} não tem registro de garantia."),
    REENVIO_INVALIDO("GAR-0004", "UC-0019", 422, "Só se reenvia o registro recusado ou com divergência de identificação."),
    SEM_EXIGENCIA("GAR-0005", "UC-0020", 422, "Só há exigência a cumprir no registro em exigência."),
    DESFAZER_SO_RECUSADO("GAR-0006", "UC-0019", 422, "Só se desfaz o contrato com o registro recusado.");

    private final String codigo;
    private final String regra;
    private final int status;
    private final String modelo;

    ErrosDaGarantia(String codigo, String regra, int status, String modelo) {
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
