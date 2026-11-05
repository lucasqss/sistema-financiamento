package br.com.financiamento.desembolso.aplicacao;

import br.com.financiamento.bem.FormaDeLiberacao;
import br.com.financiamento.contratacao.ContratacaoFacade;
import br.com.financiamento.desembolso.dominio.Desembolso;
import br.com.financiamento.desembolso.dominio.DesembolsoRepository;
import br.com.financiamento.shared.dominio.NaoEncontradoException;
import br.com.financiamento.shared.dominio.RegraVioladaException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.Clock;

/**
 * UC-0021 a UC-0023: abre o desembolso com a garantia constituída, trata o FGTS da
 * entrada, pede as parcelas da obra a cada medição e confirma os pagamentos,
 * que movem o contrato até formalizado.
 */
@ApplicationScoped
public class LiberarRecursoService {

    @Inject
    DesembolsoRepository repositorio;

    @Inject
    Desembolsos desembolsos;

    @Inject
    ContratacaoFacade contratacao;

    @Inject
    Clock relogio;

    /** GarantiaRegistrada dispara UC-0021 (bem pronto) ou abre a liberação por medição (na planta). */
    @Transactional
    public void garantiaConstituida(Long contratoId) {
        if (repositorio.doContrato(contratoId).isPresent()) return;
        var c = contratacao.paraDesembolso(contratoId);
        var d = Desembolso.abrir(c.contratoId(), c.propostaId(), c.valorFinanciado(), c.entradaFgts(),
                c.documentoDoVendedor(), c.liberacao(), c.garantiaConstituida(), relogio.instant());
        desembolsos.salvar(d);
    }

    /** Retorno do sistema do FGTS (ATR-0012). */
    @Transactional
    public Desembolso fgtsRespondido(Long desembolsoId, boolean liberado, String motivo) {
        var d = desembolsos.buscar(desembolsoId);
        d.fgtsRespondido(liberado, motivo, relogio.instant());
        desembolsos.salvar(d);
        return d;
    }

    /** UC-0022. */
    @Transactional
    public Desembolso entradaRecomposta(Long desembolsoId) {
        var d = desembolsos.buscar(desembolsoId);
        d.entradaRecomposta(relogio.instant());
        desembolsos.salvar(d);
        return d;
    }

    /**
     * UC-0023, a partir da medição (UC-0013). A medição só cabe ao contrato na
     * planta com a garantia constituída: senão é recusada, e desfeita junto.
     */
    @Transactional
    public void medicaoRealizada(Long contratoId, BigDecimal percentualAcumulado) {
        var d = repositorio.doContrato(contratoId).orElseThrow(() -> new RegraVioladaException("UC-0013",
                "Medição só para contrato de imóvel na planta com a garantia constituída."));
        d.medicaoRealizada(percentualAcumulado, relogio.instant());
        desembolsos.salvar(d);
        contratacao.medicaoRealizada(contratoId);
    }

    /** O vendedor recebeu (retorno do pagamento, idempotente): o contrato avança (EVT-0033 ou EVT-0034). */
    @Transactional
    public Desembolso pagamentoConfirmado(String protocolo) {
        var d = Desembolso.contratoDoProtocolo(protocolo).flatMap(repositorio::doContrato)
                .orElseThrow(() -> new NaoEncontradoException("Protocolo de liberação " + protocolo + " não é nosso."));
        var paga = d.pagamentoConfirmado(protocolo, relogio.instant());
        if (paga.isEmpty()) return d;
        desembolsos.salvar(d);
        if (d.forma() == FormaDeLiberacao.UNICA) contratacao.recursoLiberado(d.contratoId());
        else contratacao.parcelaDaObraLiberada(d.contratoId(), paga.get().ultima());
        return d;
    }

    public java.util.Optional<Desembolso> doContrato(Long contratoId) {
        return repositorio.doContrato(contratoId);
    }
}
