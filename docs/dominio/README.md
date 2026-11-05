# Domínio do financiamento

Modelo de domínio do sistema de financiamento de bens (carro e moto novos e usados, imóvel residencial e comercial
pronto ou na planta, terreno urbano e rural), no **espaço do problema**: sem arquitetura, tecnologia nem banco.

O modelo é mantido no [episteme](https://github.com/lucasqss/episteme), que guarda cada conceito como um artefato
com código próprio e rastreia as ligações entre eles.

| Arquivo | O que é |
|---|---|
| [`especificacao.md`](especificacao.md) | O documento compilado pelo episteme, para ler (com diagramas Mermaid). **Gerado: não edite à mão.** |
| [`episteme/financiamento.db`](episteme/financiamento.db) | A base do episteme, para abrir e editar na ferramenta. |
| [`episteme/semear.ts`](episteme/semear.ts) | O script que monta a base e gera o documento. É a fonte do conteúdo. |

## O que está modelado

| Artefato do episteme | Prefixo | Quantos | No modelo |
|---|---|---|---|
| Termo do glossário | `GLO` | 45 | linguagem ubíqua: pessoas, bem, condições, jornada |
| Ator | `ATR` | 12 | proponente, coobrigado, vendedor, correspondente, analista, sistemas externos |
| Domínio (com visão) | `DOM` / `VIS` | 8 | subdomínios e mapa de contexto: Originação (principal); Crédito, Avaliação, Contratação, Garantia e Desembolso (suporte); Cadastro de pessoas e Documentos (genéricos) |
| Agregado | `AGR` | 2 | Proposta e Contrato, com composição e invariantes |
| Ciclo de vida | `CV` | 2 | estados e transições da proposta e do contrato |
| Evento de domínio | `EVT` | 35 | o que acontece na jornada, quem provoca e o que dispara |
| Dimensão de variação | `DIM` | 1 | "Tipo de bem": a hierarquia veículo/imóvel até os 10 tipos finais |
| Regra de negócio | `RN` | 31 | regras comuns, parâmetros por tipo de bem, regras da jornada |
| Caso de uso | `UC` | 17 | comandos da jornada, com os eventos que emitem |
| Questão em aberto | `Q` | 17 | todas respondidas |
| Decisão | `DEC` | 17 | as decisões D1–D17 da [entrevista](../definicoes/entrevista-questoes-em-aberto.md) |

As ligações também são dados: o caso de uso `EMITE` evento, o evento `DISPARA` caso de uso (a política), o agregado
`USA` regra (invariante) e `EMITE` evento, a decisão `RESPONDE` questão e a visão `DEPENDE_DE` outro domínio ou um
sistema externo. O **mapa de contexto** do documento é gerado dessas dependências: não é desenhado à mão.

As regras que mudam por tipo de bem (parâmetros, documentos, fonte da avaliação, seguros e validade da contraproposta) `VARIA_POR` a dimensão
"Tipo de bem". O episteme confere a tabela de cada uma: todo tipo de bem coberto exatamente uma vez, diretamente ou
por um nível acima ("Veículo usado" vale para carro e moto usados), e nenhum tipo desconhecido. Se um tipo de bem
novo entrar na dimensão, cada regra que não o cobrir passa a ser apontada.

## Como alterar

O caminho mais simples é editar no episteme e regerar o documento. Para manter o script como fonte única,
altere o `semear.ts` e rode de dentro de um checkout atualizado do episteme:

```bash
cd ../episteme && npm install && npx tsx ../sistema-financiamento/docs/dominio/episteme/semear.ts
```

O script recria a base do zero, interrompe se houver avisos de citação ou violação dos invariantes do episteme, e regera
`especificacao.md`.

Para abrir a base no episteme local, copie `financiamento.db` para `~/episteme/bases/` e escolha a base
`financiamento` na ferramenta.
