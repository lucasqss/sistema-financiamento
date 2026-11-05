package br.com.financiamento.contratacao.aplicacao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ConsultarContratacaoService {

    @Inject
    Contratacoes contratacoes;

    @Transactional
    public DetalheDaContratacao contratacao(Long id) {
        return DetalheDaContratacao.de(contratacoes.preparo(id));
    }

    @Transactional
    public DetalheDoContrato contrato(Long id) {
        return DetalheDoContrato.de(contratacoes.contrato(id));
    }
}
