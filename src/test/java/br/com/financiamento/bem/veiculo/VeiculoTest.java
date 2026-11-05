package br.com.financiamento.bem.veiculo;

import br.com.financiamento.bem.DadosDoEnquadramento;
import br.com.financiamento.bem.FormaDoParticipante;
import br.com.financiamento.bem.TipoDeBem;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VeiculoTest {

    final Veiculo veiculo = new Veiculo();

    DadosDoEnquadramento dados(TipoDeBem tipo, int anoModelo, int prazo) {
        return new DadosDoEnquadramento(tipo, LocalDate.of(2026, 3, 10), prazo, anoModelo, null,
                FormaDoParticipante.PESSOA_FISICA, LocalDate.of(1990, 1, 1));
    }

    @Test
    void rn0011_exemploDoModelo_carro2016Em60MesesEnquadraNoLimite() {
        assertTrue(veiculo.verificarRegrasProprias(dados(Veiculo.CARRO_USADO, 2016, 60)).isEmpty());
    }

    @Test
    void rn0011_umAnoAMaisNaoEnquadra() {
        var v = veiculo.verificarRegrasProprias(dados(Veiculo.CARRO_USADO, 2015, 60));
        assertEquals("RN-0011", v.getFirst().regra());
    }

    @Test
    void rn0011_motoUsadaTemLimiteDe10Anos() {
        assertTrue(veiculo.verificarRegrasProprias(dados(Veiculo.MOTO_USADA, 2026 - 7, 36)).isEmpty());
        assertEquals(1, veiculo.verificarRegrasProprias(dados(Veiculo.MOTO_USADA, 2026 - 8, 36)).size());
    }

    @Test
    void rn0011_naoValeParaVeiculoNovo() {
        assertTrue(veiculo.verificarRegrasProprias(dados(Veiculo.CARRO_NOVO, 1990, 60)).isEmpty());
    }

    @Test
    void rn0017_pessoaJuridicaSoNoVeiculoNovo() {
        assertTrue(veiculo.documentosDoParticipante(Veiculo.CARRO_USADO, FormaDoParticipante.PESSOA_JURIDICA).isEmpty());
        assertTrue(veiculo.documentosDoParticipante(Veiculo.MOTO_NOVA, FormaDoParticipante.PESSOA_JURIDICA).contains("contrato social"));
    }

    @Test
    void rn0020_contrapropostaVale5Dias() {
        assertEquals(5, veiculo.validadeDaContraproposta().getDays());
    }

    @Test
    void rn0023_novoVale30DiasEUsadoAteOFimDoMes() {
        var dia = LocalDate.of(2026, 2, 10);
        assertEquals(LocalDate.of(2026, 3, 12), veiculo.validadeDaAvaliacao(Veiculo.CARRO_NOVO, dia));
        assertEquals(LocalDate.of(2026, 2, 28), veiculo.validadeDaAvaliacao(Veiculo.MOTO_USADA, dia));
        assertEquals("tabela de referência", veiculo.fonteDaAvaliacao(Veiculo.CARRO_USADO));
    }

    @Test
    void rn0028_orgaoDeRegistro() {
        assertEquals("órgão de trânsito", veiculo.orgaoDeRegistro(Veiculo.MOTO_NOVA));
    }
}
