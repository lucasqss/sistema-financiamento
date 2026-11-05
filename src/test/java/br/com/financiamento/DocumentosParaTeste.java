package br.com.financiamento;

/** Gera CPF e CNPJ válidos (dígitos verificadores corretos) para os testes. */
public final class DocumentosParaTeste {

    private DocumentosParaTeste() {
    }

    public static String cpf(int semente) {
        var base = String.format("%09d", 100_000_000 + semente * 7_919 % 800_000_000);
        int d1 = digitoCpf(base, 10);
        int d2 = digitoCpf(base + d1, 11);
        return base + d1 + d2;
    }

    public static String cnpj(int semente) {
        var base = String.format("%08d", 10_000_000 + semente * 7_919 % 80_000_000) + "0001";
        int d1 = digitoCnpj(base, new int[]{5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});
        int d2 = digitoCnpj(base + d1, new int[]{6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2});
        return base + d1 + d2;
    }

    private static int digitoCpf(String d, int pesoInicial) {
        int soma = 0;
        for (int i = 0; i < d.length(); i++) soma += (d.charAt(i) - '0') * (pesoInicial - i);
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
