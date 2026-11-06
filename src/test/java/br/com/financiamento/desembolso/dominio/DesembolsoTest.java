package br.com.financiamento.desembolso.dominio;

import br.com.financiamento.bem.FormaDeLiberacao;
import br.com.financiamento.shared.validacao.ErroDeDominioException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** RN-0030 a RN-0032, FGTS da entrada e confirmação de pagamento. */
class DesembolsoTest {

    static final Instant AGORA = Instant.parse("2026-11-05T10:00:00Z");

    static BigDecimal r(String v) {
        return new BigDecimal(v);
    }

    Desembolso abrir(String financiado, String fgts, FormaDeLiberacao forma) {
        return Desembolso.abrir(9L, 3L, r(financiado), r(fgts), "12345678909", forma, true, AGORA);
    }

    List<String> nomes(Desembolso d) {
        return d.retirarEventos().stream().map(EventoRegistrado::nome).toList();
    }

    @Test
    void nadaAntesDaGarantiaConstituida_rn0030() {
        var erro = assertThrows(ErroDeDominioException.class, () -> Desembolso.abrir(9L, 3L, r("1000"), r("0"),
                "12345678909", FormaDeLiberacao.UNICA, false, AGORA));
        assertEquals("RN-0030", erro.falhas().getFirst().regra());
    }

    @Test
    void bemProntoTemUmaLiberacaoDoValorFinanciadoPagaUmaVez_rn0031() {
        var d = abrir("12600", "0", FormaDeLiberacao.UNICA);
        assertEquals(List.of("LiberacaoSolicitada"), nomes(d));
        var liberacao = d.liberacoes().getFirst();
        assertEquals("LIB-9-1", liberacao.protocolo());
        assertEquals(0, r("12600").compareTo(liberacao.valor()));

        assertTrue(d.pagamentoConfirmado("LIB-9-1", AGORA).isPresent());
        assertTrue(d.pagamentoConfirmado("LIB-9-1", AGORA).isEmpty(), "confirmação repetida não paga de novo");
        assertEquals(List.of("RecursoLiberado"), nomes(d));
        assertEquals(0, r("12600").compareTo(d.totalLiberado()));
    }

    @Test
    void fgtsNegadoEsperaAEntradaRecomposta_uc0022() {
        var d = abrir("400000", "50000", FormaDeLiberacao.UNICA);
        assertEquals(List.of("FgtsSolicitado"), nomes(d));
        assertTrue(d.liberacoes().isEmpty(), "a liberação espera o FGTS");
        d.fgtsRespondido(false, "saldo insuficiente", AGORA);
        assertEquals(SituacaoDoFgts.NEGADO, d.fgts());
        assertTrue(d.liberacoes().isEmpty());
        d.entradaRecomposta(AGORA);
        assertEquals(List.of("FgtsNegado", "LiberacaoSolicitada"), nomes(d));
        d.fgtsRespondido(true, null, AGORA);
        assertEquals(SituacaoDoFgts.RECOMPOSTO, d.fgts(), "retorno atrasado do FGTS não tem efeito");
    }

    @Test
    void fgtsLiberadoSegueParaALiberacao() {
        var d = abrir("400000", "50000", FormaDeLiberacao.UNICA);
        d.fgtsRespondido(true, null, AGORA);
        assertEquals(List.of("FgtsSolicitado", "FgtsLiberado", "LiberacaoSolicitada"), nomes(d));
    }

    @Test
    void parcelasPorMedicaoSomamOValorFinanciado_rn0032() {
        var d = abrir("400000", "0", FormaDeLiberacao.POR_MEDICAO);
        assertTrue(d.liberacoes().isEmpty(), "na planta, espera a medição");
        d.medicaoRealizada(r("30"), AGORA);
        d.medicaoRealizada(r("30"), AGORA); // sem avanço, sem parcela
        d.medicaoRealizada(r("70"), AGORA);
        d.medicaoRealizada(r("100"), AGORA);
        var valores = d.liberacoes().stream().map(l -> l.valor().stripTrailingZeros().toPlainString()).toList();
        assertEquals(List.of("120000", "160000", "120000"), valores);
        assertTrue(d.liberacoes().getLast().ultima());
        assertFalse(d.liberacoes().getFirst().ultima());
    }

    @Test
    void medicaoSoNoImovelNaPlanta() {
        var d = abrir("1000", "0", FormaDeLiberacao.UNICA);
        assertThrows(ErroDeDominioException.class, () -> d.medicaoRealizada(r("50"), AGORA));
    }
}
