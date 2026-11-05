# 0004. PostgreSQL com um esquema por módulo

**Status:** Aceita · **Data:** 2026-11-05

## Contexto
Com um quantum ([0002](0002-um-quantum.md)), um banco só dá transação local entre domínios. Isso é bom para a
confiabilidade. Mas um banco sem fronteiras acopla os módulos pelas tabelas e mina a evolutividade. A operação é em
Kubernetes, com banco gerenciado.

## Decisão
**PostgreSQL**, com **um esquema por módulo**:
- cada módulo é dono do seu esquema e só ele lê e grava as suas tabelas;
- outro módulo chega a esses dados só pela fachada, usando IDs, nunca por join nem consulta direta;
- as migrações são versionadas por módulo;
- o outbox de cada módulo fica no próprio esquema.

## Consequências
- Transações ACID entre módulos continuam possíveis no mesmo banco, sem acoplamento por tabela.
- Extrair um módulo depois exige só mover o seu esquema.
- Dev Services do Quarkus sobem o PostgreSQL nos testes.
- Relatórios que cruzam domínios usam visões de leitura próprias, nunca as tabelas dos módulos.
- Uma fitness function confere que nenhum módulo acessa outro esquema ([0008](0008-fitness-functions.md)).
