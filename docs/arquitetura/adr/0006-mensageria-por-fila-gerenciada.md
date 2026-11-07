# 0006. Fila gerenciada atrás de uma porta: RabbitMQ padrão, SQS alternativa

**Status:** Substituída em parte pela [0013](0013-integracao-http-com-externos-simulados.md) (a saída e a entrada passam a ser HTTP, com os externos simulados no WireMock) · **Data:** 2026-11-05

## Contexto
O outbox ([0005](0005-comunicacao-sincrona-interna-e-outbox.md)) precisa de um meio durável para os comandos de saída e os retornos de entrada. Com volume baixo, um log de eventos (Kafka) é mais do que o necessário. Uma fila gerenciada basta.

O sistema é um modelo para vários projetos, e cada um pode estar num provedor diferente.

## Decisão
- A mensageria fica atrás de uma **porta** do domínio. O relay do outbox e os consumidores de retorno dependem dela, não do broker.
- **Adaptador padrão: RabbitMQ.** Roda em qualquer cluster Kubernetes ou como serviço gerenciado e tem Dev Services no Quarkus para os testes.
- **Adaptador alternativo: Amazon SQS**, para projetos em AWS.
- **Retornos que não puderem ser processados** vão para a fila de mensagens mortas, com alerta para o analista.

## Consequências
- Trocar de broker é trocar de adaptador, sem tocar no domínio nem no outbox.
- Sem reprocessamento por histórico (como teria um log de eventos). A auditoria fica no banco: outbox e decisões registradas.
- Dois adaptadores a manter. O SQS só é implementado quando um projeto precisar.
