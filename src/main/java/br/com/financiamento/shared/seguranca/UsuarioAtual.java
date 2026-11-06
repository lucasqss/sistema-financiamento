package br.com.financiamento.shared.seguranca;

import io.quarkus.arc.Arc;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.Optional;

/**
 * O usuário da requisição, lido do token (ADR 0012). Fora de uma requisição (agendamento de prazos) não há
 * usuário: a conferência de dono não se aplica, porque não há quem conferir.
 */
@ApplicationScoped
public class UsuarioAtual {

    @Inject
    Instance<JsonWebToken> token;

    @ConfigProperty(name = "financiamento.seguranca.claim-do-documento", defaultValue = "cpf")
    String claimDoDocumento;

    public Optional<Usuario> usuario() {
        if (!Arc.container().requestContext().isActive()) return Optional.empty();
        var jwt = token.get();
        if (jwt == null || jwt.getRawToken() == null) return Optional.empty();
        Object documento = jwt.getClaim(claimDoDocumento);
        return Optional.of(new Usuario(documento == null ? null : documento.toString(), jwt.getGroups()));
    }

    /** Pode agir sobre algo com estes participantes? Sem usuário (fora de requisição), pode. */
    public boolean participaDe(java.util.Collection<String> documentos) {
        return usuario().map(u -> u.participaDe(documentos)).orElse(true);
    }
}
