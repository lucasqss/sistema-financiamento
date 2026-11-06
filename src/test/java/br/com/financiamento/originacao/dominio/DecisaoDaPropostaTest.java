package br.com.financiamento.originacao.dominio;

import br.com.financiamento.bem.CategoriaDeBem;
import br.com.financiamento.bem.veiculo.VeiculoParaTeste;
import br.com.financiamento.credito.CondicoesSugeridas;
import br.com.financiamento.credito.ResultadoDeCredito;
import br.com.financiamento.originacao.dominio.resultado.AvaliacaoDaProposta;
import br.com.financiamento.originacao.dominio.resultado.CreditoDaProposta;
import br.com.financiamento.shared.dominio.RegraVioladaException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static br.com.financiamento.originacao.dominio.EstadoProposta.*;
import static br.com.financiamento.originacao.dominio.PropostaParaTeste.*;
import static org.junit.jupiter.api.Assertions.*;

/** UC-0006 a UC-0009 e os prazos (RN-0020 a RN-0022) no agregado. */
class DecisaoDaPropostaTest {

    final CategoriaDeBem veiculo = VeiculoParaTeste.plugin();

    static BigDecimal r(String v) {
        return new BigDecimal(v);
    }

    /** Moto usada de R$ 20.000 com entrada de R$ 7.400, submetida. */
    Proposta emAnalise() {
        var p = completa("MOTO_USADA", veiculo, r("20000"), r("7400"), 36, r("900"), 2022, null);
        p.submeter(enquadrar(p, veiculo));
        aceitarDocumentos(p);
        p.analiseDeCreditoSolicitada();
        p.avaliacaoSolicitada();
        p.retirarEventos();
        return p;
    }

    void decidir(Proposta p, LocalDate hoje) {
        p.decidir(RegrasDeEnquadramento.avaliar(p, veiculo, hoje, false), hoje, veiculo.validadeDaContraproposta());
    }

    void credito(Proposta p, ResultadoDeCredito resultado) {
        p.registrarCredito(new CreditoDaProposta(resultado, p.condicoes().versao(), null, null));
        decidir(p, HOJE);
    }

    void avaliacao(Proposta p, String valor) {
        p.registrarAvaliacao(new AvaliacaoDaProposta(true, r(valor), "tabela de referência", HOJE,
                veiculo.validadeDaAvaliacao(p.tipoDeBem(), HOJE)));
        decidir(p, HOJE);
    }

    List<String> eventos(Proposta p) {
        return p.retirarEventos().stream().map(EventoRegistrado::nome).toList();
    }

    Proposta aprovada() {
        var p = emAnalise();
        credito(p, ResultadoDeCredito.APROVADO);
        avaliacao(p, "20000");
        assertEquals(APROVADA, p.estado());
        p.retirarEventos();
        return p;
    }

    @Test
    void esperaOsDoisResultadosEAprova_rn0019() {
        var p = emAnalise();
        credito(p, ResultadoDeCredito.APROVADO);
        assertEquals(EM_ANALISE, p.estado(), "sem avaliação, espera");
        avaliacao(p, "20000");
        assertEquals(APROVADA, p.estado());
        assertEquals(HOJE.plusDays(30), p.aprovacaoValidaAte(), "RN-0021");
        assertEquals(List.of("PropostaAprovada"), eventos(p));
    }

    @Test
    void creditoReprovadoReprova() {
        var p = emAnalise();
        credito(p, ResultadoDeCredito.REPROVADO);
        assertEquals(REPROVADA, p.estado());
        assertEquals("crédito reprovado", p.retirarEventos().getFirst().dados().get("motivo"));
    }

    @Test
    void bemRecusadoComoGarantiaReprova() {
        var p = emAnalise();
        p.registrarAvaliacao(new AvaliacaoDaProposta(false, null, "tabela de referência", HOJE, null));
        decidir(p, HOJE);
        assertEquals(REPROVADA, p.estado());
    }

    @Test
    void cotaEstouradaComOValorDeAvaliacaoDaPendenciaEResolveComNovaEntrada_rn0006() {
        var p = emAnalise();
        credito(p, ResultadoDeCredito.APROVADO);
        avaliacao(p, "15000"); // referência 15.000: financiado de 12.600 passa de 70% (10.500)
        assertEquals(COM_PENDENCIA, p.estado());
        assertTrue(p.pendencia().contains("RN-0006") && p.pendencia().contains("9500.00"), p.pendencia());

        assertThrows(RegraVioladaException.class, () -> p.resolverPendencia(enquadrar(p, veiculo)),
                "sem recompor a entrada, continua fora do enquadramento");

        p.informarCondicoes(r("9500"), BigDecimal.ZERO, 36, r("700")); // UC-0008: entrada recomposta
        p.resolverPendencia(enquadrar(p, veiculo));
        assertEquals(EM_ANALISE, p.estado());
        assertTrue(p.precisaDeAnaliseDeCredito(), "condições novas pedem nova análise de crédito");
        assertFalse(p.precisaDeAvaliacao(HOJE), "a avaliação continua válida");

        decidir(p, HOJE);
        assertEquals(EM_ANALISE, p.estado(), "o crédito da versão 1 não vale para a versão 2");
        p.analiseDeCreditoSolicitada();
        credito(p, ResultadoDeCredito.APROVADO);
        assertEquals(APROVADA, p.estado());
        assertEquals(List.of("PropostaComPendencia", "PendenciaResolvida", "PropostaAprovada"), eventos(p));
    }

