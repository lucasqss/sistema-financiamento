package br.com.financiamento.contratacao.dominio;

import br.com.financiamento.shared.validacao.ErroCatalogado;
import br.com.financiamento.shared.validacao.TipoDeErro;

/**
 * Catálogo de erros de domínio do módulo: Contratação (DOM-0004) (ADR 0011). O código é estável:
 * não reaproveite nem renumere; acrescente no fim. A regra é a do modelo de negócio.
 */
public enum ErrosDaContratacao implements ErroCatalogado {
    CONTRATACAO_NAO_ENCONTRADA("CON-0001", null, 404, "Contratação {0} não encontrada."),
    CONTRATO_NAO_ENCONTRADO("CON-0002", null, 404, "Contrato {0} não encontrado."),
    CONDICOES_JA_ACEITAS("CON-0003", "AGR-0001", 422, "As condições desta proposta já foram aceitas: uma proposta gera no máximo um contrato."),
    SEGURO_OPCIONAL_NAO_OFERECIDO("CON-0004", "RN-0025", 422, "Seguro opcional não oferecido para este tipo de bem: {0}."),
    SEGURO_FORA_DA_CONTRATACAO("CON-0005", "RN-0025", 422, "O seguro {0} não faz parte desta contratação."),
    SEGURO_JA_TEM_APOLICE("CON-0006", "RN-0025", 422, "O seguro {0} já tem a apólice {1}."),
    SEGUROS_EXIGIDOS("CON-0007", "RN-0025", 422, "O contrato só é emitido com os seguros exigidos contratados."),
    SEGUROS_PENDENTES("CON-0008", "RN-0025", 422, "O contrato só é emitido com os seguros contratados."),
    CONJUGE_SO_NO_IMOVEL("CON-0009", "RN-0026", 422, "Cônjuges assinam só no imóvel e no terreno."),
    SEM_SIGNATARIOS("CON-0010", "RN-0026", 422, "O contrato precisa de signatários."),
    NAO_E_SIGNATARIO("CON-0011", "RN-0026", 422, "Quem não é signatário exigido não assina o contrato."),
    PREVISAO_DA_OBRA_OBRIGATORIA("CON-0012", "UC-0014", 422, "No imóvel na planta, informe a previsão de entrega da obra."),
    PREVISAO_SO_NA_PLANTA("CON-0013", "UC-0014", 422, "Previsão de entrega da obra só no imóvel na planta."),
    NOVA_DATA_ANTERIOR("CON-0014", "RN-0027", 422, "A nova data precisa ser posterior à previsão atual ({0})."),
    SEM_PRORROGACAO_PENDENTE("CON-0015", "RN-0027", 422, "Não há prorrogação pendente."),
    TRANSICAO_INVALIDA("CON-0016", "CV-0002", 422, "O contrato {0} não aceita {1}."),
    DESTINO_INVALIDO("CON-0017", "CV-0002", 422, "O contrato {0} não vai para {1} com {2}.");

    private final String codigo;
    private final String regra;
    private final int status;
    private final String modelo;

    ErrosDaContratacao(String codigo, String regra, int status, String modelo) {
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
