package br.com.financiamento.contratacao.aplicacao;

import br.com.financiamento.contratacao.dominio.ErrosDaContratacao;
import br.com.financiamento.shared.validacao.ContextoDeDominio;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * UC-0014, passo 2: retorno da seguradora (ATR-0011) com a apólice de um seguro.
 * Idempotente. Com o último seguro (EVT-0022), tenta emitir o contrato.
 */
@ApplicationScoped
public class RegistrarApoliceService {

    @Inject
    Contratacoes contratacoes;

    @Inject
    EmitirContratoService emissao;

    @Transactional
    public DetalheDaContratacao executar(Long preparoId, String seguro, String apolice) {
        var preparo = contratacoes.preparo(preparoId, ContextoDeDominio.exigir(
                p -> p.seguros().stream().anyMatch(s -> s.nome().equalsIgnoreCase(seguro)),
                ErrosDaContratacao.SEGURO_FORA_DA_CONTRATACAO, p -> new Object[]{seguro}));
        if (!preparo.registrarApolice(seguro, apolice)) return DetalheDaContratacao.de(preparo);
        contratacoes.salvar(preparo);
        if (preparo.segurosContratados() && !preparo.emitido()) return emissao.emitir(preparo);
        return DetalheDaContratacao.de(preparo);
    }
}
