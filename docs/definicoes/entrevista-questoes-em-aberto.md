# Entrevista: questões em aberto do domínio

- **Data:** 2026-11-05
- **Objetivo:** responder às questões em aberto levantadas na modelagem do espaço do problema
  ([`../dominio/especificacao.md`](../dominio/especificacao.md) (seção "Questões em aberto")), antes de passar ao espaço da solução.
- **Formato:** entrevista em 6 rodadas. As 4 primeiras cobrem as 13 questões originais; a 5ª trata dos desdobramentos
  que as respostas abriram; a 6ª responde às 4 questões que surgiram delas. Em cada pergunta estão as opções apresentadas e a resposta (**➜**).

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

## Rodada 6: as questões que surgiram (2026-11-05)

As quatro questões abertas pelas respostas anteriores, registradas no modelo como Q-0014 a Q-0017.

### Q-0014. Qual o limite de prorrogações da obra do imóvel na planta?
- 180 dias acumulados (sugerida)
- Até 2 prorrogações
- Os dois limites

**➜ 180 dias acumulados.** As prorrogações somadas estendem o prazo previsto da obra em até 180 dias, em quantas
vezes for preciso. Acima disso, pendência para o analista.

### Q-0015. Qual a validade da contraproposta?
- 10 dias corridos (sugerida)
- Por categoria
- Igual à da proposta (30 dias)

**➜ Por categoria.** Desdobramento, quais prazos:
- Veículo 5, imóvel e terreno 15 dias (sugerida)
- Veículo 3, imóvel e terreno 10 dias
- Veículo 7, imóvel e terreno 30 dias

**➜ Veículo 5 dias, imóvel e terreno 15 dias**, corridos.

### Q-0016. Qual a validade da avaliação em cada tipo de bem?
- Sugestão: veículo novo 30 dias, veículo usado o mês da consulta, imóvel e terreno 180 dias (sugerida)
- Imóvel na planta mais curto (90 dias)
- Tudo 90 dias

**➜ Usar a sugestão.**

### Q-0017. Quais documentos são exigidos de participante pessoa jurídica?
Opções (múltipla escolha): contrato social e alterações; faturamento dos últimos 12 meses; balanço e DRE do último
exercício; documentos dos sócios e procuração.

**➜ Contrato social e alterações; documentos dos sócios e procuração.** Desdobramento: sem faturamento, como fica a
renda comprovada da PJ, usada no limite de 30% da parcela?
- Faturamento obtido pelo bureau (sugerida)
- PJ fora do limite de comprometimento
- Incluir o faturamento nos documentos

**➜ Incluir o faturamento dos últimos 12 meses nos documentos.** Ele é a renda comprovada da PJ.

### Decisões da rodada 6

| # | Questão | Decisão | Impacto no modelo |
|---|---|---|---|
| D14 | Q-0014 | Prorrogação da obra até 180 dias acumulados | Regra de prorrogação com limite definido |
| D15 | Q-0015 | Contraproposta vale 5 dias (veículo) ou 15 dias (imóvel e terreno) | Regra da contraproposta passa a variar por tipo de bem |
| D16 | Q-0016 | Avaliação: veículo novo 30 dias, usado o mês da consulta, imóvel e terreno 180 dias | Tabela de fonte da avaliação deixa de ser hipotética |
| D17 | Q-0017 | PJ entrega contrato social, faturamento de 12 meses e documentos dos sócios com procuração | Documentos de PJ e definição da renda comprovada da PJ |

Com isso, **não há questão em aberto** no modelo.

## Próximo passo

Feito: o modelo foi refeito no episteme (`docs/dominio/`) e as decisões viraram artefatos `DEC` que respondem às questões `Q`.

---

# Entrevista: vida do contrato (2026-11-07)

- **Objetivo:** levar o modelo além do contrato formalizado, que estava fora de escopo. Antes, a etapa seguinte
  do processo foi escolhida entre vida do contrato, lacunas da jornada atual, produção e cálculo financeiro: **vida do contrato**.
- **Formato:** 2 rodadas. As questões e decisões entram no modelo como Q-0018 a Q-0025 e DEC-0018 a DEC-0025, **em revisão**.

## Rodada 7: escopo e quem faz as contas

