package br.com.financiamento.avaliacao.aplicacao;

import br.com.financiamento.avaliacao.OuvinteDaAvaliacao;
import br.com.financiamento.avaliacao.ResultadoDeAvaliacao;
import br.com.financiamento.avaliacao.dominio.Avaliacao;
import br.com.financiamento.avaliacao.dominio.AvaliacaoRepository;
import br.com.financiamento.bem.BemFacade;
import br.com.financiamento.shared.validacao.ContextoDeDominio;
import br.com.financiamento.avaliacao.dominio.ErrosDaAvaliacao;
import br.com.financiamento.shared.outbox.Outbox;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;

/**
 * UC-0012, passo 2: o avaliador devolve valor e aceitação. A validade vem do
 * tipo de bem (RN-0023). Emite BemAvaliado ou BemRecusadoComoGarantia e entrega
 * o resultado a quem acompanha, na mesma transação (ADR 0009).
 */
@ApplicationScoped
public class RegistrarLaudoService {

    public record Laudo(boolean aceitoComoGarantia, BigDecimal valor, LocalDate avaliadaEm) {
    }

    @Inject
    AvaliacaoRepository avaliacoes;

    @Inject
    BemFacade bens;

    @Inject
    Outbox outbox;

    @Inject
    @Any
    Instance<OuvinteDaAvaliacao> ouvintes;

    @Transactional
    public Avaliacao executar(Long avaliacaoId, Laudo laudo) {
        // Cadeia de domínio (ADR 0011): a avaliação existe (o laudo repetido é tratado no agregado, sem efeito).
        var avaliacao = ContextoDeDominio.validar(avaliacaoId,
                ContextoDeDominio.carregar(avaliacoes::findByIdOptional, ErrosDaAvaliacao.AVALIACAO_NAO_ENCONTRADA));
        var tipo = avaliacao.tipoDeBem();
        var validaAte = bens.categoria(tipo).validadeDaAvaliacao(tipo, laudo.avaliadaEm());
        if (!avaliacao.concluir(laudo.aceitoComoGarantia(), laudo.valor(), laudo.avaliadaEm(), validaAte)) {
            return avaliacao; // laudo repetido: nenhum novo efeito
        }
        var dados = new HashMap<String, Object>();
        dados.put("propostaId", avaliacao.propostaId());
        dados.put("avaliadaEm", laudo.avaliadaEm().toString());
        if (avaliacao.valor() != null) dados.put("valor", avaliacao.valor());
        if (avaliacao.validaAte() != null) dados.put("validaAte", avaliacao.validaAte().toString());
        outbox.registrar("Avaliacao", avaliacao.id(),
                laudo.aceitoComoGarantia() ? "BemAvaliado" : "BemRecusadoComoGarantia", dados);

        var resultado = new ResultadoDeAvaliacao(avaliacao.id(), avaliacao.propostaId(), avaliacao.aceitoComoGarantia(),
                avaliacao.valor(), avaliacao.fonte(), avaliacao.avaliadaEm(), avaliacao.validaAte());
        ouvintes.forEach(o -> o.bemAvaliado(resultado));
        return avaliacao;
    }
}
