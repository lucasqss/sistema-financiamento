package br.com.financiamento.contratacao.aplicacao;

import br.com.financiamento.bem.BemFacade;
import br.com.financiamento.contratacao.dominio.PreparoDoContrato;
import br.com.financiamento.contratacao.dominio.PreparoDoContratoRepository;
import br.com.financiamento.contratacao.dominio.assinatura.Signatario;
import br.com.financiamento.originacao.OriginacaoFacade;
import br.com.financiamento.shared.dominio.RegraVioladaException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Clock;
import java.util.List;

/**
 * UC-0014, passo 1: o proponente aceita as condições finais da proposta aprovada
 * e escolhe os seguros opcionais. Os exigidos vêm do tipo de bem (RN-0025) e
 * são pedidos à seguradora pelo outbox. Emite CondicoesAceitas.
 */
@ApplicationScoped
public class AceitarCondicoesService {

    public record Conjuge(String documento, String nome) {
    }

    @Inject
    OriginacaoFacade originacao;

    @Inject
    BemFacade bens;

    @Inject
    PreparoDoContratoRepository preparos;

    @Inject
    Contratacoes contratacoes;

    @Inject
    Clock relogio;

    @Transactional
    public DetalheDaContratacao executar(Long propostaId, List<String> segurosOpcionais, List<Conjuge> conjuges) {
        if (preparos.daProposta(propostaId).isPresent()) {
            throw new RegraVioladaException("AGR-0001", "As condições desta proposta já foram aceitas: uma proposta gera no máximo um contrato.");
        }
        var contratabilidade = originacao.contratabilidade(propostaId);
        if (!contratabilidade.contratavel()) return DetalheDaContratacao.impedida(propostaId, contratabilidade.impedimento());

        var proposta = contratabilidade.proposta();
        var categoria = bens.categoria(proposta.tipoDeBem());
        var opcionais = segurosOpcionais == null ? List.<String>of() : segurosOpcionais;
        for (var s : opcionais) {
            if (!categoria.segurosOpcionais(proposta.tipoDeBem()).contains(s)) {
                throw new RegraVioladaException("RN-0025", "Seguro opcional não oferecido para este tipo de bem: " + s + ".");
            }
        }
        var listaDeConjuges = conjuges == null ? List.<Conjuge>of() : conjuges;
        if (!listaDeConjuges.isEmpty() && !categoria.vendedorEConjugesAssinam(proposta.tipoDeBem())) {
            throw new RegraVioladaException("RN-0026", "Cônjuges assinam só no imóvel e no terreno.");
        }
        var preparo = PreparoDoContrato.aceitar(propostaId, proposta.tipoDeBem(), proposta.condicoesVersao(),
                categoria.segurosExigidos(proposta.tipoDeBem()), opcionais,
                listaDeConjuges.stream().map(c -> new Signatario("CONJUGE", c.documento(), c.nome())).toList(),
                relogio.instant());
        contratacoes.salvar(preparo);
        return DetalheDaContratacao.de(preparo);
    }
}
