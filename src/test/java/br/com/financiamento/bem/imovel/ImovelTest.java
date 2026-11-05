package br.com.financiamento.bem.imovel;

import br.com.financiamento.bem.DadosDoEnquadramento;
import br.com.financiamento.bem.FormaDoParticipante;
import br.com.financiamento.bem.TipoDeBem;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImovelTest {

    final Imovel imovel = new Imovel();
    static final LocalDate HOJE = LocalDate.of(2026, 11, 5);

    DadosDoEnquadramento dados(TipoDeBem tipo, int idade, int prazo, Boolean construcao, FormaDoParticipante forma) {
        return new DadosDoEnquadramento(tipo, HOJE, prazo, null, construcao, forma, HOJE.minusYears(idade));
    }

    @Test
    void rn0012_exemploDoModelo_60AnosPedindo420MesesNaoEnquadraEOMaximoE246() {
        var v = imovel.verificarRegrasProprias(dados(Imovel.RESIDENCIAL_PRONTO, 60, 420, null, FormaDoParticipante.PESSOA_FISICA));
        assertEquals("RN-0012", v.getFirst().regra());
        assertTrue(v.getFirst().mensagem().contains("246 meses"), v.getFirst().mensagem());
        assertTrue(imovel.verificarRegrasProprias(dados(Imovel.RESIDENCIAL_PRONTO, 60, 246, null, FormaDoParticipante.PESSOA_FISICA)).isEmpty());
    }

    @Test
    void rn0012_naoValeParaPessoaJuridica() {
        assertTrue(imovel.verificarRegrasProprias(dados(Imovel.COMERCIAL_PRONTO, 70, 240, null, FormaDoParticipante.PESSOA_JURIDICA)).isEmpty());
    }

    @Test
    void rn0014_terrenoSemConstrucaoAverbada() {
        assertTrue(imovel.verificarRegrasProprias(dados(Imovel.TERRENO_RURAL, 40, 120, false, FormaDoParticipante.PESSOA_FISICA)).isEmpty());
        assertEquals("RN-0014", imovel.verificarRegrasProprias(dados(Imovel.TERRENO_URBANO, 40, 120, true, FormaDoParticipante.PESSOA_FISICA)).getFirst().regra());
        assertEquals("RN-0014", imovel.verificarRegrasProprias(dados(Imovel.TERRENO_URBANO, 40, 120, null, FormaDoParticipante.PESSOA_FISICA)).getFirst().regra());
    }

    @Test
    void rn0016_terrenoRuralExigeCcirItrECar() {
        assertTrue(imovel.documentosDoBem(Imovel.TERRENO_RURAL).containsAll(java.util.List.of("CCIR", "ITR", "CAR")));
    }

    @Test
    void rn0020_contrapropostaVale15Dias() {
        assertEquals(15, imovel.validadeDaContraproposta().getDays());
    }

    @Test
    void rn0023_laudoVale180Dias() {
        assertEquals(HOJE.plusDays(180), imovel.validadeDaAvaliacao(Imovel.TERRENO_URBANO, HOJE));
        assertEquals("laudo de avaliador credenciado", imovel.fonteDaAvaliacao(Imovel.COMERCIAL_NA_PLANTA));
    }

    @Test
    void rn0028_orgaoDeRegistro() {
        assertEquals("cartório de registro de imóveis", imovel.orgaoDeRegistro(Imovel.TERRENO_RURAL));
    }
}
