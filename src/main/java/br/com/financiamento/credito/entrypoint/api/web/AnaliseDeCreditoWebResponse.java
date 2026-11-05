package br.com.financiamento.credito.entrypoint.api.web;

import br.com.financiamento.credito.dominio.AnaliseDeCredito;

import java.math.BigDecimal;

public record AnaliseDeCreditoWebResponse(Long id, Long propostaId, int condicoesVersao, BigDecimal valorFinanciado,
                                          int prazoMeses, BigDecimal parcela, String resultado, String decididaPor,
                                          String ressalva) {

    public static AnaliseDeCreditoWebResponse de(AnaliseDeCredito a) {
        return new AnaliseDeCreditoWebResponse(a.id(), a.propostaId(), a.condicoesVersao(), a.valorFinanciado(),
                a.prazoMeses(), a.parcela(), a.resultado() == null ? null : a.resultado().name(),
                a.decididaPor() == null ? null : a.decididaPor().name(), a.ressalva());
    }
}
