# Snapshots do modelo de domínio

Cópias congeladas da base do episteme e do documento compilado, tiradas quando o modelo atinge um ponto estável.
Seguem a convenção do snapshot do próprio episteme (`<base>-snapshot-<momento>.db`). Para voltar a um snapshot,
abra a cópia no episteme ou copie-a sobre `../financiamento.db`.

| Snapshot | Data | Estado do modelo |
|---|---|---|
| `financiamento-snapshot-2026-11-05-v1.db` · [`especificacao-2026-11-05-v1.md`](especificacao-2026-11-05-v1.md) | 2026-11-05 | Espaço do problema completo: 8 domínios classificados, 2 agregados, 2 ciclos de vida, 35 eventos, 31 regras (5 parametrizadas pela dimensão "Tipo de bem"), 17 casos de uso, 17 questões todas respondidas por 17 decisões, mapa de contexto gerado. Antes do ciclo de vida verificável. |

> Comparar e restaurar seletivamente dentro do episteme ainda não existe: é a pendência
> [001 · Snapshots da base](https://github.com/lucasqss/episteme/blob/main/docs/pendencias/001-snapshots.md).
