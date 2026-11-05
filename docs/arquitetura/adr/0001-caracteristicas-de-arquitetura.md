# 0001. Evolutividade, extensibilidade e confiabilidade dirigem a arquitetura

**Status:** Aceita · **Data:** 2026-11-05

## Contexto
Na entrevista de arquitetura, oito características foram consideradas importantes:
- disponibilidade e confiabilidade;
- evolutividade, extensibilidade e testabilidade;
- auditabilidade, segurança e interoperabilidade.

Mark Richards recomenda que no máximo três **dirijam** o desenho. Tentar maximizar todas leva a uma arquitetura genérica, que não atende bem a nenhuma.

O contexto que pesa na escolha:
- o sistema é um **modelo para vários projetos**;
- ele varia por **tipo de bem**;
- ele movimenta **dinheiro e garantias**;
- o volume é baixo (~1 mil propostas por mês) e o time é pequeno.

## Decisão
**Dirigem:**
- **Evolutividade:** cada projeto adapta regras, prazos e jornada.
- **Extensibilidade:** um tipo de bem novo entra sem mexer no núcleo.
- **Confiabilidade:** nada se perde nem se duplica (proposta, contrato, retorno de cartório, liberação).

**Implícitas:** valem e são atendidas no desenho de qualquer estilo, mas não decidem a escolha.
- **Disponibilidade:** canais 24x7, com réplicas de um implantável.
- **Testabilidade:** domínio isolado, externos simulados.
- **Auditabilidade:** decisões registradas com motivo e responsável.
- **Segurança:** acesso por perfil.
- **Interoperabilidade:** camada anticorrupção por externo.

**Não são críticos:** desempenho e elasticidade.

## Consequências
- O estilo é escolhido pela aderência às três características ([0003](0003-monolito-modular-com-microkernel.md)).
- Desempenho e elasticidade não justificam distribuição. Se o volume crescer muito, esta decisão é revista.
- As implícitas viram requisitos de projeto e de teste, sem moldar a estrutura.
