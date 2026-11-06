package br.com.financiamento.credito.aplicacao;

import br.com.financiamento.credito.CondicoesSugeridas;
import br.com.financiamento.credito.DecisaoDeCredito;
import br.com.financiamento.credito.OuvinteDoCredito;
import br.com.financiamento.credito.ResultadoDeCredito;
import br.com.financiamento.credito.dominio.AnaliseDeCredito;
import br.com.financiamento.credito.dominio.AnaliseDeCreditoRepository;
import br.com.financiamento.shared.validacao.ContextoDeDominio;
import br.com.financiamento.credito.dominio.ErrosDoCredito;
import br.com.financiamento.shared.outbox.Outbox;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Clock;
import java.util.HashMap;

/**
 * UC-0011, passo 3: registra a decisão do bureau ou do analista e a entrega a
 * quem acompanha o crédito, na mesma transação (ADR 0009). Emite
 * CreditoAprovado, CreditoAprovadoComRessalva ou CreditoReprovado.
 */
@ApplicationScoped
public class RegistrarDecisaoService {

    public record Decisao(ResultadoDeCredito resultado, String ressalva, CondicoesSugeridas sugeridas,
                          AnaliseDeCredito.Origem origem) {
    }

    @Inject
    AnaliseDeCreditoRepository analises;

    @Inject
    Outbox outbox;

    @Inject
    @Any
    Instance<OuvinteDoCredito> ouvintes;

    @Inject
    Clock relogio;

    @Transactional
    public AnaliseDeCredito executar(Long analiseId, Decisao d) {
        // Cadeia de domínio (ADR 0011): a análise existe e não foi decidida de outro jeito (UC-0011).
        var analise = ContextoDeDominio.validar(analiseId,
                ContextoDeDominio.carregar(analises::findByIdOptional, ErrosDoCredito.ANALISE_NAO_ENCONTRADA),
                ContextoDeDominio.exigir(a -> a.resultado() == null || a.resultado() == d.resultado(),
                        ErrosDoCredito.ANALISE_JA_DECIDIDA, a -> new Object[]{a.id(), a.resultado()}));
        if (!analise.decidir(d.resultado(), d.ressalva(), d.sugeridas(), d.origem(), relogio.instant())) {
            return analise; // retorno repetido: nenhum novo efeito
        }
        var dados = new HashMap<String, Object>();
        dados.put("propostaId", analise.propostaId());
        dados.put("condicoesVersao", analise.condicoesVersao());
        dados.put("decididaPor", d.origem().name());
        if (d.ressalva() != null) dados.put("ressalva", d.ressalva());
        outbox.registrar("AnaliseDeCredito", analise.id(), d.resultado().evento(), dados);

        var decisao = new DecisaoDeCredito(analise.id(), analise.propostaId(), analise.condicoesVersao(),
                analise.resultado(), analise.ressalva(), analise.sugeridas());
        ouvintes.forEach(o -> o.creditoDecidido(decisao));
        return analise;
    }
}
