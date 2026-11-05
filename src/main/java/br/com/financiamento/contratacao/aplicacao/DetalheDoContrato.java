package br.com.financiamento.contratacao.aplicacao;

import br.com.financiamento.contratacao.dominio.Contrato;
import br.com.financiamento.contratacao.dominio.EstadoContrato;
import br.com.financiamento.contratacao.dominio.assinatura.Signatario;
import br.com.financiamento.contratacao.dominio.condicoes.CondicoesFinais;
import br.com.financiamento.contratacao.dominio.seguro.Seguro;

import java.util.List;

public record DetalheDoContrato(Long id, Long propostaId, String tipoDeBem, EstadoContrato estado,
                                CondicoesFinais condicoes, List<Seguro> seguros, List<Signatario> signatarios) {

    static DetalheDoContrato de(Contrato c) {
        return new DetalheDoContrato(c.id(), c.propostaId(), c.tipoDeBem().codigo(), c.estado(), c.condicoes(),
                c.seguros(), c.signatarios());
    }
}
