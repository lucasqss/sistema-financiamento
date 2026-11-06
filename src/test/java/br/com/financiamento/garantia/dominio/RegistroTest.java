package br.com.financiamento.garantia.dominio;

import br.com.financiamento.bem.TipoDeBem;
import br.com.financiamento.shared.validacao.ErroDeDominioException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import br.com.financiamento.garantia.dominio.Registro.Efeito;
import br.com.financiamento.garantia.dominio.retorno.ResultadoDoOrgao;
import static org.junit.jupiter.api.Assertions.*;

/** RN-0029: o retorno do órgão é classificado como fato, nunca recusado. */
class RegistroTest {

    static final Instant AGORA = Instant.parse("2026-11-05T10:00:00Z");

    Registro solicitado() {
        var r = Registro.solicitar(7L, 3L, TipoDeBem.de("CARRO_USADO"), "9BW-ZZZ-377", "órgão de trânsito");
        assertEquals(List.of("RegistroSolicitado"), nomes(r));
        return r;
    }

    List<String> nomes(Registro r) {
        return r.retirarEventos().stream().map(EventoRegistrado::nome).toList();
    }

    @Test
    void protocoloNossoPorTentativa() {
        var r = solicitado();
        assertEquals("GAR-7-1", r.protocolo());
        assertEquals(7L, Registro.contratoDoProtocolo("GAR-7-1").orElseThrow());
        assertTrue(Registro.contratoDoProtocolo("XYZ").isEmpty());
    }

    @Test
    void registradoComOBemDoContratoConstituiAGarantiaERepetidoNaoTemEfeito() {
        var r = solicitado();
        assertEquals(Efeito.CONSTITUIDA, r.classificar("GAR-7-1", ResultadoDoOrgao.REGISTRADO, "9bwzzz377", null, AGORA));
        assertEquals(SituacaoDoRegistro.CONSTITUIDA, r.situacao());
        assertEquals(List.of("GarantiaRegistrada"), nomes(r));
        assertEquals(Efeito.REPETIDO, r.classificar("GAR-7-1", ResultadoDoOrgao.REGISTRADO, "9bwzzz377", null, AGORA));
        assertEquals(1, r.retornos().size());
    }

    @Test
    void registradoComOutroBemViraPendenciaParaOAnalista() {
        var r = solicitado();
        assertEquals(Efeito.PENDENCIA_DE_IDENTIFICACAO, r.classificar("GAR-7-1", ResultadoDoOrgao.REGISTRADO, "OUTRO-1", null, AGORA));
        assertTrue(r.retirarEventos().isEmpty());
        r.reenviar();
        assertEquals("GAR-7-2", r.protocolo());
        assertEquals(SituacaoDoRegistro.SOLICITADO, r.situacao());
    }

    @Test
    void retornoDeTentativaAntigaOuForaDeOrdemEGuardadoSemEfeito() {
        var r = solicitado();
        r.classificar("GAR-7-1", ResultadoDoOrgao.RECUSADO, null, "matrícula ilegível", AGORA);
        r.reenviar();
        nomes(r);
        assertEquals(Efeito.SEM_EFEITO, r.classificar("GAR-7-1", ResultadoDoOrgao.REGISTRADO, "9BWZZZ377", null, AGORA));
        assertEquals(SituacaoDoRegistro.SOLICITADO, r.situacao());
        assertEquals(Efeito.CONSTITUIDA, r.classificar("GAR-7-2", ResultadoDoOrgao.REGISTRADO, "9BWZZZ377", null, AGORA));
        assertEquals(Efeito.SEM_EFEITO, r.classificar("GAR-7-2", ResultadoDoOrgao.RECUSADO, null, "tarde", AGORA), "depois de constituída");
        assertEquals(SituacaoDoRegistro.CONSTITUIDA, r.situacao());
    }

    @Test
    void exigenciaCumpridaReenviaComNovoProtocolo_uc0020() {
        var r = solicitado();
        assertEquals(Efeito.EXIGENCIA, r.classificar("GAR-7-1", ResultadoDoOrgao.EXIGENCIA, null, "certidão atualizada", AGORA));
        assertEquals("certidão atualizada", r.observacao());
        r.cumprirExigencia();
        assertEquals("GAR-7-2", r.protocolo());
        assertEquals(List.of("ExigenciaEmitida", "ExigenciaCumprida", "RegistroSolicitado"), nomes(r));
    }

    @Test
    void recusadoSemSolucaoEDesfeito_uc0019() {
        var r = solicitado();
        assertThrows(ErroDeDominioException.class, r::desfazer);
        r.classificar("GAR-7-1", ResultadoDoOrgao.RECUSADO, null, "vendedor sem poderes", AGORA);
        r.desfazer();
        assertEquals(SituacaoDoRegistro.DESFEITO, r.situacao());
        assertThrows(ErroDeDominioException.class, r::reenviar);
    }
}
