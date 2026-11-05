# Entrevista: questões em aberto do domínio

- **Data:** 2026-11-05
- **Objetivo:** responder às questões em aberto levantadas na modelagem do espaço do problema
  ([`../dominio/especificacao.md`](../dominio/especificacao.md) (seção "Questões em aberto")), antes de passar ao espaço da solução.
- **Formato:** entrevista em 5 rodadas. As 4 primeiras cobrem as 13 questões originais; a 5ª trata dos desdobramentos
  que as respostas abriram. Em cada pergunta estão as opções apresentadas e a resposta (**➜**).

---

## Rodada 1: pessoas

### Q1. O proponente pode ser pessoa jurídica (empresa), ou só pessoa física?
- Só pessoa física (sugerida)
- PF e PJ
- PJ só em alguns tipos de bem

**➜ PJ só em alguns tipos de bem.** Quais tipos: ver a rodada 2.

### Q2. A proposta pode ter coobrigado (outra pessoa que compõe a renda e responde junto pela dívida)?
- Até 1 coobrigado (sugerida)
- Vários coobrigados
- Sem coobrigado

**➜ Até 4 coobrigados** (resposta livre). Uma proposta tem no máximo 5 participantes.

### Q9. Quem pode iniciar uma proposta?
- Proponente e vendedor (sugerida)
- Só o proponente
- Proponente, vendedor e correspondente

**➜ Proponente, vendedor e correspondente.** O correspondente (correspondente bancário ou corretor) passa a ser um ator
próprio, com sua carteira de propostas.

### Q3. Na compra de veículo, quem pode ser o vendedor?
- Loja e particular (sugerida)
- Só loja/concessionária

**➜ Loja e particular.** No usado vendido por particular entram vistoria e conferência de que o vendedor é o dono.

---

## Rodada 2: PJ e bens

### Q1 (desdobramento). Em quais tipos de bem uma empresa pode ser proponente?
Opções (múltipla escolha): carro (novo e usado), moto (nova e usada), imóvel comercial, terreno.

**➜ Carro e moto apenas novos, e imóvel comercial** (resposta livre). Veículo usado, imóvel residencial e terreno:
só pessoa física.

### Q2 (desdobramento). Quando o proponente é uma empresa, quem pode ser coobrigado?
- Só PF, como avalista (sugerida)
- PF ou PJ
- Sem coobrigado para PJ

**➜ PF ou PJ.** Os sócios (PF) ou outra empresa, por exemplo do mesmo grupo.

### Q4. O imóvel na planta (em construção, com dinheiro liberado conforme a obra avança) entra no modelo?
- Não, só imóvel pronto (sugerida)
- Sim, como subtipo

**➜ Sim.** Detalhes na rodada 3.

### Q5/Q6. Como tratar o terreno?
- Subtipo de imóvel, só urbano (sugerida)
- Subtipo de imóvel, urbano e rural
- Categoria própria

**➜ Subtipo de imóvel, urbano e rural.** O terreno rural tem documentos próprios (CCIR, ITR, CAR).

---

## Rodada 3: imóvel na planta e fluxo

### Q4 (desdobramento). O imóvel na planta vale para qual finalidade?
- Residencial e comercial (sugerida)
- Só residencial

**➜ Residencial e comercial.** "Na planta" deixa de ser subtipo e vira **condição do imóvel** (pronto × na planta),
como novo/usado no veículo. Com isso, PJ também pode financiar imóvel comercial na planta. Terreno não tem condição.

### Q4 (desdobramento). No imóvel na planta, como o dinheiro é liberado para a incorporadora?
- Por medição da obra (sugerida)
- Cronograma fixo
- Liberação única na entrega

**➜ Por medição da obra.** Um engenheiro vistoria a obra periodicamente e cada medição libera a parcela
correspondente ao avanço. A garantia é registrada antes da primeira liberação.

### Q7. No veículo novo, o dinheiro pode ser liberado com o gravame apenas solicitado, ou só depois de confirmado?
- Solicitado, só para veículo novo (sugerida)
- Sempre após confirmado
- Solicitado para todo veículo

**➜ Sempre após confirmado.** Nenhuma liberação sem garantia confirmada, em todos os tipos de bem.

### Q10. Quando a análise de crédito aprova com ressalva (valor menor, entrada maior), o que acontece?
- Pendência com aceite (sugerida)
- Volta a rascunho
- Contraproposta

**➜ Contraproposta.** A ressalva gera uma nova versão da proposta com as condições do credor, e o proponente aceita
ou recusa essa versão. Prazo e recusa: ver a rodada 5.

---

## Rodada 4: regras e dinheiro

### Q12. O mesmo bem (mesmo chassi ou matrícula) pode estar em mais de uma proposta ativa?
- Não (sugerida)
- Sim, até uma ser contratada

**➜ Não.** Um bem só pode estar em uma proposta ativa por vez.

