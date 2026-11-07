# Operação

A aplicação é uma imagem JVM (`src/main/docker/Dockerfile.jvm`, Temurin 25, usuário sem privilégio), configurada só por variáveis de ambiente. O ambiente local completo está em [`docker/compose.yml`](../../docker/compose.yml): veja [externos simulados](../api/externos-simulados.md).

## Variáveis de ambiente

| Variável | Para quê |
|---|---|
| `QUARKUS_DATASOURCE_JDBC_URL`, `QUARKUS_DATASOURCE_USERNAME`, `QUARKUS_DATASOURCE_PASSWORD` | PostgreSQL. As migrações criam os esquemas na subida. |
| `JWT_ISSUER`, `JWT_JWKS_URL` | O emissor de JWT e as chaves dele ([ADR 0012](../arquitetura/adr/0012-autenticacao-delegada-e-autorizacao.md)) |
| `JWT_CLAIM_PAPEIS`, `JWT_CLAIM_DOCUMENTO` | As claims dos papéis e do CPF/CNPJ (padrão `groups` e `cpf`) |
| `EXTERNOS_URL` | O endereço comum dos sistemas externos (no ambiente local, o WireMock) |
| `QUARKUS_REST_CLIENT_<EXTERNO>_URL` | O endereço de um externo à parte: `BUREAU`, `AVALIADOR`, `SEGURADORA`, `CARTORIO`, `ORGAO_DE_TRANSITO`, `FGTS` ou `PAGAMENTO` |
| `URL_PUBLICA` | Como os externos alcançam a aplicação, para devolver os resultados (`urlDeRetorno`) |
| `OUTBOX_RELAY_INTERVALO` | De quanto em quanto tempo o relay envia os pendentes (padrão `5s`) |
| `DOCUMENTOS_ARMAZEM_DIRETORIO` | A pasta dos arquivos dos documentos (na imagem, o volume `/dados/documentos`; [ADR 0010](../arquitetura/adr/0010-armazenamento-de-arquivos.md)) |

## Saúde e métricas
- **`/q/health/live` e `/q/health/ready`:** a prontidão inclui o banco.
- **`/q/metrics`** (Prometheus), com as métricas do outbox ([ADR 0005](../arquitetura/adr/0005-comunicacao-sincrona-interna-e-outbox.md), [ADR 0013](../arquitetura/adr/0013-integracao-http-com-externos-simulados.md)):

| Métrica | Alerta sugerido |
|---|---|
| `financiamento_outbox_pendentes` | — |
| `financiamento_outbox_pendente_mais_antigo_segundos` | acima de 900 (15 min): o relay parou ou um externo está fora do ar há muito tempo |
| `financiamento_outbox_desistidos` | cresceu: um externo recusou um pedido (4xx). O motivo fica em `shared.outbox.ultimo_erro` |
| `financiamento_outbox_falhas_total{externo,resultado}` | taxa alta de `TEMPORARIA` para um externo |

Para reenviar um pedido desistido depois de corrigir a causa:

```sql
update shared.outbox set desistido_em = null, proxima_tentativa = null where id = ...;
```
