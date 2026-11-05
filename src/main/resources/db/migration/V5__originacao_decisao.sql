-- Módulo originacao: resultados do crédito e da avaliação, contraproposta, pendência e validade (UC-0006 a UC-0009).
ALTER TABLE originacao.proposta
    ADD COLUMN credito_resultado                VARCHAR(30),
    ADD COLUMN credito_condicoes_versao         INTEGER,
    ADD COLUMN credito_ressalva                 VARCHAR(500),
    ADD COLUMN credito_sugerida_entrada_propria NUMERIC(15, 2),
    ADD COLUMN credito_sugerida_entrada_fgts    NUMERIC(15, 2),
    ADD COLUMN credito_sugerido_prazo_meses     INTEGER,
    ADD COLUMN credito_sugerida_parcela         NUMERIC(15, 2),
    ADD COLUMN credito_solicitado_versao        INTEGER,
    ADD COLUMN avaliacao_aceito                 BOOLEAN,
    ADD COLUMN avaliacao_valor                  NUMERIC(15, 2),
    ADD COLUMN avaliacao_fonte                  VARCHAR(80),
    ADD COLUMN avaliacao_data                   DATE,
    ADD COLUMN avaliacao_valida_ate             DATE,
    ADD COLUMN avaliacao_pendente               BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN contraproposta_entrada_propria   NUMERIC(15, 2),
    ADD COLUMN contraproposta_entrada_fgts      NUMERIC(15, 2),
    ADD COLUMN contraproposta_prazo_meses       INTEGER,
    ADD COLUMN contraproposta_parcela           NUMERIC(15, 2),
    ADD COLUMN contraproposta_ressalva          VARCHAR(500),
    ADD COLUMN contraproposta_valida_ate        DATE,
    ADD COLUMN pendencia                        VARCHAR(1000),
    ADD COLUMN aprovada_em                      DATE,
    ADD COLUMN aprovacao_valida_ate             DATE;

CREATE INDEX proposta_estado ON originacao.proposta (estado);
