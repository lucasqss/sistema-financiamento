-- Módulo contratacao: resumo da garantia no contrato (AGR-0002); o processo de registro é do módulo garantia.
ALTER TABLE contratacao.contrato
    ADD COLUMN garantia_orgao     VARCHAR(60),
    ADD COLUMN garantia_protocolo VARCHAR(40),
    ADD COLUMN garantia_situacao  VARCHAR(600);
