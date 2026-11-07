import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Emissor de JWT só para o ambiente local (docker/compose.yml), sem dependências além do JDK:
 *
 *   java docker/emissor-local/GerarTokens.java
 *
 * Na primeira vez, gera o par de chaves (docker/emissor-local/chave-privada.pem). A cada execução, grava o
 * JWKS que o WireMock serve (docker/wiremock/__files/emissor-jwks.json) e os tokens de longa validade dos
 * sistemas externos e de pessoas de exemplo (docker/externos.env). NUNCA use estas chaves fora da máquina local.
 */
public class GerarTokens {

    static final Path DIR = Path.of("docker");
    static final String EMISSOR = "https://emissor.local";
    static final String KID = "emissor-local";

    public static void main(String[] args) throws Exception {
        var arquivoDaChave = DIR.resolve("emissor-local/chave-privada.pem");
        RSAPrivateCrtKey privada;
        if (Files.exists(arquivoDaChave)) {
            var pem = Files.readString(arquivoDaChave).replaceAll("-----[A-Z ]+-----", "").replaceAll("\\s", "");
            privada = (RSAPrivateCrtKey) KeyFactory.getInstance("RSA")
                    .generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(pem)));
        } else {
            var gerador = KeyPairGenerator.getInstance("RSA");
            gerador.initialize(2048);
            privada = (RSAPrivateCrtKey) gerador.generateKeyPair().getPrivate();
            Files.writeString(arquivoDaChave, "-----BEGIN PRIVATE KEY-----\n"
                    + Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(privada.getEncoded())
                    + "\n-----END PRIVATE KEY-----\n");
        }
        var publica = (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new RSAPublicKeySpec(privada.getModulus(), privada.getPublicExponent()));
        Files.writeString(DIR.resolve("wiremock/__files/emissor-jwks.json"), """
                {"keys":[{"kty":"RSA","use":"sig","alg":"RS256","kid":"%s","n":"%s","e":"%s"}]}
                """.formatted(KID, b64(publica.getModulus()), b64(publica.getPublicExponent())));

        var tokens = new LinkedHashMap<String, String>();
        for (var sistema : new String[]{"sistema-bureau", "sistema-avaliador", "sistema-seguradora", "sistema-registro",
                "sistema-fgts", "sistema-pagamento", "sistema-financeiro", "sistema-cobranca"}) {
            tokens.put("TOKEN_" + sistema.toUpperCase().replace('-', '_'), token(privada, sistema, null, sistema));
        }
        // Pessoas de exemplo, para chamar a API à mão (CPFs válidos fictícios).
        tokens.put("TOKEN_PROPONENTE", token(privada, "ana", "52998224725", "proponente"));
        tokens.put("TOKEN_VENDEDOR", token(privada, "bruno", "11144477735", "vendedor"));
        tokens.put("TOKEN_ANALISTA", token(privada, "carla", "39053344705", "analista"));
        tokens.put("TOKEN_ENGENHEIRO", token(privada, "davi", "16899535009", "engenheiro"));

        var env = new StringBuilder("# Gerado por: java docker/emissor-local/GerarTokens.java (só para o ambiente local)\n");
        tokens.forEach((k, v) -> env.append(k).append('=').append(v).append('\n'));
        var opts = new StringBuilder("WIREMOCK_JAVA_OPTS=");
        tokens.forEach((k, v) -> {
            if (k.startsWith("TOKEN_SISTEMA_")) {
                opts.append("-Dtoken.").append(k.substring(6).toLowerCase().replace('_', '-')).append('=').append(v).append(' ');
            }
        });
        env.append(opts.toString().trim()).append('\n');
        Files.writeString(DIR.resolve("externos.env"), env.toString());
        System.out.println("Gravados docker/externos.env e docker/wiremock/__files/emissor-jwks.json");
    }

    static String token(RSAPrivateCrtKey chave, String sujeito, String documento, String papel) throws Exception {
        var agora = Instant.now();
        var claims = new LinkedHashMap<String, Object>();
        claims.put("iss", EMISSOR);
        claims.put("sub", sujeito);
        if (documento != null) claims.put("cpf", documento);
        claims.put("groups", new String[]{papel});
        claims.put("iat", agora.getEpochSecond());
        claims.put("exp", agora.plus(3650, ChronoUnit.DAYS).getEpochSecond());
        var cabecalho = b64("{\"alg\":\"RS256\",\"typ\":\"JWT\",\"kid\":\"" + KID + "\"}");
        var corpo = b64(json(claims));
        var assinador = Signature.getInstance("SHA256withRSA");
        assinador.initSign(chave);
        assinador.update((cabecalho + "." + corpo).getBytes(StandardCharsets.US_ASCII));
        return cabecalho + "." + corpo + "." + Base64.getUrlEncoder().withoutPadding().encodeToString(assinador.sign());
    }

    static String json(Map<String, Object> m) {
        var sb = new StringBuilder("{");
        m.forEach((k, v) -> {
            if (sb.length() > 1) sb.append(',');
            sb.append('"').append(k).append("\":");
            if (v instanceof String s) sb.append('"').append(s).append('"');
            else if (v instanceof String[] a) sb.append("[\"").append(String.join("\",\"", a)).append("\"]");
            else sb.append(v);
        });
        return sb.append('}').toString();
    }

    static String b64(String s) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(s.getBytes(StandardCharsets.UTF_8));
    }

    static String b64(BigInteger n) {
        var bytes = n.toByteArray();
        if (bytes.length > 1 && bytes[0] == 0) bytes = java.util.Arrays.copyOfRange(bytes, 1, bytes.length);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
