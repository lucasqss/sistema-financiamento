package br.com.financiamento.originacao.aplicacao;

import br.com.financiamento.originacao.dominio.bem.BemDaProposta;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.Set;

/** UC-0003: informar bem e vendedor. Confere RN-0003 e RN-0004 na hora. */
@ApplicationScoped
public class InformarBemService {

    public record NovoBem(String identificacao, BigDecimal valorDeCompra, String documentoDoVendedor,
                          Integer anoModelo, Boolean construcaoAverbada) {
    }

    @Inject
    Propostas propostas;

    @Transactional
    public DetalheProposta executar(Long propostaId, NovoBem bem) {
        var proposta = propostas.buscar(propostaId);
        proposta.informarBem(new BemDaProposta(bem.identificacao(), bem.valorDeCompra(), bem.documentoDoVendedor(),
                bem.anoModelo(), bem.construcaoAverbada()));
        var enquadramento = propostas.exigirRegras(proposta, Set.of("RN-0003", "RN-0004"));
        return propostas.salvar(proposta, enquadramento);
    }
}
