package br.com.financiamento.originacao.dominio;

import br.com.financiamento.shared.validacao.ErroCatalogado;
import br.com.financiamento.shared.validacao.TipoDeErro;

/**
 * Catálogo de erros de domínio do módulo: Originação (DOM-0001) (ADR 0011). O código é estável:
 * não reaproveite nem renumere; acrescente no fim. A regra é a do modelo de negócio.
 */
public enum ErrosDaOriginacao implements ErroCatalogado {
    PROPOSTA_NAO_ENCONTRADA("ORI-0001", null, 404, "Proposta {0} não encontrada."),
    NAO_ACEITA_ALTERACAO("ORI-0002", "AGR-0001", 422, "Só o rascunho e a proposta com pendência aceitam alteração; esta está {0}."),
    TRANSICAO_INVALIDA("ORI-0003", "CV-0001", 422, "A proposta {0} não aceita {1}."),
    TITULAR_DESCONHECIDO("ORI-0004", "RN-0009", 422, "O titular {0} não é o bem nem um participante da proposta."),
    DOCUMENTO_JA_ACEITO("ORI-0005", "RN-0009", 422, "O documento {0} ({1}) já foi aceito."),
    CONFERENCIA_FORA_DA_ANALISE("ORI-0006", "UC-0024", 422, "A conferência é feita na proposta submetida e ainda em análise; esta está {0}."),
    DOCUMENTO_NAO_VIGENTE("ORI-0007", "UC-0024", 422, "O documento {0} não é o vigente desta proposta."),
    PROPOSTA_NAO_APROVADA("ORI-0008", "UC-0014", 422, "A proposta está {0}; só a aprovada é contratada."),
    PROPOSTA_EXPIROU("ORI-0009", "RN-0021", 422, "A proposta passou da validade de 30 dias e expirou."),
    AVALIACAO_VENCIDA("ORI-0010", "RN-0022", 422, "A avaliação do bem venceu; a proposta ficou com pendência para reavaliar."),
    FALTA_PROPONENTE("ORI-0011", "RN-0018", 422, "Informe o proponente."),
    FALTA_IDENTIFICACAO_DO_BEM("ORI-0012", "RN-0018", 422, "Informe a identificação do bem."),
    FALTA_VALOR_DE_COMPRA("ORI-0013", "RN-0018", 422, "Informe o valor de compra do bem."),
    FALTA_VENDEDOR("ORI-0014", "RN-0018", 422, "Informe o vendedor."),
    FALTAM_CONDICOES("ORI-0015", "RN-0018", 422, "Informe entrada, prazo e parcela."),
    FALTA_PARCELA("ORI-0016", "RN-0018", 422, "Informe a parcela simulada."),
    PARTICIPANTES_DEMAIS("ORI-0017", "RN-0001", 422, "A proposta tem {0} participantes; o máximo é 5 (1 proponente e até 4 coobrigados)."),
    QUANTIDADE_DE_PROPONENTES("ORI-0018", "RN-0001", 422, "A proposta tem {0} proponentes; deve ter exatamente 1."),
    PARTICIPANTE_REPETIDO("ORI-0019", "RN-0001", 422, "Participante repetido: {0}."),
    FALTA_NASCIMENTO("ORI-0020", "RN-0001", 422, "Informe a data de nascimento de {0}."),
    MENOR_DE_IDADE("ORI-0021", "RN-0001", 422, "{0} não é maior de idade."),
    DOCUMENTO_FISCAL_INVALIDO("ORI-0022", "RN-0033", 422, "{0} inválido: {1}."),
    PJ_NAO_ACEITA("ORI-0023", "RN-0002", 422, "Este tipo de bem não aceita pessoa jurídica como proponente."),
    COOBRIGADO_PJ("ORI-0024", "RN-0002", 422, "Coobrigado pessoa jurídica só em proposta de proponente pessoa jurídica."),
    VENDEDOR_INVALIDO("ORI-0025", "RN-0003", 422, "CPF ou CNPJ do vendedor inválido: {0}."),
    VENDEDOR_E_PROPONENTE("ORI-0026", "RN-0003", 422, "O vendedor não pode ser o proponente."),
    BEM_EM_OUTRA_PROPOSTA("ORI-0027", "RN-0004", 422, "O bem já está em outra proposta ativa."),
    VALOR_FINANCIADO("ORI-0028", "RN-0005", 422, "O valor financiado precisa ser maior que zero."),
    COTA_EXCEDIDA("ORI-0029", "RN-0006", 422, "Valor financiado de {0} passa da cota máxima de {1}% do valor de referência ({2}). A entrada mínima é {3}."),
    PRAZO_FORA_DA_FAIXA("ORI-0030", "RN-0007", 422, "Prazo de {0} meses fora da faixa do tipo: {1} a {2}."),
    COMPROMETIMENTO_DE_RENDA("ORI-0031", "RN-0008", 422, "Parcela de {0} passa de 30% da renda comprovada somada ({1})."),
    DOCUMENTO_FALTANDO("ORI-0032", "RN-0009", 422, "Falta o documento {0}: {1}."),
    FGTS_NAO_ACEITO("ORI-0033", "RN-0015", 422, "Só imóvel residencial de proponente pessoa física aceita FGTS na entrada.");

    private final String codigo;
    private final String regra;
    private final int status;
    private final String modelo;

    ErrosDaOriginacao(String codigo, String regra, int status, String modelo) {
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
