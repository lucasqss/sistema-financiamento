-- Módulo contratacao: cronograma da obra no imóvel na planta (AGR-0002, UC-0017, RN-0027).
ALTER TABLE contratacao.preparo ADD COLUMN previsao_entrega_obra DATE;

ALTER TABLE contratacao.contrato
    ADD COLUMN obra_previsao_original    DATE,
    ADD COLUMN obra_previsao_atual       DATE,
    ADD COLUMN obra_prorrogacao_pendente DATE;