### Q13. A avaliação do bem tem validade?
- Sim, por categoria (sugerida)
- Não tem validade

**➜ Sim, por categoria** (ex.: laudo de imóvel vale 180 dias; tabela de referência de veículo vale o mês da consulta).
Vencida antes da contratação, a proposta volta para pendência e o bem é reavaliado.

### Q8. Seguros obrigatórios entram no modelo?
- Fora por enquanto (sugerida)
- Sim, como exigência
- Sim, com cálculo

**➜ Sim, como exigência.** O modelo registra quais seguros cada tipo de bem exige e cobra a contratação antes do
contrato, sem calcular prêmio. Quais seguros: ver a rodada 5.

### Q11. No imóvel, a entrada pode usar FGTS ou subsídio habitacional?
- Fora por enquanto (sugerida)
- FGTS na entrada
- FGTS e subsídio

**➜ FGTS na entrada**, sem subsídio. Regras: ver a rodada 5.

---

## Rodada 5: desdobramentos

### Seguros exigidos por tipo de bem
Proposta apresentada: imóvel (pronto e na planta) exige MIP (morte e invalidez) e DFI (danos físicos ao imóvel);
terreno exige só MIP; veículo exige seguro do bem (casco); prestamista é opcional em todos.

**➜ Usar a proposta.**

### Regras do FGTS na entrada
- Regra completa simplificada: imóvel residencial, PF com 3+ anos de FGTS, sem outro imóvel na cidade, moradia própria, valor até um teto (sugerida)
- Versão mínima

**➜ Versão mínima.** O modelo só garante que o FGTS seja usado em **imóvel residencial** (pronto ou na planta) de
**proponente PF**. A elegibilidade e o saldo são decididos pelo sistema externo do FGTS.

### Prazo e recusa da contraproposta
- Com prazo; recusa ou vencimento reprova (sugerida)
- Com prazo; recusa volta a rascunho
- Várias rodadas de negociação

**➜ Com prazo; recusa ou vencimento reprova.** A contraproposta tem validade (ex.: 10 dias). Se for aceita, a proposta
segue com as novas condições. Se for recusada ou vencer, a proposta é encerrada como reprovada.

### Atraso da obra no imóvel na planta
- Fora do escopo agora (sugerida)
- Prorrogação registrada

**➜ Prorrogação registrada.** O contrato aceita prorrogações do prazo da obra até um limite; acima do limite vira
pendência para o analista.

---

## Resumo das decisões

| # | Questão | Decisão | Impacto no modelo |
|---|---|---|---|
| D1 | Q1 | PJ só em carro novo, moto nova e imóvel comercial | Participante PF/PJ; regra "aceita PJ" por tipo de bem |
| D2 | Q2 | Até 4 coobrigados; em proposta PJ, coobrigado PF ou PJ | Proposta com 1 a 5 participantes; renda somada |
| D3 | Q9 | Proponente, vendedor e correspondente iniciam a proposta | Novo ator: Correspondente |
| D4 | Q3 | Vendedor de veículo: loja ou particular | Vendedor PF/PJ; vistoria no usado de particular |
| D5 | Q4 | Na planta = condição do imóvel (pronto × na planta), residencial e comercial | Nova condição; incorporadora como vendedor |
| D6 | Q4 / rodada 5 | Liberação por medição; atraso aceita prorrogação até um limite | Medição, liberação parcial, cronograma da obra |
| D7 | Q5/Q6 | Terreno = subtipo de imóvel, urbano e rural | Documentos do rural (CCIR, ITR, CAR) |
| D8 | Q7 | Nenhuma liberação sem garantia confirmada | Regra sem exceção, em todos os tipos |
| D9 | Q10 / rodada 5 | Ressalva vira contraproposta com prazo; recusa ou vencimento reprova | Novo estado e versões das condições |
| D10 | Q12 | Bem em no máximo uma proposta ativa | Regra de unicidade entre propostas |
| D11 | Q13 | Avaliação com validade por categoria | Reavaliação antes de contratar |
| D12 | Q8 / rodada 5 | Seguros como exigência: MIP+DFI (imóvel), MIP (terreno), seguro do bem (veículo), prestamista opcional | Seguro exigido por tipo; novo externo: seguradora |
| D13 | Q11 / rodada 5 | FGTS na entrada só em imóvel residencial de PF; elegibilidade pelo sistema do FGTS | Entrada = recurso próprio + FGTS; novo externo |

## Novas questões surgidas

1. Qual o limite de prorrogações (quantidade ou meses) do prazo da obra?
2. Qual o prazo exato de validade da contraproposta?
3. Qual a validade exata da avaliação em cada categoria (veículo novo, usado, imóvel, terreno)?
4. Quais documentos são exigidos de participante PJ (contrato social, balanço, procuração dos sócios)?

## Próximo passo

Feito: o modelo foi refeito no episteme (`docs/dominio/`) e as decisões viraram artefatos `DEC` que respondem às questões `Q`.
