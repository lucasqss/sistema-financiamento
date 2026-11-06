-- Módulo originacao: quem iniciou a proposta (UC-0001), para a autorização por dono (ADR 0012).
ALTER TABLE originacao.proposta
    ADD COLUMN iniciada_por_documento VARCHAR(14),
    ADD COLUMN iniciada_por_papel     VARCHAR(30);
