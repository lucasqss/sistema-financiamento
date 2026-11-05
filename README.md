# sistema-financiamento

Sistema-modelo de financiamento de bens (carro e moto novos e usados, imóveis e terrenos), para servir de base a
outros projetos.

Etapa atual:
- **definições de negócio aprovadas**, com o modelo mantido no episteme;
- **arquitetura definida**;
- **código iniciado**: o esqueleto dos 8 módulos, as fitness functions e o núcleo da originação (proposta e plug-ins de veículo e imóvel).

- [`docs/dominio`](docs/dominio/README.md): o modelo (documento compilado, base do episteme e script de carga);
- [`docs/arquitetura`](docs/arquitetura/README.md): a arquitetura (características, estilo, módulos e ADRs);
- [`docs/definicoes`](docs/definicoes/entrevista-questoes-em-aberto.md): a entrevista que respondeu às questões em aberto.

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

O código fica em `src/main/java/br/com/financiamento`, um pacote por módulo. As fitness functions estão em `src/test/java/br/com/financiamento/arquitetura`; uma falha nelas se corrige no código, nunca na regra.
