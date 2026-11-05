# 0002. Um quantum

**Status:** Aceita · **Data:** 2026-11-05

## Contexto
Um quantum de arquitetura é uma parte implantável de forma independente, com alta coesão funcional e as suas
próprias características. Separar quanta só compensa quando partes do sistema precisam de características
diferentes. Por exemplo: uma parte com elasticidade alta e outra não, ou ciclos de entrega independentes de times diferentes.

Aqui não há esse caso:
- as três características que dirigem valem para todos os domínios;
- o volume é baixo;
- há um só time.

As integrações externas são assíncronas, mas a confiabilidade delas se resolve com fila durável e outbox ([0005](0005-comunicacao-sincrona-interna-e-outbox.md)), sem um quantum próprio.

## Decisão
O sistema é **um quantum**: um implantável, com um banco.

## Consequências
- Transações locais entre domínios, sem sagas nem consistência eventual interna. Ganha a confiabilidade.
- Operação simples: um artefato, réplicas no Kubernetes para a disponibilidade.
- Um módulo que um dia precise de características próprias pode ser extraído, porque já tem fronteira e esquema próprios ([0003](0003-monolito-modular-com-microkernel.md), [0004](0004-postgresql-com-esquema-por-modulo.md)).