    @Test
    void creditoDeVersaoAntigaNaoValeParaAsCondicoesNovas() {
        var p = emAnalise();
        assertFalse(p.registrarCredito(new CreditoDaProposta(ResultadoDeCredito.APROVADO, 99, null, null)));
        assertNull(p.credito());
    }

    @Test
    void ressalvaEmiteContrapropostaQueAceitaViraNovaVersaoAprovada_rn0020() {
        var p = emAnalise();
        avaliacao(p, "20000");
        p.registrarCredito(new CreditoDaProposta(ResultadoDeCredito.APROVADO_COM_RESSALVA, 1, "entrada maior",
                new CondicoesSugeridas(r("9000"), BigDecimal.ZERO, 36, r("800"))));
        decidir(p, HOJE);
        assertEquals(EM_CONTRAPROPOSTA, p.estado());
        assertEquals(HOJE.plusDays(5), p.contraproposta().validaAte());

        p.aceitarContraproposta();
        assertEquals(EM_ANALISE, p.estado());
        assertEquals(2, p.condicoes().versao());
        assertEquals(0, r("9000").compareTo(p.condicoes().entradaPropria()));
        assertFalse(p.precisaDeAnaliseDeCredito(), "as condições da contraproposta já vêm aprovadas");
        decidir(p, HOJE);
        assertEquals(APROVADA, p.estado());
        assertEquals(List.of("ContrapropostaEmitida", "ContrapropostaAceita", "PropostaAprovada"), eventos(p));
    }

    @Test
    void contrapropostaVencidaReprova() {
        var p = emAnalise();
        p.registrarCredito(new CreditoDaProposta(ResultadoDeCredito.APROVADO_COM_RESSALVA, 1, "prazo menor",
                new CondicoesSugeridas(r("7400"), BigDecimal.ZERO, 24, r("1000"))));
        decidir(p, HOJE);
        assertFalse(p.recusarContrapropostaSeVencida(HOJE.plusDays(5)), "vale até o 5º dia");
        assertTrue(p.recusarContrapropostaSeVencida(HOJE.plusDays(6)));
        assertEquals(REPROVADA, p.estado());
    }

    @Test
    void aprovadaSemContratoExpiraDepoisDe30Dias_rn0021() {
        var p = aprovada();
        assertFalse(p.expirarSeVencida(HOJE.plusDays(30)));
        assertTrue(p.expirarSeVencida(HOJE.plusDays(31)));
        assertEquals(EXPIRADA, p.estado());
        assertEquals(List.of("PropostaExpirada"), eventos(p));
    }

    @Test
    void avaliacaoVencidaNaAprovadaDaPendenciaEPedeNovaAvaliacao_rn0022() {
        var p = aprovada(); // moto usada: avaliação vale até o fim do mês da consulta
        assertFalse(p.pendenciaSeAvaliacaoVencida(LocalDate.of(2026, 11, 30)));
        assertTrue(p.pendenciaSeAvaliacaoVencida(LocalDate.of(2026, 12, 1)));
        assertEquals(COM_PENDENCIA, p.estado());
        assertTrue(p.precisaDeAvaliacao(LocalDate.of(2026, 12, 1)));
        assertEquals(List.of("AvaliacaoVencida"), eventos(p));
    }

    @Test
    void resultadoQueChegaAPropostaEncerradaNaoAMuda() {
        var p = rascunho("MOTO_USADA");
        p.cancelar();
        assertFalse(p.registrarAvaliacao(new AvaliacaoDaProposta(true, r("1"), "x", HOJE, HOJE)));
    }

    @Test
    void contrataSoAAprovadaDentroDaValidadeEComAvaliacaoValida_uc0014() {
        assertNotNull(emAnalise().impedimentoParaContratar(HOJE));

        var p = aprovada();
        assertNull(p.impedimentoParaContratar(HOJE));
        p.registrarContrato(77L, HOJE);
        assertEquals(CONTRATADA, p.estado());
        assertEquals(77L, p.contratoId());
        assertThrows(RegraVioladaException.class, () -> p.registrarContrato(78L, HOJE), "um contrato por proposta");

        var expirada = aprovada();
        assertTrue(expirada.impedimentoParaContratar(HOJE.plusDays(31)).startsWith("RN-0021"));
        assertEquals(EXPIRADA, expirada.estado());

        var vencida = aprovada();
        assertTrue(vencida.impedimentoParaContratar(LocalDate.of(2026, 12, 1)).startsWith("RN-0022"));
        assertEquals(COM_PENDENCIA, vencida.estado());
    }
}
