package br.com.financiamento.documentos.dominio;

import br.com.financiamento.shared.dominio.RegraVioladaException;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/** Envio (ADR 0010) e conferência (UC-0024) de um documento. */
class DocumentoTest {

    static final Instant AGORA = Instant.parse("2026-11-06T10:00:00Z");
    static final byte[] PDF = "%PDF-1.4 teste".getBytes(StandardCharsets.US_ASCII);

    Documento enviado() {
        return Documento.enviar(1L, "529.982.247-25", "identidade", "rg.pdf", "application/pdf", PDF, "chave", AGORA);
    }

    @Test
    void guardaOTitularSoComDigitosEAIntegridade() {
        var d = enviado();
        assertEquals("52998224725", d.titular());
        assertEquals(Documento.sha256(PDF), d.sha256());
        assertEquals(SituacaoDoDocumento.ENVIADO, d.situacao());
    }

    @Test
    void soPdfJpegOuPngConferidosPeloConteudo() {
        assertThrows(RegraVioladaException.class, () -> Documento.validar("identidade", "text/plain", PDF));
        assertThrows(RegraVioladaException.class, () -> Documento.validar("identidade", "image/png", PDF),
                "o conteúdo é PDF, não PNG");
        assertThrows(RegraVioladaException.class, () -> Documento.validar("identidade", "application/pdf", new byte[0]));
        assertThrows(RegraVioladaException.class, () -> Documento.validar("identidade", "application/pdf",
                new byte[(int) Documento.TAMANHO_MAXIMO + 1]));
    }

    @Test
    void conferenciaRepetidaNaoTemEfeitoEDiferenteERecusada() {
        var d = enviado();
        assertTrue(d.conferir(false, MotivoDaRecusa.ILEGIVEL, "foto tremida", AGORA));
        assertEquals("ilegível: foto tremida", d.motivoDescrito());
        assertFalse(d.conferir(false, MotivoDaRecusa.ILEGIVEL, "foto tremida", AGORA));
        assertThrows(RegraVioladaException.class, () -> d.conferir(true, null, null, AGORA));
    }

    @Test
    void recusaPedeMotivoEAceitoNaoESubstituido() {
        var d = enviado();
        assertThrows(RegraVioladaException.class, () -> d.conferir(false, null, null, AGORA));
        d.conferir(true, null, null, AGORA);
        assertThrows(RegraVioladaException.class, d::substituir);
    }
}
