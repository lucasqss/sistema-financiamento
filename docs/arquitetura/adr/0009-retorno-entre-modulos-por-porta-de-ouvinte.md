# 0009. Retorno entre módulos por porta de ouvinte

**Status:** Aceita · **Data:** 2026-11-05

## Contexto
No mapa de contexto, a Originação depende do Crédito e da Avaliação: ela pede a análise e a avaliação na submissão.
Mas a decisão do crédito e o laudo do avaliador precisam voltar à Originação, que decide a proposta (UC-0006).

Se o Crédito chamasse a fachada da Originação, haveria um ciclo entre módulos, proibido pelas fitness functions
([0008](0008-fitness-functions.md)). Eventos internos (CDI events) estão descartados pelo [0005](0005-comunicacao-sincrona-interna-e-outbox.md).
Publicar na fila e consumir de volta tornaria assíncrona uma conversa entre módulos do mesmo quantum.

## Decisão
O módulo que decide expõe, na sua raiz, uma **porta de ouvinte**: uma interface com o fato que ele produz. Por exemplo,
`OuvinteDoCredito.creditoDecidido(DecisaoDeCredito)` e `OuvinteDaAvaliacao.bemAvaliado(ResultadoDeAvaliacao)`.
- Quem depende do módulo implementa a porta no seu `entrypoint/modulo/`, porque é uma entrada, e repassa ao caso de uso.
- O módulo que decide chama todos os ouvintes, sem saber quem são, **na mesma transação** em que registra o fato e o grava no outbox.
- O fato é entregue como fato. Quem ouve decide o que fazer, inclusive nada, quando ele é de uma versão antiga ou chega a uma proposta encerrada.

## Consequências
- A dependência continua num só sentido (Originação → Crédito, Originação → Avaliação) e a comunicação continua síncrona e transacional:
  se a decisão da proposta falhar, o registro do crédito também é desfeito e o retorno pode ser reenviado.
- O retorno externo continua idempotente: o mesmo retorno repetido não chama os ouvintes de novo.
- Uma porta de ouvinte é uma interface só para inverter a dependência entre módulos. Não serve para criar abstração dentro de um módulo.
- Se um módulo for extraído como serviço, a porta vira a assinatura de um evento na fila, sem mudar quem a implementa.
