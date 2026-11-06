package br.com.financiamento.bem;

import br.com.financiamento.shared.validacao.ErroCatalogado;
import br.com.financiamento.shared.validacao.TipoDeErro;

/**
 * Catálogo de erros de domínio do módulo: Bem e plug-ins de categoria (DIM-0001) (ADR 0011). O código é estável:
 * não reaproveite nem renumere; acrescente no fim. A regra é a do modelo de negócio.
 */
public enum ErrosDoBem implements ErroCatalogado {
    TIPO_NAO_FINANCIADO("BEM-0001", "DIM-0001", 422, "Tipo de bem não financiado: {0}. Tipos aceitos: {1}."),
    ANO_MODELO_FALTANDO("BEM-0002", "RN-0011", 422, "Informe o ano-modelo do veículo usado."),
    VEICULO_ANTIGO_DEMAIS("BEM-0003", "RN-0011", 422, "O veículo terá {0} anos ao fim do contrato ({1}); o máximo é {2}."),
    IDADE_DO_PROPONENTE("BEM-0004", "RN-0012", 422, "Idade do proponente somada ao prazo passa de 80 anos e 6 meses; o prazo máximo possível é de {0} meses."),
    CONSTRUCAO_NAO_INFORMADA("BEM-0005", "RN-0014", 422, "Informe se há construção averbada na matrícula do terreno."),
    TERRENO_COM_CONSTRUCAO("BEM-0006", "RN-0014", 422, "Terreno com construção averbada não é financiado como terreno.");

    private final String codigo;
    private final String regra;
    private final int status;
    private final String modelo;

    ErrosDoBem(String codigo, String regra, int status, String modelo) {
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
