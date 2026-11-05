package br.com.financiamento.avaliacao.aplicacao;

import br.com.financiamento.avaliacao.MedicaoRealizada;
import br.com.financiamento.avaliacao.OuvinteDaAvaliacao;
import br.com.financiamento.avaliacao.dominio.MedicaoDaObra;
import br.com.financiamento.avaliacao.dominio.MedicaoDaObraRepository;
import br.com.financiamento.shared.outbox.Outbox;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * UC-0013: o engenheiro (ATR-0006) informa o percentual acumulado da obra
 * (RN-0024). Emite MedicaoRealizada e avisa quem acompanha a avaliação, na
 * mesma transação: se a medição não cabe ao contrato, ela é desfeita junto.
 */
@ApplicationScoped
public class MedirObraService {

    @Inject
    MedicaoDaObraRepository medicoes;

    @Inject
    Outbox outbox;

    @Inject
    @Any
    Instance<OuvinteDaAvaliacao> ouvintes;

    @Transactional
    public MedicaoDaObra executar(Long contratoId, BigDecimal percentualAcumulado, LocalDate medidaEm) {
        var medicao = MedicaoDaObra.medir(contratoId, percentualAcumulado, medidaEm,
                medicoes.ultimaDoContrato(contratoId).orElse(null));
        medicoes.persistAndFlush(medicao);
        outbox.registrar("MedicaoDaObra", medicao.id(), "MedicaoRealizada", Map.of("contratoId", contratoId,
                "percentualAcumulado", percentualAcumulado, "medidaEm", medidaEm.toString()));
        var fato = new MedicaoRealizada(medicao.id(), contratoId, percentualAcumulado, medidaEm);
        ouvintes.forEach(o -> o.medicaoRealizada(fato));
        return medicao;
    }

    public List<MedicaoDaObra> doContrato(Long contratoId) {
        return medicoes.doContrato(contratoId);
    }
}
