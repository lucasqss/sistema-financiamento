package br.com.financiamento.shared.seguranca;

import java.util.Set;

/**
 * Quem faz a requisição, como o token o descreve (ADR 0012): o CPF/CNPJ (só dígitos) e os papéis.
 */
public record Usuario(String documento, Set<String> papeis) {

    public Usuario {
        documento = documento == null ? "" : documento.replaceAll("\\D", "");
        papeis = Set.copyOf(papeis);
    }

    public boolean tem(String papel) {
        return papeis.contains(papel);
    }

    /** Um sistema externo (client credentials): autorizado pelo papel do retorno, não age como pessoa. */
    public boolean sistema() {
        return !papeis.isEmpty() && papeis.stream().allMatch(p -> p.startsWith(Papeis.PREFIXO_DE_SISTEMA));
    }

    /**
     * Só age no que participa (proposta, contrato)? Vale para proponente, coobrigado, vendedor, correspondente e
     * cônjuge. O analista e o engenheiro trabalham sobre todos os casos; os sistemas respondem pelo protocolo.
     */
    public boolean restritoAoQueParticipa() {
        return !sistema() && !tem(Papeis.ANALISTA) && !tem(Papeis.ENGENHEIRO);
    }

    /** Pode agir sobre algo que tem estes participantes (CPF/CNPJ)? */
    public boolean participaDe(java.util.Collection<String> documentos) {
        return !restritoAoQueParticipa() || documentos.stream().anyMatch(d -> d != null && d.equals(documento));
    }
}
