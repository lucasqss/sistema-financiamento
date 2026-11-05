# 0005. Comunicação síncrona entre módulos e outbox para fora

**Status:** Aceita · **Data:** 2026-11-05

## Contexto
A confiabilidade é uma das três características que dirigem. O sistema conversa com 6 sistemas externos:
bureau, avaliador, seguradora, cartório, órgão de trânsito e FGTS. Os retornos deles são assíncronos e podem chegar repetidos ou fora de ordem.

Uma chamada externa feita no meio de uma transação não é desfeita pelo rollback. Um externo fora do ar travaria a jornada.

## Decisão
- **Entre módulos:** chamada síncrona pela fachada, na mesma transação. Não há eventos internos.
- **Para fora: transactional outbox.**
  - O comando para o externo é gravado no outbox na mesma transação da mudança de estado.
  - Um relay publica depois do commit, com retentativa e recuo exponencial.
- **Retornos:**
  - entram pela fila e são **idempotentes**, pelo protocolo que nós mesmos geramos;
  - são classificados como fato, nunca recusados. É a regra de negócio "retorno de registro é fato, não pedido".
- Cada externo tem uma **camada anticorrupção** no módulo dono. Os formatos externos não entram no domínio.

## Consequências
- Nada se perde: se o externo cai, o comando espera no outbox ou na fila. Nada se duplica: o retorno repetido não tem efeito.
- A liberação de dinheiro só sai depois da garantia confirmada e uma única vez por protocolo.
- Há latência entre o commit e o envio. É aceitável, porque os RNFs de prazo são de dias úteis.
- Exige monitorar o outbox: itens antigos pendentes viram alerta.
