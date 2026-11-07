package br.com.financiamento.integracao;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.Signature;
import java.security.spec.RSAPublicKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * O ambiente local (docker/compose.yml) é coerente: os tokens de docker/externos.env, que o WireMock usa nos
 * webhooks, são assinados pela chave do JWKS que ele mesmo serve, vêm do emissor que a aplicação espera, têm o
 * papel do sistema e ainda valem. Se alguém regerar as chaves e esquecer os tokens (ou o contrário), quebra aqui.
 */
class AmbienteLocalTest {

    static final ObjectMapper JSON = new ObjectMapper();

    @Test
    void tokensDoAmbienteLocalValemContraOJwksDoEmissorLocal() throws Exception {
        var jwk = JSON.readTree(Path.of("docker/wiremock/__files/emissor-jwks.json").toFile()).path("keys").get(0);
        var chave = KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(
                new BigInteger(1, Base64.getUrlDecoder().decode(jwk.path("n").asText())),
                new BigInteger(1, Base64.getUrlDecoder().decode(jwk.path("e").asText()))));
        var compose = Files.readString(Path.of("docker/compose.yml"));
        var tokens = new HashMap<String, String>();
        for (var linha : Files.readAllLines(Path.of("docker/externos.env"))) {
            if (linha.startsWith("TOKEN_")) tokens.put(linha.substring(0, linha.indexOf('=')), linha.substring(linha.indexOf('=') + 1));
        }
        assertEquals(10, tokens.size(), tokens.keySet().toString());

        for (var t : tokens.entrySet()) {
            var partes = t.getValue().split("\\.");
            var verificador = Signature.getInstance("SHA256withRSA");
            verificador.initVerify(chave);
            verificador.update((partes[0] + "." + partes[1]).getBytes(StandardCharsets.US_ASCII));
            assertTrue(verificador.verify(Base64.getUrlDecoder().decode(partes[2])), t.getKey() + ": assinatura");
            var claims = JSON.readTree(Base64.getUrlDecoder().decode(partes[1]));
            assertTrue(compose.contains("JWT_ISSUER: " + claims.path("iss").asText()), t.getKey() + ": emissor");
            assertTrue(claims.path("exp").asLong() > Instant.now().plusSeconds(365L * 24 * 3600).getEpochSecond(),
                    t.getKey() + ": vale por mais de um ano");
            if (t.getKey().startsWith("TOKEN_SISTEMA_")) {
                var papel = t.getKey().substring(6).toLowerCase().replace('_', '-');
                assertEquals(papel, claims.path("groups").get(0).asText(), t.getKey());
                assertTrue(Files.readString(Path.of("docker/externos.env")).contains("-Dtoken." + papel + "="),
                        "o WireMock recebe o token de " + papel);
            }
        }
    }
}
