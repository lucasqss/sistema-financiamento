package br.com.financiamento.avaliacao.dominio;

import br.com.financiamento.shared.validacao.ErroDeDominioException;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Uma medição da obra do imóvel na planta (GLO-0019, UC-0013): o percentual
 * acumulado executado, que nunca diminui em relação à anterior (RN-0024).
 */
@Entity
@Table(schema = "avaliacao", name = "medicao")
public class MedicaoDaObra extends PanacheEntityBase {

    static final BigDecimal CEM = new BigDecimal("100");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contrato_id", nullable = false, updatable = false)
    private Long contratoId;

    @Column(name = "percentual_acumulado", nullable = false, updatable = false)
    private BigDecimal percentualAcumulado;

    @Column(name = "medida_em", nullable = false, updatable = false)
    private LocalDate medidaEm;

    protected MedicaoDaObra() {
    }

    /** RN-0024: entre 0 e 100%, nunca abaixo da medição anterior. */
    public static MedicaoDaObra medir(Long contratoId, BigDecimal percentualAcumulado, LocalDate medidaEm,
                                      MedicaoDaObra anterior) {
        if (percentualAcumulado == null || percentualAcumulado.signum() <= 0 || percentualAcumulado.compareTo(CEM) > 0) {
            throw new ErroDeDominioException(ErrosDaAvaliacao.PERCENTUAL_FORA_DA_FAIXA);
        }
        if (anterior != null && percentualAcumulado.compareTo(anterior.percentualAcumulado) < 0) {
            throw new ErroDeDominioException(ErrosDaAvaliacao.MEDICAO_DIMINUIU, anterior.percentualAcumulado.stripTrailingZeros().toPlainString());
        }
        var m = new MedicaoDaObra();
        m.contratoId = contratoId;
        m.percentualAcumulado = percentualAcumulado;
        m.medidaEm = medidaEm;
        return m;
    }

    public Long id() { return id; }
    public Long contratoId() { return contratoId; }
    public BigDecimal percentualAcumulado() { return percentualAcumulado; }
    public LocalDate medidaEm() { return medidaEm; }
}
