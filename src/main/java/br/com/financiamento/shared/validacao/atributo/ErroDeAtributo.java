package br.com.financiamento.shared.validacao.atributo;

import br.com.financiamento.shared.validacao.ErroCatalogado;
import br.com.financiamento.shared.validacao.TipoDeErro;

/** Os erros de forma e formato, comuns a todas as requisições (ADR 0011). */
public enum ErroDeAtributo implements ErroCatalogado {
    OBRIGATORIO("ATR-0001", "Campo obrigatório."),
    TAMANHO_MAXIMO("ATR-0002", "No máximo {0} caracteres."),
    FORMATO("ATR-0003", "Formato inválido: {0}."),
    CPF_INVALIDO("ATR-0004", "CPF inválido."),
    CNPJ_INVALIDO("ATR-0005", "CNPJ inválido."),
    CPF_OU_CNPJ_INVALIDO("ATR-0006", "CPF ou CNPJ inválido."),
    MAIOR_QUE_ZERO("ATR-0007", "Precisa ser maior que zero."),
    NAO_NEGATIVO("ATR-0008", "Não pode ser negativo."),
    FORA_DA_LISTA("ATR-0009", "Valor não aceito; use um destes: {0}."),
    QUANTIDADE_MAXIMA("ATR-0010", "No máximo {0} itens."),
    LISTA_VAZIA("ATR-0011", "Informe ao menos um item."),
    NO_MAXIMO("ATR-0012", "No máximo {0}.");

    private final String codigo;
    private final String modelo;

    ErroDeAtributo(String codigo, String modelo) {
        this.codigo = codigo;
        this.modelo = modelo;
    }

    @Override
    public String codigo() {
        return codigo;
    }

    @Override
    public TipoDeErro tipo() {
        return TipoDeErro.ATRIBUTO;
    }

    @Override
    public String modelo() {
        return modelo;
    }
}
