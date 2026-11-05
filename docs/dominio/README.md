# Domínio do financiamento — espaço do problema

> Etapa 1 de modelagem: **entender o problema** antes de decidir a solução. Aqui não há arquitetura, framework,
> pacotes nem banco de dados. Tudo que está aqui deve fazer sentido para alguém de negócio.

## Visão

Um **sistema-modelo de financiamento de bens**, reaproveitável em vários projetos. Ele cobre a jornada desde a
**proposta** até o **contrato formalizado com a garantia registrada**, para quatro famílias de bens:

| Categoria | Subtipos | Condição |
|---|---|---|
| **Veículo** | carro, moto | novo ou usado |
| **Imóvel** | residencial, comercial | pronto (*) |
| **Imóvel** | terreno | — |

(*) imóvel na planta está em aberto (ver [questões em aberto](questoes-em-aberto.md)).

O que se repete em todos os tipos é a **jornada**. O que varia é a **regra**: prazo, quanto pode ser financiado,
documentos, como o bem é avaliado e onde a garantia é registrada.

## Objetivo do sistema

1. Receber e montar uma proposta de financiamento de um bem.
2. Enquadrar a proposta nas regras do tipo de bem.
3. Obter a avaliação do bem e a análise de crédito do proponente.
4. Formalizar o contrato e constituir a garantia (alienação fiduciária) no órgão competente.
5. Liberar o recurso ao vendedor.

## Atores

| Ator | Papel |
|---|---|
| **Proponente** | quem pede o financiamento e será o devedor |
| **Coobrigado** (opcional) | compõe renda e responde junto pela dívida |
| **Vendedor** | concessionária, loja, incorporadora ou particular que vende o bem e recebe o recurso |
| **Analista** | trata pendências e decide casos fora da alçada automática |
| **Avaliador** (externo) | laudo do imóvel/terreno; tabela de referência (ex.: FIPE) para veículo usado |
| **Bureau de crédito** (externo) | score, restrições e endividamento do proponente |
| **Cartório de Registro de Imóveis** (externo) | registra a alienação fiduciária do imóvel/terreno na matrícula |
| **Órgão de trânsito / sistema de gravames** (externo) | registra o gravame do veículo |

## Fora de escopo (por enquanto)

- Vida do contrato depois de formalizado: cobrança, pagamento de parcelas, atraso, renegociação, quitação.
- Cálculo financeiro detalhado (taxa, CET, PRICE/SAC, seguros). Nesta etapa a parcela é tratada como um valor
  informado pela simulação, sem modelar como é calculada.
- Consórcio, leasing, refinanciamento (home/auto equity).

## Documentos

1. [Linguagem ubíqua](linguagem-ubiqua.md) — o glossário.
2. [Subdomínios e contextos](subdominios.md) — o que é central, o que é suporte, quem conversa com quem.
3. [Fluxo e eventos](fluxo-e-eventos.md) — event storming da jornada e ciclos de vida.
4. [Regras por tipo de bem](regras-por-tipo-de-bem.md) — a matriz do que varia.
5. [Modelo conceitual](modelo-conceitual.md) — conceitos, agregados candidatos e invariantes.
6. [Questões em aberto](questoes-em-aberto.md) — o que precisa ser decidido antes de ir à solução.

> Os números das regras são **hipotéticos e parametrizáveis**. O modelo existe para ser adaptado por cada projeto.
