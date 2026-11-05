package br.com.financiamento.contratacao.dominio;

import br.com.financiamento.bem.TipoDeBem;
import br.com.financiamento.contratacao.dominio.assinatura.Signatario;
import br.com.financiamento.contratacao.dominio.condicoes.CondicoesFinais;
import br.com.financiamento.contratacao.dominio.seguro.Seguro;
import br.com.financiamento.shared.dominio.RegraVioladaException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static br.com.financiamento.DocumentosParaTeste.cpf;
import static org.junit.jupiter.api.Assertions.*;

/** Invariantes do agregado AGR-0002 e a preparação da emissão. */
class ContratoTest {

    static final Instant AGORA = Instant.parse("2026-11-05T10:00:00Z");
    static final String ANA = cpf(1), BRUNO = cpf(2), VENDEDOR = cpf(3);

    PreparoDoContrato preparo() {
        var p = PreparoDoContrato.aceitar(10L, TipoDeBem.de("RESIDENCIAL_PRONTO"), 1, List.of("MIP", "DFI"),
                List.of("prestamista"), List.of(), null, AGORA);
        p.retirarEventos();
        return p;
    }

    CondicoesFinais condicoes() {
        var v = new BigDecimal("1000");
        return new CondicoesFinais(1, "MAT-1", v, v, v, BigDecimal.ZERO, v, 120, v);
    }

    Contrato emitido() {
        var seguro = new Seguro("MIP", true);
        seguro.contratar("AP-1");
        var c = Contrato.emitir(10L, TipoDeBem.de("RESIDENCIAL_PRONTO"), VENDEDOR, condicoes(), List.of(seguro),
                List.of(new Signatario("PROPONENTE", ANA, "Ana"), new Signatario("VENDEDOR", VENDEDOR, "Vendedor"),
                        new Signatario("CONJUGE", BRUNO, "Bruno")), null, AGORA);
        c.retirarEventos();
        return c;
    }

    @Test
    void segurosContratadosSoComTodasAsApolices_rn0025() {
        var p = preparo();
        assertTrue(p.registrarApolice("MIP", "AP-1"));
        assertTrue(p.registrarApolice("DFI", "AP-2"));
        assertTrue(p.retirarEventos().isEmpty(), "falta o prestamista escolhido");
        assertFalse(p.registrarApolice("mip", "AP-1"), "a mesma apólice repetida não tem efeito");
        assertThrows(RegraVioladaException.class, () -> p.registrarApolice("MIP", "AP-9"));
        assertThrows(RegraVioladaException.class, () -> p.registrarApolice("seguro do bem", "AP-3"));
        p.registrarApolice("prestamista", "AP-4");
        assertTrue(p.segurosContratados());
        assertEquals("SegurosContratados", p.retirarEventos().getFirst().nome());
    }

    @Test
    void naoEmiteSemOsSegurosExigidos_rn0025() {
        var erro = assertThrows(RegraVioladaException.class, () -> Contrato.emitir(10L, TipoDeBem.de("CARRO_NOVO"),
                VENDEDOR, condicoes(), List.of(new Seguro("seguro do bem", true)),
                List.of(new Signatario("PROPONENTE", ANA, "Ana")), null, AGORA));
        assertEquals("RN-0025", erro.violacoes().getFirst().regra());
    }

    @Test
    void ficaAssinadoSoComTodasAsAssinaturas_rn0026() {
        var c = emitido();
        assertEquals(EstadoContrato.AGUARDANDO_ASSINATURA, c.estado());
        assertTrue(c.assinar(ANA, AGORA));
        assertFalse(c.assinar(ANA, AGORA), "assinatura repetida não tem efeito");
        c.assinar(VENDEDOR, AGORA);
        assertEquals(EstadoContrato.AGUARDANDO_ASSINATURA, c.estado());
        c.assinar(BRUNO, AGORA);
        assertEquals(EstadoContrato.ASSINADO, c.estado());
        assertEquals(List.of("ContratoAssinado"), c.retirarEventos().stream().map(EventoRegistrado::nome).toList());
    }

    @Test
    void quemNaoESignatarioNaoAssina() {
        var erro = assertThrows(RegraVioladaException.class, () -> emitido().assinar(cpf(99), AGORA));
        assertEquals("RN-0026", erro.violacoes().getFirst().regra());
    }

    @Test
    void desistenciaSoAntesDeAssinar_uc0016() {
        var c = emitido();
        c.cancelar("desistência");
        assertEquals(EstadoContrato.CANCELADO, c.estado());

        var assinado = emitido();
        List.of(ANA, VENDEDOR, BRUNO).forEach(d -> assinado.assinar(d, AGORA));
        var erro = assertThrows(RegraVioladaException.class, () -> assinado.cancelar("desistência"));
        assertEquals("CV-0002", erro.violacoes().getFirst().regra());
    }

