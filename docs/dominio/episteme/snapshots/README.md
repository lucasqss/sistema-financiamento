# Snapshots do modelo de domínio

Cópias congeladas da base do episteme e do documento compilado, tiradas quando o modelo atinge um ponto estável.
Seguem a convenção do snapshot do próprio episteme (`<base>-snapshot-<momento>.db`). Para voltar a um snapshot,
abra a cópia no episteme ou copie-a sobre `../financiamento.db`.

| Snapshot | Data | Estado do modelo |
|---|---|---|
| `financiamento-snapshot-2026-11-05-v1.db` · [`especificacao-2026-11-05-v1.md`](especificacao-2026-11-05-v1.md) | 2026-11-05 | Espaço do problema completo: 8 domínios classificados, 2 agregados, 2 ciclos de vida, 35 eventos, 31 regras (5 parametrizadas pela dimensão "Tipo de bem"), 17 casos de uso, 17 questões todas respondidas por 17 decisões, mapa de contexto gerado. Antes do ciclo de vida verificável. |
| `financiamento-snapshot-2026-11-05-v2.db` · [`especificacao-2026-11-05-v2.md`](especificacao-2026-11-05-v2.md) | 2026-11-05 | **Definições negociais da fase aprovadas** (252 artefatos, todos "Aprovado"). 8 domínios, 2 agregados, 2 ciclos de vida conferidos, 39 eventos, 24 casos de uso, 33 regras (6 parametrizadas por 2 dimensões), 3 RNFs, 5 fontes normativas, 17 questões respondidas por 17 decisões, 407 ligações. Fora de escopo: cálculo financeiro e vida do contrato após a liberação. |
| `financiamento-snapshot-2026-11-05-v3.db` · [`especificacao-2026-11-05-v3.md`](especificacao-2026-11-05-v3.md) | 2026-11-05 | **Aprovado**, com duas correções achadas na implementação do desembolso: o CV-0002 ganha a parcela intermediária da obra ("Em obra → Em obra: ParcelaDaObraLiberada"), e o UC-0014 diz que, no imóvel na planta, a previsão de entrega da obra é informada no aceite. 252 artefatos, 408 ligações. |
| `financiamento-snapshot-2026-11-06-v4.db` · [`especificacao-2026-11-06-v4.md`](especificacao-2026-11-06-v4.md) | 2026-11-06 | **Aprovado**, com a análise documental do MVP: a aprovação da proposta (RN-0019, invariante 7 do AGR-0001, UC-0006) passa a exigir os documentos exigidos conferidos e aceitos; DocumentoAceito dispara a decisão; a conferência (UC-0024) acontece depois da submissão, e o documento recusado é reenviado com a proposta em pendência. 252 artefatos, 412 ligações. |
| `financiamento-snapshot-2026-11-05-v3.db` · [`especificacao-2026-11-05-v3.md`](especificacao-2026-11-05-v3.md) | 2026-11-05 | **Aprovado**, com duas correções achadas na implementação do desembolso: o CV-0002 ganha a parcela intermediária da obra ("Em obra → Em obra: ParcelaDaObraLiberada"), e o UC-0014 diz que, no imóvel na planta, a previsão de entrega da obra é informada no aceite. 252 artefatos, 408 ligações. |

> Comparar e restaurar seletivamente dentro do episteme ainda não existe: é a pendência
> [001 · Snapshots da base](https://github.com/lucasqss/episteme/blob/main/docs/pendencias/001-snapshots.md).
