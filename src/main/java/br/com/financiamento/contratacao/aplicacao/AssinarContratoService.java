package br.com.financiamento.contratacao.aplicacao;

import br.com.financiamento.contratacao.ContratoAssinado;
import br.com.financiamento.contratacao.OuvinteDoContrato;
import br.com.financiamento.contratacao.dominio.EstadoContrato;
import br.com.financiamento.shared.seguranca.ErroDeSeguranca;
import br.com.financiamento.shared.seguranca.UsuarioAtual;
import br.com.financiamento.shared.validacao.CadeiaDeValidacao;
import br.com.financiamento.shared.validacao.ErroDeDominioException;
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
    UsuarioAtual usuarioAtual;

    @Inject
    @Any
    Instance<OuvinteDoContrato> ouvintes;

    @Transactional
    public DetalheDoContrato executar(Long contratoId, String documento) {
        // ADR 0012: cada signatário assina por si; o documento informado é o do token.
        CadeiaDeValidacao.<String>de((doc, proximo) -> {
            var usuario = usuarioAtual.usuario();
            if (usuario.isPresent() && !usuario.get().sistema()
                    && !usuario.get().documento().equals(doc == null ? "" : doc.replaceAll("\\D", ""))) {
                throw new ErroDeDominioException(ErroDeSeguranca.ASSINATURA_DE_OUTRO);
            }
            proximo.seguir(doc);
        }).validar(documento);
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
