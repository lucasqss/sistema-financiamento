# 0013. Integração com os sistemas externos por HTTP, pelo relay do outbox, simulados no WireMock

**Status:** Aceita · **Data:** 2026-11-07 · **Substitui em parte:** [0006](0006-mensageria-por-fila-gerenciada.md) (o meio de saída e de entrada)

## Contexto
Os pedidos aos 7 sistemas externos ficavam gravados no outbox ([0005](0005-comunicacao-sincrona-interna-e-outbox.md)), mas nada os enviava. São eles:
- bureau, avaliador e seguradora;
- cartório e órgão de trânsito;
- FGTS e pagamento.

O ADR 0006 previa uma fila gerenciada entre nós e os externos. O escopo deste projeto, porém, é o sistema de financiamento: os externos não são construídos aqui. Eles precisam existir para a jornada rodar de ponta a ponta, em desenvolvimento, nos testes e na CI.

Os externos reais costumam expor HTTP e devolver o resultado de forma assíncrona, chamando de volta um endereço informado no pedido.

## Decisão
- **Saída: relay do outbox chamando cada externo por HTTP.**
  - O relay (`shared/outbox/RelayDoOutbox`, agendado) reivindica uma mensagem por vez, com `FOR UPDATE SKIP LOCKED`, e entrega às rotas de saída. A chamada HTTP fica fora da transação longa.
  - **A ordem vale por agregado:** uma mensagem só sai depois das anteriores do mesmo agregado. Um externo fora do ar não trava os pedidos dos outros.
  - **Falha temporária** (conexão, tempo esgotado, 5xx): nova tentativa com recuo exponencial (2, 4, 8… segundos, até 15 min).
  - **Recusa** (4xx): o relay desiste da mensagem, guarda o motivo e conta na métrica `financiamento_outbox_desistidos`, para alguém tratar.
  - **Evento sem rota** é interno e só é marcado como enviado.
- **Camada anticorrupção por externo** (`<módulo>/integracao`): a porta `RotaDeSaida` traduz o evento do modelo para o formato do externo e chama o cliente HTTP (`@RegisterRestClient`).
  - Cada pedido leva o nosso protocolo (`CRE-`, `AVA-`, `GAR-`, `LIB-`…) e a `urlDeRetorno`.

| Evento | Externo |
|---|---|
| `AnaliseCreditoSolicitada` | bureau |
| `AvaliacaoSolicitada` | avaliador |
| `CondicoesAceitas` | seguradora (um pedido por seguro) |
| `RegistroSolicitado` | cartório ou órgão de trânsito, pelo plug-in (RN-0028) |
| `FgtsSolicitado` | FGTS |
| `LiberacaoSolicitada` | pagamento |

- **Entrada: os `/retornos/...` que já existiam.**
  - São idempotentes pelo protocolo e autenticados com o JWT de sistema ([0012](0012-autenticacao-delegada-e-autorizacao.md)).
  - Retornos simultâneos que mudam o mesmo agregado, como o crédito e a avaliação da mesma proposta, são repetidos em conflito de concorrência (`@RepeteEmConflito`, até 5 vezes). O que sobrar volta 503 com `Retry-After`.
- **Externos simulados no WireMock** (`docker/wiremock`), um contêiner para os sete:
  - cada stub responde 202 e devolve o resultado por *webhook* na `urlDeRetorno`;
  - a massa de teste é determinística ([`docs/api/externos-simulados.md`](../../api/externos-simulados.md));
  - o mesmo WireMock serve o JWKS de um emissor local, só de desenvolvimento;
  - os mesmos stubs rodam no `docker compose` e, embutidos, nos testes de integração.
- **A fila do ADR 0006 deixa de ser o meio padrão.** Volta se um externo real exigir mensageria, como mais uma `RotaDeSaida`, sem mudar o domínio nem o outbox.

## Consequências
- A jornada inteira roda sem nenhum externo real: na máquina de quem desenvolve, com `docker compose`; nos testes, com o WireMock embutido; e na CI.
- **Fitness functions:**
  - toda rota de saída e todo cliente de externo ficam em `..integracao..`, e todo pedido a externo tem exatamente uma rota;
  - todo endpoint de externo chamado tem stub no WireMock, e todo externo do mapa de contexto do modelo tem simulador;
  - todo retorno se repete em conflito;
  - os tokens do ambiente local valem contra o JWKS dele.
- Trocar um simulador por um externo real é trocar a URL (`QUARKUS_REST_CLIENT_<EXTERNO>_URL`) e o emissor. Se o formato do real for outro, muda só a camada anticorrupção.
- Sem fila, não há fila de mensagens mortas. O papel dela fica com o outbox: a mensagem desistida guarda o motivo, e a métrica avisa.
