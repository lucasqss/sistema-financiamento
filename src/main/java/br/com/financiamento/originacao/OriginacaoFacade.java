package br.com.financiamento.originacao;

import br.com.financiamento.originacao.aplicacao.ContratacaoDaPropostaService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * API pública da Originação (DOM-0001) para os outros módulos. Os resultados do
 * Crédito e da Avaliação chegam pelas portas de ouvinte deles (ADR 0009).
 */
@ApplicationScoped
public class OriginacaoFacade {

    @Inject
    ContratacaoDaPropostaService contratacao;

    /** UC-0014: a proposta aprovada, dentro da validade e com avaliação válida (RN-0021, RN-0022)? */
    public Contratabilidade contratabilidade(Long propostaId) {
        return contratacao.contratabilidade(propostaId);
    }

    /** Quem participa da proposta (ADR 0012): quem a iniciou, os participantes e o vendedor. */
    public java.util.List<String> documentosEnvolvidos(Long propostaId) {
        return contratacao.documentosEnvolvidos(propostaId);
    }

    /** UC-0014: o contrato foi emitido; a proposta fica contratada (EVT-0023 no ciclo CV-0001). */
    public void contratoEmitido(Long propostaId, Long contratoId) {
        contratacao.contratoEmitido(propostaId, contratoId);
    }
}
