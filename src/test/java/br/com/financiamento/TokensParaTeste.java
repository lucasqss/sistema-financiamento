package br.com.financiamento;

import io.smallrye.jwt.build.Jwt;
import io.smallrye.jwt.util.KeyUtils;

import java.security.PrivateKey;
import java.time.Instant;
import java.util.Set;

/**
 * Tokens JWT assinados com a chave de teste (src/test/resources/privateKey.pem), como o emissor externo os
 * emitiria (ADR 0012): CPF/CNPJ na claim "cpf" e papéis em "groups".
 */
public final class TokensParaTeste {

    public static final String EMISSOR = "https://emissor.teste";

    /** O operador dos testes de jornada: todos os papéis humanos e de sistema. */
    public static final String OPERADOR_DOCUMENTO = "00000000191";
    public static final Set<String> TODOS_OS_PAPEIS = Set.of("proponente", "coobrigado", "vendedor", "correspondente",
            "analista", "engenheiro", "conjuge", "sistema-bureau", "sistema-avaliador", "sistema-seguradora",
            "sistema-registro", "sistema-fgts", "sistema-pagamento");

    private static PrivateKey chave;

    private TokensParaTeste() {
    }

    private static synchronized PrivateKey chave() {
        if (chave == null) {
            try {
                chave = KeyUtils.readPrivateKey("/privateKey.pem");
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        return chave;
    }

    public static String de(String documento, String... papeis) {
        return Jwt.issuer(EMISSOR).subject(documento).claim("cpf", documento).groups(Set.of(papeis))
                .expiresIn(3600).sign(chave());
    }

    public static String operador() {
        return Jwt.issuer(EMISSOR).subject("operador-de-teste").claim("cpf", OPERADOR_DOCUMENTO)
                .groups(TODOS_OS_PAPEIS).expiresIn(3600).sign(chave());
    }

    /** Quem assina o contrato assina por si (ADR 0012). */
    public static String signatario(String documento) {
        return de(documento, "proponente", "coobrigado", "vendedor", "conjuge");
    }

    public static String sistema(String papel) {
        return Jwt.issuer(EMISSOR).subject(papel).groups(Set.of(papel)).expiresIn(3600).sign(chave());
    }

    public static String deOutroEmissor(String documento, String... papeis) {
        return Jwt.issuer("https://outro.emissor").claim("cpf", documento).groups(Set.of(papeis)).expiresIn(3600).sign(chave());
    }

    public static String expirado(String documento, String... papeis) {
        return Jwt.issuer(EMISSOR).claim("cpf", documento).groups(Set.of(papeis))
                .issuedAt(Instant.now().minusSeconds(7200)).expiresAt(Instant.now().minusSeconds(3600)).sign(chave());
    }
}
