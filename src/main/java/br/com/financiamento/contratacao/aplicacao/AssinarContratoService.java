package br.com.financiamento.contratacao.aplicacao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Clock;

/** UC-0015: cada signatário exigido assina; com a última assinatura, emite ContratoAssinado. */
@ApplicationScoped
public class AssinarContratoService {

    @Inject
    Contratacoes contratacoes;

    @Inject
    Clock relogio;

    @Transactional
    public DetalheDoContrato executar(Long contratoId, String documento) {
        var contrato = contratacoes.contrato(contratoId);
        if (contrato.assinar(documento, relogio.instant())) contratacoes.salvar(contrato);
        return DetalheDoContrato.de(contrato);
    }
}