### Q-0018. O que entra nesta etapa da vida do contrato?
Opções (múltipla escolha): cobrança e atraso; quitação e baixa da garantia; renegociação; retomada da garantia
(consolidação da propriedade do imóvel, busca e apreensão do veículo).

**➜ Quitação e baixa da garantia, e renegociação.** Cobrança das parcelas, atraso e retomada ficam para outra etapa.

### Q-0019. Saldo devedor, valor de quitação antecipada (com o desconto proporcional dos juros) e encargos exigem cálculo financeiro, fora de escopo. Quem calcula?
- Um sistema financeiro externo (sugerida)
- Trazer o cálculo para o escopo (taxa, CET, PRICE/SAC)
- Simplificar: parcelas fixas sem juros, quitação = soma das restantes

**➜ Um sistema financeiro externo.** O financiamento decide e conduz; o cálculo continua fora do escopo.

### Q-0020. Quem emite a cobrança (boleto, débito, Pix) e informa os pagamentos?
- Um sistema de cobrança externo (sugerida)
- O próprio sistema de financiamento

**➜ Um sistema de cobrança externo.** Nesta etapa, ele cobra só a quitação e a amortização.

### Q-0021. Quando o atraso vira inadimplência, e o que acontece?
- 90 dias (sugerida)
- Prazo por tipo de bem
- Só registra o atraso

**➜ 90 dias**, valor de referência. Como a cobrança e a retomada ficaram fora, nesta etapa a inadimplência só informa:
o contrato inadimplente também pode ser renegociado.

## Rodada 8: quitação parcial, FGTS e renegociação

### Q-0022. Na amortização (quitação parcial), o que muda no contrato?
- O devedor escolhe reduzir o prazo ou a parcela (sugerida)
- Só reduz o prazo
- Sem amortização parcial

**➜ O devedor escolhe.** O sistema financeiro calcula o efeito, e o contrato ganha nova versão das condições.

### Q-0023. O FGTS pode amortizar ou quitar o imóvel residencial?
- Sim, no residencial de pessoa física, com 2 anos entre usos (sugerida)
- Não nesta etapa

**➜ Sim.** Pedido ao sistema do FGTS, como na entrada.

### Q-0024. Renegociação: quem pede, quando, e passa por nova análise?
- O devedor pede, em dia ou em atraso; reenquadra, passa no bureau e o analista decide (sugerida)
- Só em atraso, incorporando as vencidas, sem bureau
- O credor oferece e o devedor aceita ou recusa

**➜ O devedor pede, a qualquer momento (inadimplente incluído).** As novas condições respeitam as regras do tipo de bem,
a análise de crédito é refeita e o analista decide.

### Q-0025. Como a renegociação é formalizada?
- Aditivo assinado por todos e averbado (cartório) ou gravame atualizado (veículo) (sugerida)
- Só o aditivo assinado

**➜ Aditivo assinado e averbado.**

### Decisões das rodadas 7 e 8

| # | Questão | Decisão | Impacto no modelo |
|---|---|---|---|
| D18 | Q-0018 | Quitação com baixa da garantia, e renegociação | Domínio novo Carteira de contratos; o contrato ganha Quitado e Encerrado |
| D19 | Q-0019 | Sistema financeiro externo faz as contas | Ator novo; quitação e renegociação esperam o cálculo dele |
| D20 | Q-0020 | Sistema de cobrança externo | Ator novo; cobra a quitação e a amortização |
| D21 | Q-0021 | Inadimplente com 90 dias de atraso, só informativo nesta etapa | Regra de inadimplência; não impede a renegociação |
| D22 | Q-0022 | Amortização reduz prazo ou parcela, à escolha | Regra da amortização; nova versão das condições do contrato |
| D23 | Q-0023 | FGTS na quitação e na amortização do residencial de PF, 2 anos entre usos | Regra do FGTS na amortização |
| D24 | Q-0024 | Renegociação a pedido, reenquadrada, com bureau e decisão do analista | Agregado e ciclo de vida da renegociação; reuso das regras de enquadramento |
| D25 | Q-0025 | Aditivo assinado e averbado | Garantia ganha a averbação do aditivo e a baixa |

Próximo passo: validar o modelo (situação "Em revisão" no episteme) e então implementar.
