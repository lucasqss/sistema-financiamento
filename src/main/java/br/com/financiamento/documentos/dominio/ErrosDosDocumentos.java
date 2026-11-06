package br.com.financiamento.documentos.dominio;

import br.com.financiamento.shared.validacao.ErroCatalogado;
import br.com.financiamento.shared.validacao.TipoDeErro;

/**
 * Catálogo de erros de domínio do módulo: Documentos (DOM-0008) (ADR 0011). O código é estável:
 * não reaproveite nem renumere; acrescente no fim. A regra é a do modelo de negócio.
 */
public enum ErrosDosDocumentos implements ErroCatalogado {
    DOCUMENTO_NAO_ENCONTRADO("DOC-0001", null, 404, "Documento {0} não encontrado."),
    ARQUIVO_VAZIO("DOC-0002", "UC-0024", 422, "O arquivo está vazio."),
    ARQUIVO_GRANDE_DEMAIS("DOC-0003", "UC-0024", 422, "O arquivo passa de 10 MB."),
    TIPO_DE_ARQUIVO("DOC-0004", "UC-0024", 422, "Envie PDF, JPEG ou PNG."),
    CONTEUDO_DIVERGENTE("DOC-0005", "UC-0024", 422, "O conteúdo do arquivo não é {0}."),
    TIPO_DE_DOCUMENTO_OBRIGATORIO("DOC-0006", "RN-0009", 422, "Informe qual documento é."),
    JA_CONFERIDO("DOC-0007", "UC-0024", 422, "O documento {0} está {1} e não pode ser conferido de novo."),
    MOTIVO_OBRIGATORIO("DOC-0008", "UC-0024", 422, "Informe o motivo da recusa: ilegível, vencido ou divergente."),
    DOCUMENTO_JA_ACEITO("DOC-0009", "RN-0009", 422, "O documento {0} já foi aceito.");

    private final String codigo;
    private final String regra;
    private final int status;
    private final String modelo;

    ErrosDosDocumentos(String codigo, String regra, int status, String modelo) {
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
