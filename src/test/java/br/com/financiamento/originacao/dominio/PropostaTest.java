package br.com.financiamento.originacao.dominio;

import br.com.financiamento.bem.CategoriaDeBem;
import br.com.financiamento.bem.veiculo.VeiculoParaTeste;
import br.com.financiamento.shared.validacao.ErroDeDominioException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static br.com.financiamento.originacao.dominio.PropostaParaTeste.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
        var erro = assertThrows(ErroDeDominioException.class, () -> p.submeter(enquadrar(p, veiculo)));
        assertEquals("RN-0018", erro.falhas().getFirst().regra());
        assertEquals(EstadoProposta.RASCUNHO, p.estado());
    }

    @Test
    void emAnaliseNaoAceitaAlteracao() {
        var p = enquadrada();
        p.submeter(enquadrar(p, veiculo));
        var erro = assertThrows(ErroDeDominioException.class,
                () -> p.informarCondicoes(BigDecimal.ONE, BigDecimal.ZERO, 36, BigDecimal.ONE));
        assertEquals("AGR-0001", erro.falhas().getFirst().regra());
    }

    @Test
    void emAnaliseNaoPodeSerCanceladaPeloCicloDeVida() {
        var p = enquadrada();
        p.submeter(enquadrar(p, veiculo));
        var erro = assertThrows(ErroDeDominioException.class, p::cancelar);
        assertEquals("CV-0001", erro.falhas().getFirst().regra());
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
    void reenvioSubstituiODocumentoAindaNaoAceito_rn0009() {
        var p = enquadrada();
        int antes = p.documentos().size();
        p.documentoRecebido(900L, "bem", "crlv");
        assertEquals(antes, p.documentos().size(), "o reenvio substitui, não duplica");
        assertTrue(p.documentos().stream().anyMatch(d -> Long.valueOf(900L).equals(d.documentoId())));
        var erro = assertThrows(ErroDeDominioException.class, () -> p.documentoRecebido(901L, "99999999999", "identidade"));
        assertEquals("RN-0009", erro.falhas().getFirst().regra());
    }

    @Test
    void conferenciaSoDepoisDaSubmissaoERecusaDaPendencia_uc0024() {
        var p = enquadrada();
        var id = p.documentos().getFirst().documentoId();
        assertThrows(ErroDeDominioException.class, () -> p.documentoConferido(id, true, null), "em rascunho");
        p.submeter(enquadrar(p, veiculo));
        p.retirarEventos();
        p.documentoConferido(id, false, "ilegível");
        assertEquals(EstadoProposta.COM_PENDENCIA, p.estado());
        assertTrue(p.pendencia().contains("ilegível"));
        assertEquals(List.of("PropostaComPendencia"), p.retirarEventos().stream().map(EventoRegistrado::nome).toList());
        assertTrue(enquadrar(p, veiculo).falhas().stream().anyMatch(v -> v.regra().equals("RN-0009")),
                "o recusado conta como não entregue até o reenvio");
        var recusado = p.documentos().getFirst();
        p.documentoRecebido(902L, recusado.titular(), recusado.tipo());
        p.resolverPendencia(enquadrar(p, veiculo));
        assertEquals(EstadoProposta.EM_ANALISE, p.estado());
        assertFalse(p.documentosAceitos());
        aceitarDocumentos(p);
        assertTrue(p.documentosAceitos());
        var aceito = p.documentos().getFirst();
        assertThrows(ErroDeDominioException.class, () -> p.documentoRecebido(903L, aceito.titular(), aceito.tipo()),
                "documento aceito não é reenviado");
    }

    @Test
    void estadosAtivosSeguramOBem() {
        assertTrue(EstadoProposta.APROVADA.ativa());
        assertTrue(EstadoProposta.EM_CONTRAPROPOSTA.ativa());
        assertTrue(!EstadoProposta.CONTRATADA.ativa() && !EstadoProposta.CANCELADA.ativa());
    }
}