    @Test
    void registroDaGarantiaMoveOContratoSemPublicarEventosDaGarantia() {
        var c = emitido();
        List.of(ANA, VENDEDOR, BRUNO).forEach(d -> c.assinar(d, AGORA));
        c.retirarEventos();
        c.registroSolicitado("cartório de registro de imóveis", "GAR-1-1");
        assertEquals(EstadoContrato.EM_REGISTRO, c.estado());
        c.exigenciaEmitida("certidão");
        assertEquals(EstadoContrato.EM_EXIGENCIA, c.estado());
        c.exigenciaCumprida("GAR-1-2");
        c.registroRecusado("vendedor sem poderes");
        assertEquals(EstadoContrato.REGISTRO_RECUSADO, c.estado());
        c.registroSolicitado("cartório de registro de imóveis", "GAR-1-3");
        c.garantiaRegistrada("GAR-1-3");
        assertEquals(EstadoContrato.GARANTIA_CONSTITUIDA, c.estado());
        assertEquals("GAR-1-3", c.protocoloDaGarantia());
        assertTrue(c.retirarEventos().isEmpty(), "EVT-0027 a EVT-0031 são da Garantia");
        assertThrows(RegraVioladaException.class, () -> c.garantiaRegistrada("GAR-1-3"), "CV-0002");
    }

    /** Contrato na planta, com a garantia já constituída. */
    Contrato naPlantaComGarantia(java.time.LocalDate previsao) {
        var seguro = new Seguro("MIP", true);
        seguro.contratar("AP-1");
        var c = Contrato.emitir(11L, TipoDeBem.de("RESIDENCIAL_NA_PLANTA"), VENDEDOR, condicoes(), List.of(seguro),
                List.of(new Signatario("PROPONENTE", ANA, "Ana")), new CronogramaDaObra(previsao), AGORA);
        c.assinar(ANA, AGORA);
        c.registroSolicitado("cartório de registro de imóveis", "GAR-11-1");
        c.garantiaRegistrada("GAR-11-1");
        c.retirarEventos();
        return c;
    }

    @Test
    void parcelasDaObraAteFormalizar_cv0002() {
        var c = naPlantaComGarantia(java.time.LocalDate.of(2027, 6, 30));
        c.medicaoRealizada(); // primeira medição: ainda com a garantia recém-constituída
        c.parcelaDaObraLiberada(false);
        assertEquals(EstadoContrato.EM_OBRA, c.estado());
        c.medicaoRealizada();
        c.parcelaDaObraLiberada(false); // intermediária: continua em obra
        assertEquals(EstadoContrato.EM_OBRA, c.estado());
        c.parcelaDaObraLiberada(true);
        assertEquals(EstadoContrato.FORMALIZADO, c.estado());
        assertTrue(c.retirarEventos().isEmpty(), "eventos da liberação são do Desembolso");
    }

    @Test
    void prorrogacaoDaObra_exemploDoModelo_rn0027() {
        var c = naPlantaComGarantia(java.time.LocalDate.of(2027, 6, 30));
        c.parcelaDaObraLiberada(false);
        assertThrows(RegraVioladaException.class, () -> naPlantaComGarantia(java.time.LocalDate.of(2027, 6, 30))
                .pedirProrrogacao(java.time.LocalDate.of(2027, 7, 30)), "fora de obra não prorroga (CV-0002)");

        assertTrue(c.pedirProrrogacao(java.time.LocalDate.of(2027, 6, 30).plusDays(90)));
        assertTrue(c.pedirProrrogacao(java.time.LocalDate.of(2027, 6, 30).plusDays(150)));
        assertEquals(150, c.cronograma().diasProrrogados());
        assertFalse(c.pedirProrrogacao(java.time.LocalDate.of(2027, 6, 30).plusDays(195)), "195 dias: pendência");
        assertEquals(150, c.cronograma().diasProrrogados());
        assertEquals(java.time.LocalDate.of(2027, 6, 30).plusDays(195), c.cronograma().pendente());
        c.decidirProrrogacao(true);
        assertEquals(195, c.cronograma().diasProrrogados());
        assertEquals(List.of("PrazoDaObraProrrogado", "PrazoDaObraProrrogado", "PrazoDaObraProrrogado"),
                c.retirarEventos().stream().map(EventoRegistrado::nome).toList());
        assertThrows(RegraVioladaException.class, () -> c.decidirProrrogacao(true), "nada pendente");
    }
}
