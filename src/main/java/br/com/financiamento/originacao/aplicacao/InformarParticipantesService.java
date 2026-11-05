package br.com.financiamento.originacao.aplicacao;

import br.com.financiamento.bem.FormaDoParticipante;
import br.com.financiamento.originacao.dominio.participante.Papel;
import br.com.financiamento.originacao.dominio.participante.Participante;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/** UC-0002: informar participantes. Confere RN-0001, RN-0002 e RN-0033 na hora. */
@ApplicationScoped
public class InformarParticipantesService {

    public record NovoParticipante(Papel papel, FormaDoParticipante forma, String documento, String nome,
                                   LocalDate nascimento, BigDecimal rendaComprovada) {
    }

    @Inject
    Propostas propostas;

    @Transactional
    public DetalheProposta executar(Long propostaId, List<NovoParticipante> participantes) {
        var proposta = propostas.buscar(propostaId);
        proposta.informarParticipantes(participantes.stream()
                .map(p -> new Participante(p.papel(), p.forma(), p.documento(), p.nome(), p.nascimento(), p.rendaComprovada()))
                .toList());
        var enquadramento = propostas.exigirRegras(proposta, Set.of("RN-0001", "RN-0002", "RN-0033"));
        return propostas.salvar(proposta, enquadramento);
    }
}
