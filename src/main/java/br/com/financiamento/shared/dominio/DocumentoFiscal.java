package br.com.financiamento.shared.dominio;

/**
 * Validação de CPF e CNPJ pelos dígitos verificadores (RN-0033).
 */
public final class DocumentoFiscal {

    private DocumentoFiscal() {
    }

    /** Só os dígitos do documento. */
    public static String digitos(String documento) {
        return documento == null ? "" : documento.replaceAll("\\D", "");
    }

    public static boolean cpfValido(String cpf) {
        var d = digitos(cpf);
        if (d.length() != 11 || d.chars().distinct().count() == 1) return false;
        return digito(d, 9, 10) == d.charAt(9) - '0' && digito(d, 10, 11) == d.charAt(10) - '0';
    }

    public static boolean cnpjValido(String cnpj) {
        var d = digitos(cnpj);
        if (d.length() != 14 || d.chars().distinct().count() == 1) return false;
        int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        return digitoCnpj(d, pesos1) == d.charAt(12) - '0' && digitoCnpj(d, pesos2) == d.charAt(13) - '0';
    }

    private static int digito(String d, int tamanho, int pesoInicial) {
        int soma = 0;
        for (int i = 0; i < tamanho; i++) soma += (d.charAt(i) - '0') * (pesoInicial - i);
        int resto = (soma * 10) % 11;
        return resto == 10 ? 0 : resto;
    }

    private static int digitoCnpj(String d, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < pesos.length; i++) soma += (d.charAt(i) - '0') * pesos[i];
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
