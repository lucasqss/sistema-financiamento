package br.com.financiamento.avaliacao.entrypoint.api.web;

import br.com.financiamento.avaliacao.dominio.MedicaoDaObra;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MedicaoWebResponse(Long id, Long contratoId, BigDecimal percentualAcumulado, LocalDate medidaEm) {

    public static MedicaoWebResponse de(MedicaoDaObra m) {
        return new MedicaoWebResponse(m.id(), m.contratoId(), m.percentualAcumulado(), m.medidaEm());
    }
}
