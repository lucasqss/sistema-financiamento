package br.com.financiamento.avaliacao.entrypoint.api.web;

import br.com.financiamento.avaliacao.dominio.Avaliacao;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AvaliacaoWebResponse(Long id, Long propostaId, String tipoDeBem, String identificacaoDoBem, String fonte,
                                   Boolean aceitoComoGarantia, BigDecimal valor, LocalDate avaliadaEm,
                                   LocalDate validaAte) {

    public static AvaliacaoWebResponse de(Avaliacao a) {
        return new AvaliacaoWebResponse(a.id(), a.propostaId(), a.tipoDeBem().codigo(), a.identificacaoDoBem(),
                a.fonte(), a.aceitoComoGarantia(), a.valor(), a.avaliadaEm(), a.validaAte());
    }
}
