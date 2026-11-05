package br.com.financiamento.originacao;

/**
 * Se a proposta pode virar contrato agora. Impedida, traz o motivo em linguagem
 * de negócio e a proposta já reagiu a ele (expirada ou com pendência).
 */
public record Contratabilidade(PropostaParaContratar proposta, String impedimento) {

    public boolean contratavel() {
        return impedimento == null;
    }
}
