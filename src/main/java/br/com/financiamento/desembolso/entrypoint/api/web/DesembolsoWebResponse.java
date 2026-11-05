package br.com.financiamento.desembolso.entrypoint.api.web;

import br.com.financiamento.desembolso.dominio.Desembolso;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record DesembolsoWebResponse(Long id, Long contratoId, BigDecimal valorFinanciado, BigDecimal totalLiberado,
                                    String forma, String fgts, BigDecimal percentualMedido, List<Liberacao> liberacoes) {

    public record Liberacao(String protocolo, BigDecimal valor, BigDecimal percentualAte, boolean ultima, Instant pagaEm) {
    }

    public static DesembolsoWebResponse de(Desembolso d) {
        return new DesembolsoWebResponse(d.id(), d.contratoId(), d.valorFinanciado(), d.totalLiberado(), d.forma().name(),
                d.fgts().name(), d.percentualMedido(),
                d.liberacoes().stream().map(l -> new Liberacao(l.protocolo(), l.valor(), l.percentualAte(), l.ultima(),
                        l.pagaEm())).toList());
    }
}
