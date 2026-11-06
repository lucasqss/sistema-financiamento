package br.com.financiamento.shared.validacao;

import br.com.financiamento.shared.validacao.atributo.CadeiaDeAtributos;
import br.com.financiamento.shared.validacao.atributo.ErroDeAtributo;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static br.com.financiamento.shared.validacao.atributo.ElosDeAtributo.*;
import static org.junit.jupiter.api.Assertions.*;

/** As duas cadeias do ADR 0011: a de atributo acumula; a de domínio para no primeiro elo. */
class CadeiasDeValidacaoTest {

    record Pessoa(String nome, String documento, BigDecimal renda) {
    }

    record Grupo(List<Pessoa> pessoas) {
    }

    static final CadeiaDeAtributos<Pessoa> PESSOA = CadeiaDeAtributos.de(
            obrigatorio("nome", Pessoa::nome),
            tamanhoMaximo("nome", 5, Pessoa::nome),
            cpfOuCnpj("documento", Pessoa::documento),
            naoNegativo("renda", Pessoa::renda));

    enum Erro implements ErroCatalogado {
        NAO_ACHEI, ESTADO;

        public String codigo() { return "TST-000" + ordinal(); }
        public TipoDeErro tipo() { return TipoDeErro.DOMINIO; }
        public String modelo() { return "erro {0}"; }
        public int status() { return this == NAO_ACHEI ? 404 : 422; }
    }

    @Test
    void cadeiaDeAtributoAcumulaTodasAsFalhas() {
        var erro = assertThrows(ErroDeAtributoException.class, () -> PESSOA.validar(new Pessoa("", "123", new BigDecimal("-1"))));
        assertEquals(List.of("nome", "documento", "renda"), erro.falhas().stream().map(Falha::campo).toList());
        assertEquals(List.of("ATR-0001", "ATR-0006", "ATR-0008"), erro.falhas().stream().map(Falha::codigo).toList());
        assertDoesNotThrow(() -> PESSOA.validar(new Pessoa("Ana", "529.982.247-25", BigDecimal.TEN)));
    }

    @Test
    void itensDeListaSaemComOCaminhoDoCampo() {
        var grupo = CadeiaDeAtributos.<Grupo>de(obrigatorio("pessoas", Grupo::pessoas), cadaItem("pessoas", Grupo::pessoas, PESSOA));
        var erro = assertThrows(ErroDeAtributoException.class, () -> grupo.validar(new Grupo(List.of(
                new Pessoa("Ana", "52998224725", BigDecimal.ONE), new Pessoa("Nome longo", null, null)))));
        assertEquals("pessoas[1].nome", erro.falhas().getFirst().campo());
        assertEquals("No máximo 5 caracteres.", erro.falhas().getFirst().mensagem());
        assertEquals(ErroDeAtributo.TAMANHO_MAXIMO, erro.falhas().getFirst().erro());
    }

    @Test
    void corpoAusenteEFalhaDeAtributo() {
        var erro = assertThrows(ErroDeAtributoException.class, () -> PESSOA.validar(null));
        assertEquals("corpo", erro.falhas().getFirst().campo());
    }

    @Test
    void cadeiaDeDominioParaNoPrimeiroEloQueFalha() {
        var passou = new ArrayList<String>();
        Elo<ContextoDeDominio<Long, String>> registra = (c, p) -> { passou.add("depois"); p.seguir(c); };
        var erro = assertThrows(ErroDeDominioException.class, () -> ContextoDeDominio.validar(7L,
                ContextoDeDominio.carregar(id -> Optional.<String>empty(), Erro.NAO_ACHEI), registra));
        assertEquals(404, erro.status());
        assertEquals("erro 7", erro.falhas().getFirst().mensagem());
        assertTrue(passou.isEmpty(), "o elo seguinte não roda");

        var entidade = ContextoDeDominio.validar(7L, ContextoDeDominio.carregar(id -> Optional.of("ok"), Erro.NAO_ACHEI),
                ContextoDeDominio.exigir("ok"::equals, Erro.ESTADO, x -> new Object[]{x}), registra);
        assertEquals("ok", entidade);
        assertEquals(List.of("depois"), passou);
    }

    @Test
    void mensagemNaoFormataNumerosNemQuebraComApostrofo() {
        ErroCatalogado erro = new ErroCatalogado() {
            public String codigo() { return "TST-9"; }
            public TipoDeErro tipo() { return TipoDeErro.DOMINIO; }
            public String modelo() { return "d'água: {0}"; }
        };
        assertEquals("d'água: 12600.00", erro.mensagem(new BigDecimal("12600.00")));
    }
}
