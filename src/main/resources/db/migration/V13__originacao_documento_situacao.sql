-- Módulo originacao: documento entregue com o vínculo ao módulo Documentos e o resultado da conferência (RN-0019, UC-0024).
ALTER TABLE originacao.proposta_documento
    ADD COLUMN documento_id BIGINT,
    ADD COLUMN situacao     VARCHAR(20)  NOT NULL DEFAULT 'ENTREGUE',
    ADD COLUMN motivo       VARCHAR(300);
