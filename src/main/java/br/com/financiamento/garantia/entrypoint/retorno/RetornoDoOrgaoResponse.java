package br.com.financiamento.garantia.entrypoint.retorno;

/** Como o retorno foi classificado (RN-0029); nunca uma recusa. */
public record RetornoDoOrgaoResponse(String protocolo, String efeito) {
}
