package br.com.financiamento.garantia.entrypoint.retorno;

import br.com.financiamento.garantia.dominio.retorno.ResultadoDoOrgao;

/**
 * @param identificacaoRegistrada a matrícula ou o chassi que o órgão registrou
 * @param texto                   a exigência ou o motivo da recusa
 */
public record RetornoDoOrgaoRequest(String protocolo, ResultadoDoOrgao resultado, String identificacaoRegistrada,
                                    String texto) {
}
