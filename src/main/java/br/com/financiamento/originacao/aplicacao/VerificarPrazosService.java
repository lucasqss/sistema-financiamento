package br.com.financiamento.originacao.aplicacao;

import br.com.financiamento.originacao.dominio.PropostaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;

/**
 * Prazos que correm sozinhos, pela passagem do tempo:
 * - UC-0007: a aprovada sem contrato em 30 dias expira (RN-0021);
 * - RN-0022: a aprovada com avaliação vencida fica com pendência e o bem é reavaliado;
 * - RN-0020: a contraproposta vencida conta como recusada.
 */
@ApplicationScoped
public class VerificarPrazosService {

    @Inject
    PropostaRepository repositorio;

    @Inject
    Propostas propostas;

    /** Devolve quantas propostas mudaram. */
    @Transactional
    public int executar(LocalDate hoje) {
        int mudaram = 0;
        for (var p : repositorio.comPrazoCorrendo()) {
            boolean mudou = p.expirarSeVencida(hoje) || p.recusarContrapropostaSeVencida(hoje);
            if (!mudou && p.pendenciaSeAvaliacaoVencida(hoje)) {
                propostas.solicitarAnalises(p);
                mudou = true;
            }
            if (mudou) {
                propostas.salvar(p);
                mudaram++;
            }
        }
        return mudaram;
    }
}
