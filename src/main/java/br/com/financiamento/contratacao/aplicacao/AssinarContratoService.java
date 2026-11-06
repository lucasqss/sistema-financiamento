package br.com.financiamento.contratacao.aplicacao;

import br.com.financiamento.contratacao.ContratoAssinado;
import br.com.financiamento.contratacao.OuvinteDoContrato;
import br.com.financiamento.contratacao.dominio.EstadoContrato;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Clock;

/**
 * UC-0015: cada signatário exigido assina; com a última assinatura, emite
 * ContratoAssinado, que dispara o registro da garantia (UC-0018) por quem ouve
 * o contrato, na mesma transação (ADR 0009).
 */
@ApplicationScoped
public class AssinarContratoService {

    @Inject
    Contratacoes contratacoes;

    @Inject
    Clock relogio;

    @Inject
    @Any
    Instance<OuvinteDoContrato> ouvintes;

    @Transactional
    public DetalheDoContrato executar(Long contratoId, String documento) {
        var contrato = contratacoes.contrato(contratoId, Contratacoes.aceita("ContratoAssinado"));
        if (!contrato.assinar(documento, relogio.instant())) return DetalheDoContrato.de(contrato);
        contratacoes.salvar(contrato);
        if (contrato.estado() == EstadoContrato.ASSINADO) {
            var assinado = new ContratoAssinado(contrato.id(), contrato.propostaId(), contrato.tipoDeBem(),
                    contrato.condicoes().identificacaoDoBem());
            ouvintes.forEach(o -> o.contratoAssinado(assinado));
        }
        return DetalheDoContrato.de(contrato);
    }
}
