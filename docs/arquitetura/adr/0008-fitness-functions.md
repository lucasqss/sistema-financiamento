# 0008. Fitness functions para as decisões estruturais

**Status:** Aceita · **Data:** 2026-11-05

## Contexto
Um monólito modular só se mantém modular se as fronteiras forem verificadas automaticamente. Senão, degrada com o
tempo e perde a evolutividade. O microkernel só entrega extensibilidade se o núcleo não conhecer nenhuma categoria
de bem. O esquema por módulo só protege se ninguém consultar o esquema alheio.

## Decisão
Três fitness functions, que rodam nos testes de toda build e não podem ser desligadas para fazer o teste passar:

| Fitness function | Garante | Como |
|---|---|---|
| **Fronteiras de módulo** | [0003](0003-monolito-modular-com-microkernel.md) | ArchUnit, com as regras da skill `arquitetura-modular`: sem ciclo entre módulos; outro módulo só pela `Facade`; domínio sem depender de aplicação, entrada ou integração; adaptadores e repositories de subpacote não públicos |
| **Plug-ins de tipo de bem** | [0003](0003-monolito-modular-com-microkernel.md) | ArchUnit: o núcleo (`originacao` e os outros módulos) não importa classe de nenhum plug-in. Teste: todo tipo de bem final da dimensão "Tipo de bem" (DIM-0001) do modelo aprovado é atendido por exatamente um plug-in registrado |
| **Esquema por módulo** | [0004](0004-postgresql-com-esquema-por-modulo.md) | Teste que confere que o mapeamento de cada módulo só aponta para o seu esquema e que nenhuma consulta nativa cita outro esquema |

## Consequências
- Uma violação quebra a build. A correção é no código, nunca na regra. Mudar uma regra exige um novo ADR.
- A lista de tipos de bem do teste de plug-ins vem do modelo de negócio. Um tipo novo no modelo sem plug-in quebra a build, o que mantém negócio e código alinhados.
- A implementação das funções fica para a fase de código.
