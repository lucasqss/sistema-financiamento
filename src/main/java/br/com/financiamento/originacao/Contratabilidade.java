package br.com.financiamento.originacao;

/**
 * Se a proposta pode virar contrato agora. Impedida, traz o motivo em linguagem
 * de negócio e a proposta já reagiu a ele (expirada ou com pendência).
 */
/** @param codigoDoImpedimento o código do catálogo (ADR 0011) do impedimento */
public record Contratabilidade(PropostaParaContratar proposta, String impedimento, String codigoDoImpedimento) {

    public boolean contratavel() {
        return impedimento == null;
    }
}
