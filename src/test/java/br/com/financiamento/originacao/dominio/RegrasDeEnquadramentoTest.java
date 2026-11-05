package br.com.financiamento.originacao.dominio;

import br.com.financiamento.bem.CategoriaDeBem;
import br.com.financiamento.bem.FormaDoParticipante;
import br.com.financiamento.bem.imovel.ImovelParaTeste;
import br.com.financiamento.bem.veiculo.VeiculoParaTeste;
import br.com.financiamento.originacao.dominio.participante.Papel;
import br.com.financiamento.originacao.dominio.participante.Participante;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static br.com.financiamento.DocumentosParaTeste.cnpj;
import static br.com.financiamento.DocumentosParaTeste.cpf;
import static br.com.financiamento.originacao.dominio.PropostaParaTeste.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegrasDeEnquadramentoTest {

    final CategoriaDeBem veiculo = VeiculoParaTeste.plugin();
    final CategoriaDeBem imovel = ImovelParaTeste.plugin();

    static BigDecimal r(String v) {
        return new BigDecimal(v);
    }

    Proposta motoUsada(String compra, String entrada) {
        return completa("MOTO_USADA", veiculo, r(compra), r(entrada), 36, r("900"), 2022, null);
    }

    @Test
    void propostaCompletaEnquadra() {
        var e = enquadrar(motoUsada("20000", "7400"), veiculo);
        assertTrue(e.enquadrada(), e.violacoes().toString());
    }

    @Test
    void rascunhoVazioListaOQueFaltaPelaRN0018() {
        var e = enquadrar(rascunho("CARRO_NOVO"), veiculo);
        assertEquals(List.of("RN-0009", "RN-0018"), regras(e));
        assertEquals(5, e.violacoes().stream().filter(v -> v.regra().equals("RN-0018")).count());
    }

    @Test
    void rn0006_exemploDoModelo_motoUsadaComAvaliacaoDe18Mil() {
        var p = motoUsada("20000", "4000");
        p.informarBem(p.bem().comAvaliacao(r("18000")));
        var e = enquadrar(p, veiculo);
        assertEquals(List.of("RN-0006"), regras(e));
        assertTrue(e.violacoes().getFirst().mensagem().contains("entrada mínima é 7400.00"), e.violacoes().toString());

        p.informarCondicoes(r("7400"), BigDecimal.ZERO, 36, r("900"));
        assertTrue(enquadrar(p, veiculo).enquadrada());
    }

    @Test
    void rn0005_valorFinanciadoMaiorQueZero() {
        assertEquals(List.of("RN-0005"), regras(enquadrar(motoUsada("20000", "20000"), veiculo)));
    }

    @Test
    void rn0007_prazoNaFaixaDoTipo() {
        var p = motoUsada("20000", "7400");
        p.informarCondicoes(r("7400"), BigDecimal.ZERO, 48, r("900"));
        assertEquals(List.of("RN-0007"), regras(enquadrar(p, veiculo)));
    }

    @Test
    void rn0008_parcelaAte30PorCentoDaRendaSomada() {
        var p = motoUsada("20000", "7400");
        p.informarCondicoes(r("7400"), BigDecimal.ZERO, 36, r("6000.01"));
        assertEquals(List.of("RN-0008"), regras(enquadrar(p, veiculo)));
        p.informarCondicoes(r("7400"), BigDecimal.ZERO, 36, r("6000"));
        assertTrue(enquadrar(p, veiculo).enquadrada());
    }

    @Test
    void rn0009_faltaDocumento() {
        var p = rascunho("MOTO_USADA");
        p.informarParticipantes(List.of(proponentePf(r("20000"))));
        p.informarBem(new br.com.financiamento.originacao.dominio.bem.BemDaProposta("B", r("20000"), VENDEDOR, 2022, null));
        p.informarCondicoes(r("7400"), BigDecimal.ZERO, 36, r("900"));
        var e = enquadrar(p, veiculo);
        assertEquals(List.of("RN-0009"), regras(e));
        assertEquals(4, e.violacoes().size()); // CRLV e os 3 documentos da pessoa física
    }

    @Test
    void rn0001_umProponenteEAteQuatroCoobrigadosSemRepetir() {
        var p = motoUsada("20000", "7400");
        var lista = new java.util.ArrayList<Participante>();
        lista.add(proponentePf(r("20000")));
        for (int i = 10; i < 15; i++) {
            lista.add(new Participante(Papel.COOBRIGADO, FormaDoParticipante.PESSOA_FISICA, cpf(i), "C" + i, LocalDate.of(1980, 1, 1), r("1000")));
        }
        p.informarParticipantes(lista);
        assertTrue(regras(enquadrar(p, veiculo)).contains("RN-0001"));

        p.informarParticipantes(List.of(proponentePf(r("20000")), proponentePf(r("1"))));
        var e = enquadrar(p, veiculo);
        assertTrue(e.violacoes().stream().anyMatch(v -> v.mensagem().contains("repetido")));
        assertTrue(e.violacoes().stream().anyMatch(v -> v.mensagem().contains("2 proponentes")));
    }

    @Test
    void rn0001_menorDeIdade() {
        var p = motoUsada("20000", "7400");
        p.informarParticipantes(List.of(new Participante(Papel.PROPONENTE, FormaDoParticipante.PESSOA_FISICA, PROPONENTE, "Bia",
                HOJE.minusYears(17), r("20000"))));
        assertTrue(regras(enquadrar(p, veiculo)).contains("RN-0001"));
    }

    @Test
    void rn0033_cpfInvalido() {
        var p = motoUsada("20000", "7400");
        p.informarParticipantes(List.of(new Participante(Papel.PROPONENTE, FormaDoParticipante.PESSOA_FISICA, "11111111111", "Ana",
                LocalDate.of(1990, 1, 1), r("20000"))));
        assertTrue(regras(enquadrar(p, veiculo)).contains("RN-0033"));
    }

    @Test
    void rn0002_pessoaJuridicaSoOndeOTipoAceita() {
        var pj = new Participante(Papel.PROPONENTE, FormaDoParticipante.PESSOA_JURIDICA, cnpj(1), "Empresa", null, r("50000"));
        var usada = motoUsada("20000", "7400");
        usada.informarParticipantes(List.of(pj));
        assertTrue(regras(enquadrar(usada, veiculo)).contains("RN-0002"));

        var nova = completa("MOTO_NOVA", veiculo, r("20000"), r("2000"), 36, r("900"), null, null);
        nova.informarParticipantes(List.of(pj));
        entregarDocumentos(nova, veiculo);
        assertTrue(enquadrar(nova, veiculo).enquadrada(), enquadrar(nova, veiculo).violacoes().toString());
    }

    @Test
    void rn0002_coobrigadoPjSoEmPropostaDePj() {
        var p = motoUsada("20000", "7400");
        p.informarParticipantes(List.of(proponentePf(r("20000")),
                new Participante(Papel.COOBRIGADO, FormaDoParticipante.PESSOA_JURIDICA, cnpj(2), "Empresa", null, r("1"))));
        assertTrue(regras(enquadrar(p, veiculo)).contains("RN-0002"));
    }

    @Test
    void rn0003_vendedorDiferenteDoProponente() {
        var p = motoUsada("20000", "7400");
        p.informarBem(new br.com.financiamento.originacao.dominio.bem.BemDaProposta("B", r("20000"), PROPONENTE, 2022, null));
        assertEquals(List.of("RN-0003"), regras(enquadrar(p, veiculo)));
    }

    @Test
    void rn0004_bemEmOutraPropostaAtiva() {
        var e = RegrasDeEnquadramento.avaliar(motoUsada("20000", "7400"), veiculo, HOJE, true);
        assertEquals(List.of("RN-0004"), regras(e));
    }

    @Test
    void rn0015_fgtsSoNoResidencialDePessoaFisica() {
        var residencial = completa("RESIDENCIAL_PRONTO", imovel, r("500000"), r("100000"), 360, r("4000"), null, null);
        residencial.informarCondicoes(r("50000"), r("50000"), 360, r("4000"));
        assertTrue(enquadrar(residencial, imovel).enquadrada(), enquadrar(residencial, imovel).violacoes().toString());

        var comercial = completa("COMERCIAL_PRONTO", imovel, r("500000"), r("150000"), 240, r("4000"), null, null);
        comercial.informarCondicoes(r("100000"), r("50000"), 240, r("4000"));
        assertEquals(List.of("RN-0015"), regras(enquadrar(comercial, imovel)));
    }

    @Test
    void regrasDoPluginEntramNoEnquadramento() {
        var terreno = completa("TERRENO_RURAL", imovel, r("100000"), r("50000"), 120, r("1000"), null, true);
        assertEquals(List.of("RN-0014"), regras(enquadrar(terreno, imovel)));
    }
}
