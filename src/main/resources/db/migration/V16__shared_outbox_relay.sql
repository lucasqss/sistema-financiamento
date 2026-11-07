-- Módulo shared: envio do outbox aos sistemas externos pelo relay (ADR 0013).
ALTER TABLE shared.outbox
    ADD COLUMN tentativas        INTEGER     NOT NULL DEFAULT 0,
    ADD COLUMN proxima_tentativa TIMESTAMPTZ,
    ADD COLUMN ultimo_erro       TEXT,
    ADD COLUMN desistido_em      TIMESTAMPTZ;

DROP INDEX shared.outbox_pendentes;
CREATE INDEX outbox_pendentes ON shared.outbox (id) WHERE publicado_em IS NULL AND desistido_em IS NULL;
