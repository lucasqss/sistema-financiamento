# 0007. Java 25 e Quarkus 3.33

**Status:** Aceita · **Data:** 2026-11-05

## Contexto
Os projetos de referência do time (multi-module) já usam Java 25 e Quarkus 3. Existe também a skill `arquitetura-modular` para essa stack: regras de módulo, fachadas e testes ArchUnit. A operação é em Kubernetes.

## Decisão
**Java 25** e **Quarkus 3.33**, com:
- Hibernate ORM com Panache e PostgreSQL;
- Quarkus Messaging, com o conector RabbitMQ ([0006](0006-mensageria-por-fila-gerenciada.md));
- REST;
- Dev Services nos testes;
- ArchUnit para as fitness functions.

## Consequências
- Reaproveita padrões, testes e conhecimento do time. Ganha a evolutividade.
- Imagem leve e rápida para Kubernetes. Imagem nativa possível se um projeto precisar.
- A versão do Quarkus segue a plataforma BOM. Atualizações maiores viram ADR.
