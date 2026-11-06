# sistema-financiamento

Sistema-modelo de financiamento de bens (carro e moto novos e usados, imóveis e terrenos), para servir de base a
outros projetos.

Etapa atual:
- **definições de negócio aprovadas**, com o modelo mantido no episteme;
- **arquitetura definida**;
- **código iniciado**:
  - o esqueleto dos 8 módulos e as fitness functions;
  - a originação, da proposta à aprovação, com os plug-ins de veículo e imóvel;
  - os módulos Crédito e Avaliação, cujos resultados decidem a proposta;
  - a Contratação, do aceite das condições ao contrato assinado;
  - a Garantia, do contrato assinado à alienação fiduciária registrada;
  - o Desembolso, da garantia constituída ao contrato formalizado, de uma vez ou por medição da obra;
  - os Documentos: envio dos arquivos e conferência pelo analista, que a aprovação da proposta exige.

- [`docs/dominio`](docs/dominio/README.md): o modelo (documento compilado, base do episteme e script de carga);
- [`docs/api`](docs/api/README.md): **a API para quem integra**: guia, contrato OpenAPI gerado do código e catálogo de erros;
- [`docs/arquitetura`](docs/arquitetura/README.md): a arquitetura (características, estilo, módulos e ADRs);
- [`docs/definicoes`](docs/definicoes/entrevista-questoes-em-aberto.md): a entrevista que respondeu às questões em aberto.

O sistema é **só API**: as telas e os sistemas externos integram por ela. Comece pelo [guia de integração](docs/api/README.md).

## Como rodar

Pré-requisito: JDK 25. O Maven vem pelo wrapper.

```bash
./mvnw quarkus:dev
```

```bash
./mvnw verify
```

- Em dev e teste, os Dev Services do Quarkus sobem um PostgreSQL em container (é preciso Docker).
- Sem Docker, aponte para um PostgreSQL com `QUARKUS_DATASOURCE_JDBC_URL`, `QUARKUS_DATASOURCE_USERNAME` e `QUARKUS_DATASOURCE_PASSWORD`. As migrações criam os esquemas.
- A API web da originação fica em `/web/propostas`. Exemplo: `POST /web/propostas` com `{"tipoDeBem": "MOTO_USADA"}`.

A autenticação é delegada a um emissor externo de JWT ([ADR 0012](docs/arquitetura/adr/0012-autenticacao-delegada-e-autorizacao.md)). Configure:
- `JWT_ISSUER`: o emissor esperado no token;
- `JWT_JWKS_URL`: as chaves públicas do emissor;
- `JWT_CLAIM_PAPEIS`: a claim dos papéis (padrão `groups`);
- `JWT_CLAIM_DOCUMENTO`: a claim do CPF/CNPJ do usuário (padrão `cpf`).

Cada endpoint exige os papéis dos atores do modelo, e cada pessoa só age nas propostas e contratos de que participa.

Toda requisição segue o mesmo fluxo de validação ([ADR 0011](docs/arquitetura/adr/0011-fluxo-comum-de-validacao.md)):
- a cadeia de atributos do request (400, todos os campos de uma vez);
- a cadeia de domínio do caso de uso (422, ou 404);
- cada erro com um código estável do catálogo, mapeável para o catálogo corporativo em `src/main/resources/catalogo-corporativo.properties`.

O código fica em `src/main/java/br/com/financiamento`, um pacote por módulo. As fitness functions estão em `src/test/java/br/com/financiamento/arquitetura`; uma falha nelas se corrige no código, nunca na regra.
