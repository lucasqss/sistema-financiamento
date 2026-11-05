package br.com.financiamento.originacao.dominio;

import br.com.financiamento.bem.CategoriaDeBem;
import br.com.financiamento.bem.veiculo.VeiculoParaTeste;
import br.com.financiamento.originacao.dominio.documento.DocumentoEntregue;
import br.com.financiamento.shared.dominio.RegraVioladaException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static br.com.financiamento.originacao.dominio.PropostaParaTeste.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Invariantes do agregado AGR-0001. */
class PropostaTest {

    final CategoriaDeBem veiculo = VeiculoParaTeste.plugin();

    Proposta enquadrada() {
        return completa("MOTO_USADA", veiculo, new BigDecimal("20000"), new BigDecimal("7400"), 36,
                new BigDecimal("900"), 2022, null);
    }

    @Test
    void nasceEmRascunhoEEmitePropostaIniciada() {
        var p = rascunho("CARRO_NOVO");
        assertEquals(EstadoProposta.RASCUNHO, p.estado());
        var eventos = p.retirarEventos();
        assertEquals("PropostaIniciada", eventos.getFirst().nome());
        assertEquals("CARRO_NOVO", eventos.getFirst().dados().get("tipoDeBem"));
        assertTrue(p.retirarEventos().isEmpty(), "eventos são retirados uma vez só");
    }

    @Test
    void submeteEnquadradaEEmitePropostaSubmetida() {
        var p = enquadrada();
        p.retirarEventos();
        p.submeter(enquadrar(p, veiculo));
        assertEquals(EstadoProposta.EM_ANALISE, p.estado());
        assertEquals(List.of("PropostaSubmetida"), p.retirarEventos().stream().map(EventoRegistrado::nome).toList());
    }

    @Test
    void naoSubmeteForaDoEnquadramentoEContinuaEmRascunho() {
        var p = rascunho("CARRO_NOVO");
        var erro = assertThrows(RegraVioladaException.class, () -> p.submeter(enquadrar(p, veiculo)));
        assertEquals("RN-0018", erro.violacoes().getFirst().regra());
        assertEquals(EstadoProposta.RASCUNHO, p.estado());
    }

    @Test
    void emAnaliseNaoAceitaAlteracao() {
        var p = enquadrada();
        p.submeter(enquadrar(p, veiculo));
        var erro = assertThrows(RegraVioladaException.class,
                () -> p.informarCondicoes(BigDecimal.ONE, BigDecimal.ZERO, 36, BigDecimal.ONE));
        assertEquals("AGR-0001", erro.violacoes().getFirst().regra());
    }

    @Test
    void emAnaliseNaoPodeSerCanceladaPeloCicloDeVida() {
        var p = enquadrada();
        p.submeter(enquadrar(p, veiculo));
        var erro = assertThrows(RegraVioladaException.class, p::cancelar);
        assertEquals("CV-0001", erro.violacoes().getFirst().regra());
    }

    @Test
    void cancelaORascunhoEEmitePropostaCancelada() {
        var p = rascunho("CARRO_NOVO");
        p.retirarEventos();
        p.cancelar();
        assertEquals(EstadoProposta.CANCELADA, p.estado());
        assertEquals("PropostaCancelada", p.retirarEventos().getFirst().nome());
    }

    @Test
    void cadaMudancaDeCondicoesEUmaNovaVersao() {
        var p = enquadrada();
        assertEquals(1, p.condicoes().versao());
        p.informarCondicoes(new BigDecimal("8000"), BigDecimal.ZERO, 36, new BigDecimal("850"));
        assertEquals(2, p.condicoes().versao());
    }

    @Test
    void mesmoDocumentoNaoEEntregueDuasVezes() {
        var p = enquadrada();
        var erro = assertThrows(RegraVioladaException.class,
                () -> p.registrarDocumento(new DocumentoEntregue("bem", "crlv")));
        assertEquals("RN-0009", erro.violacoes().getFirst().regra());
    }

    @Test
    void estadosAtivosSeguramOBem() {
        assertTrue(EstadoProposta.APROVADA.ativa());
        assertTrue(EstadoProposta.EM_CONTRAPROPOSTA.ativa());
        assertTrue(!EstadoProposta.CONTRATADA.ativa() && !EstadoProposta.CANCELADA.ativa());
    }
}
