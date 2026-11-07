# Sistemas externos simulados

O sistema conversa com 7 sistemas externos. Nenhum deles é construído aqui: em desenvolvimento, nos testes e na CI, todos são simulados num **WireMock** ([ADR 0013](../arquitetura/adr/0013-integracao-http-com-externos-simulados.md)). Os stubs ficam em [`docker/wiremock/mappings`](../../docker/wiremock/mappings), e os mesmos rodam no `docker compose` e, embutidos, nos testes de integração.

## Como funciona
1. A mudança de estado grava o pedido no outbox. O relay o envia por HTTP ao externo, pela camada anticorrupção do módulo dono.
2. O simulador responde `202` e, logo depois, devolve o resultado por *webhook*: um `POST` na `urlDeRetorno` do pedido, com o JWT do sistema.
3. O retorno entra pelos mesmos `/retornos/...` do [guia de integração](README.md) e é idempotente.

| Externo | Recebe | Devolve em | Massa de teste |
|---|---|---|---|
| Bureau de crédito | `POST /bureau/consultas` com `protocolo` (`CRE-<análise>`), `documentos`, `valorFinanciado`, `prazoMeses`, `parcela` | `/retornos/bureau-de-credito/{analiseId}` | valor financiado de 1.000.000 ou mais é **reprovado**; o resto é aprovado |
| Avaliador | `POST /avaliador/laudos` com `protocolo` (`AVA-<avaliação>`), `tipoDeBem`, `identificacaoDoBem`, `valorDeCompra`, `fonte` | `/retornos/avaliador/{avaliacaoId}` | aceita o bem pelo valor de compra, avaliado hoje |
| Seguradora | `POST /seguradora/apolices`, **um por seguro**: `contratacao`, `propostaId`, `seguro` | `/retornos/seguradora/{contratacaoId}` | emite a apólice `AP-<seguro>-<contratação>` |
| Cartório de registro de imóveis | `POST /cartorio/registros` com `protocolo` (`GAR-<contrato>-<tentativa>`), `identificacaoDoBem`, `tentativa` | `/retornos/orgao-de-registro` | identificação com `EXIGENCIA` gera **exigência**, e com `RECUSA` gera **recusa**, só na 1ª tentativa; o resto é registrado |
| Órgão de trânsito | `POST /orgao-de-transito/gravames`, no mesmo formato | `/retornos/orgao-de-registro` | identificação com `RECUSA` é **recusada** na 1ª tentativa; o resto é registrado |
| FGTS | `POST /fgts/saques` com `desembolso`, `valor`, `favorecido` | `/retornos/fgts/{desembolsoId}` | saque de 200.000 ou mais é **negado** (saldo insuficiente); o resto é liberado |
| Pagamento | `POST /pagamento/transferencias` com `protocolo` (`LIB-<contrato>-<n>`), `valor`, `favorecido` | `/retornos/pagamento` | confirma a transferência |

Para mudar um comportamento, edite o stub. A ordem de escolha é a `priority`: o número menor vence, e a massa de teste fica acima do padrão.

## Subir tudo

Pré-requisito: Docker. JDK 25 só para o `package`.

```bash
./mvnw package -DskipTests
```

```bash
docker compose -f docker/compose.yml --env-file docker/externos.env up --build
```

| Serviço | Endereço |
|---|---|
| Aplicação | http://localhost:8080 (contrato em `/q/openapi`, saúde em `/q/health`, métricas em `/q/metrics`) |
| Externos e emissor local (WireMock) | http://localhost:8089 (pedidos recebidos em `/__admin/requests`) |
| PostgreSQL | `localhost:5432`, banco, usuário e senha `financiamento` |

## Emissor local de JWT
Também é só para a máquina local. O WireMock serve o JWKS de um emissor de desenvolvimento (`https://emissor.local`), e `docker/externos.env` traz tokens de longa validade:
- os 6 sistemas, que o WireMock usa nos webhooks;
- pessoas de exemplo para chamar a API à mão: `TOKEN_PROPONENTE` (CPF 529.982.247-25), `TOKEN_VENDEDOR`, `TOKEN_ANALISTA` e `TOKEN_ENGENHEIRO`.

```bash
TOKEN_PROPONENTE=$(grep '^TOKEN_PROPONENTE=' docker/externos.env | cut -d= -f2-)
```

```bash
curl -H "Authorization: Bearer $TOKEN_PROPONENTE" -H 'Content-Type: application/json' -d '{"tipoDeBem":"MOTO_USADA"}' http://localhost:8080/web/propostas
```

Para gerar chaves e tokens novos:

```bash
java docker/emissor-local/GerarTokens.java
```

O `AmbienteLocalTest` confere que os tokens valem contra o JWKS. **Nunca use estas chaves fora da máquina local.**
