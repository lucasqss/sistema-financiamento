package br.com.financiamento.contratacao.aplicacao;

import br.com.financiamento.bem.BemFacade;
import br.com.financiamento.bem.FormaDeLiberacao;
import br.com.financiamento.contratacao.dominio.PreparoDoContrato;
import br.com.financiamento.contratacao.dominio.PreparoDoContratoRepository;
import br.com.financiamento.contratacao.dominio.assinatura.Signatario;
import br.com.financiamento.originacao.OriginacaoFacade;
import br.com.financiamento.shared.validacao.CadeiaDeValidacao;
import br.com.financiamento.shared.validacao.ErroDeDominioException;
import br.com.financiamento.contratacao.dominio.ErrosDaContratacao;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Clock;
import java.time.LocalDate;
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
    public DetalheDaContratacao executar(Long propostaId, List<String> segurosOpcionais, List<Conjuge> conjuges,
                                         LocalDate previsaoDeEntregaDaObra) {
        // Cadeia de domínio (ADR 0011): uma proposta gera no máximo um contrato (invariante 8 do AGR-0001).
        CadeiaDeValidacao.<Long>de((id, proximo) -> {
            if (preparos.daProposta(id).isPresent()) throw new ErroDeDominioException(ErrosDaContratacao.CONDICOES_JA_ACEITAS);
            proximo.seguir(id);
        }).validar(propostaId);
        var contratabilidade = originacao.contratabilidade(propostaId);
        if (!contratabilidade.contratavel()) return DetalheDaContratacao.impedida(propostaId, contratabilidade.impedimento());

        var proposta = contratabilidade.proposta();
        var categoria = bens.categoria(proposta.tipoDeBem());
        var opcionais = segurosOpcionais == null ? List.<String>of() : segurosOpcionais;
        for (var s : opcionais) {
            if (!categoria.segurosOpcionais(proposta.tipoDeBem()).contains(s)) {
                throw new ErroDeDominioException(ErrosDaContratacao.SEGURO_OPCIONAL_NAO_OFERECIDO, s);
            }
        }
        var listaDeConjuges = conjuges == null ? List.<Conjuge>of() : conjuges;
        if (!listaDeConjuges.isEmpty() && !categoria.vendedorEConjugesAssinam(proposta.tipoDeBem())) {
            throw new ErroDeDominioException(ErrosDaContratacao.CONJUGE_SO_NO_IMOVEL);
        }
        boolean naPlanta = categoria.parametros(proposta.tipoDeBem()).liberacao() == FormaDeLiberacao.POR_MEDICAO;
        if (naPlanta && previsaoDeEntregaDaObra == null) {
            throw new ErroDeDominioException(ErrosDaContratacao.PREVISAO_DA_OBRA_OBRIGATORIA);
        }
        if (!naPlanta && previsaoDeEntregaDaObra != null) {
            throw new ErroDeDominioException(ErrosDaContratacao.PREVISAO_SO_NA_PLANTA);
        }
        var preparo = PreparoDoContrato.aceitar(propostaId, proposta.tipoDeBem(), proposta.condicoesVersao(),
                categoria.segurosExigidos(proposta.tipoDeBem()), opcionais,
                listaDeConjuges.stream().map(c -> new Signatario("CONJUGE", c.documento(), c.nome())).toList(),
                previsaoDeEntregaDaObra, relogio.instant());
        contratacoes.salvar(preparo);
        return DetalheDaContratacao.de(preparo);
    }
}
