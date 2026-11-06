package br.com.financiamento.contratacao.aplicacao;

import br.com.financiamento.contratacao.dominio.ErrosDaContratacao;
import br.com.financiamento.shared.validacao.ContextoDeDominio;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;

/**
 * UC-0017: a incorporadora pede nova data para a obra. Dentro do limite
 * (RN-0027), aplica; acima, vira pendência e o analista decide.
 */
@ApplicationScoped
public class ProrrogarObraService {

    @Inject
    Contratacoes contratacoes;

    @Transactional
    public DetalheDoContrato pedir(Long contratoId, LocalDate novaData) {
        var c = contratacoes.contrato(contratoId, Contratacoes.aceita("PrazoDaObraProrrogado"));
        c.pedirProrrogacao(novaData);
        contratacoes.salvar(c);
        return DetalheDoContrato.de(c);
    }

    @Transactional
    public DetalheDoContrato decidir(Long contratoId, boolean aprovada) {
        var c = contratacoes.contrato(contratoId, ContextoDeDominio.exigir(
                x -> x.cronograma() != null && x.cronograma().pendente() != null,
                ErrosDaContratacao.SEM_PRORROGACAO_PENDENTE, x -> new Object[0]));
        c.decidirProrrogacao(aprovada);
        contratacoes.salvar(c);
        return DetalheDoContrato.de(c);
    }
}
