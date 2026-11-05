package br.com.financiamento.shared.outbox;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Evento de domínio aguardando publicação (transactional outbox, ADR 0005).
 * Gravado na mesma transação que mudou o agregado; o publicador o envia à fila
 * depois do commit.
 */
@Entity
@Table(schema = "shared", name = "outbox")
class MensagemOutbox extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public String agregado;

    @Column(name = "agregado_id", nullable = false)
    public String agregadoId;

    @Column(nullable = false)
    public String evento;

    @Column(nullable = false, columnDefinition = "text")
    public String dados;

    @Column(name = "ocorrido_em", nullable = false)
    public Instant ocorridoEm;

    @Column(name = "publicado_em")
    public Instant publicadoEm;
}
