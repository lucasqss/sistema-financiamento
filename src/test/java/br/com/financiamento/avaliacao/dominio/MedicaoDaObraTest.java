package br.com.financiamento.avaliacao.dominio;

import br.com.financiamento.shared.validacao.ErroDeDominioException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** RN-0024: o percentual acumulado da obra nunca diminui. */
class MedicaoDaObraTest {

    static final LocalDate HOJE = LocalDate.of(2026, 11, 5);

    @Test
    void acumuladoNuncaDiminui() {
        var primeira = MedicaoDaObra.medir(1L, new BigDecimal("30"), HOJE, null);
        var segunda = MedicaoDaObra.medir(1L, new BigDecimal("30"), HOJE, primeira);
        assertEquals(0, new BigDecimal("30").compareTo(segunda.percentualAcumulado()));
        var erro = assertThrows(ErroDeDominioException.class, () -> MedicaoDaObra.medir(1L, new BigDecimal("25"), HOJE, segunda));
        assertEquals("RN-0024", erro.falhas().getFirst().regra());
        assertThrows(ErroDeDominioException.class, () -> MedicaoDaObra.medir(1L, new BigDecimal("101"), HOJE, null));
    }
}
