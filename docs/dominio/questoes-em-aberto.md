# Questões em aberto

Decisões de negócio necessárias antes de ir ao espaço da solução. Cada uma muda o modelo.

| # | Questão | Impacto no modelo | Sugestão inicial |
|---|---|---|---|
| Q1 | Proponente pode ser **pessoa jurídica**? | Participante PF/PJ, documentos, análise de crédito diferente | Só PF na primeira versão |
| Q2 | Aceitar **coobrigado** (composição de renda)? Quantos? | Regra C6 soma rendas; assinaturas | Até 1 coobrigado |
| Q3 | Veículo **entre particulares** ou só de loja/concessionária? | Vendedor PF, vistoria, transferência | Ambos, com vistoria para usado |
| Q4 | Imóvel **na planta** entra? | Liberação em parcelas por obra, outro ciclo de vida | Fora da primeira versão |
| Q5 | **Terreno** é subtipo de imóvel ou categoria própria? | Hierarquia e reuso de regras | Subtipo de imóvel |
| Q6 | Terreno **rural** entra? | Documentos (CCIR, ITR, CAR), registro | Só urbano na primeira versão |
| Q7 | Veículo novo: liberar recurso com gravame **solicitado** ou só **confirmado**? | Ordem das etapas 7 e 8 | Solicitado, para veículo novo |
| Q8 | Seguros obrigatórios (MIP/DFI no imóvel, prestamista no veículo) entram? | Condições, parcela | Fora (parcela informada) |
| Q9 | Quem pode iniciar a proposta: proponente, vendedor, ambos? | Atores e permissões | Ambos |
| Q10 | **Ressalva** do crédito: o proponente aceita no sistema ou volta a rascunho? | Fluxo de pendência | Pendência; aceite gera reenquadramento |
| Q11 | Uso de **FGTS** ou subsídio no imóvel? | Composição da entrada | Fora da primeira versão |
| Q12 | O mesmo bem pode estar em mais de uma proposta ativa? | Regra de unicidade entre propostas | Não |
| Q13 | Validade da avaliação (ex.: laudo vale 180 dias)? | Revalidação antes da contratação | Sim, por categoria |

## Próximo passo

Com estas questões respondidas: escolher a arquitetura (camadas como no `multi-module` ou monólito modular) e
traduzir os agregados **Proposta** e **Contrato**, as regras por tipo e a linguagem ubíqua em código de domínio com testes.
