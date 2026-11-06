# Catálogo de erros da API

Gerado do código (ADR 0011) e conferido pelo teste `CatalogoDeErrosPublicadoTest`: não edite à mão.
Toda resposta de erro traz `falhas[]`, cada uma com `codigo` (desta lista), `codigoCorporativo` (quando mapeado em
`src/main/resources/catalogo-corporativo.properties`), `campo` (nas falhas de atributo), `regra` (do modelo de negócio) e `mensagem`.
Os códigos são estáveis: um código nunca muda de significado nem é reaproveitado.
`{0}`, `{1}`… na mensagem são preenchidos com os dados do caso.

## Atributo (forma e formato, todas as requisições)

| Código | Status | Tipo | Regra | Mensagem |
|---|---|---|---|---|
| `ATR-0001` | 400 | atributo | — | Campo obrigatório. |
| `ATR-0002` | 400 | atributo | — | No máximo {0} caracteres. |
| `ATR-0003` | 400 | atributo | — | Formato inválido: {0}. |
| `ATR-0004` | 400 | atributo | — | CPF inválido. |
| `ATR-0005` | 400 | atributo | — | CNPJ inválido. |
| `ATR-0006` | 400 | atributo | — | CPF ou CNPJ inválido. |
| `ATR-0007` | 400 | atributo | — | Precisa ser maior que zero. |
| `ATR-0008` | 400 | atributo | — | Não pode ser negativo. |
| `ATR-0009` | 400 | atributo | — | Valor não aceito; use um destes: {0}. |
| `ATR-0010` | 400 | atributo | — | No máximo {0} itens. |
| `ATR-0011` | 400 | atributo | — | Informe ao menos um item. |
| `ATR-0012` | 400 | atributo | — | No máximo {0}. |

## Avaliação (DOM-0003)

| Código | Status | Tipo | Regra | Mensagem |
|---|---|---|---|---|
| `AVA-0001` | 404 | dominio | — | Avaliação {0} não encontrada. |
| `AVA-0002` | 422 | dominio | UC-0012 | Bem aceito como garantia precisa do valor de avaliação. |
| `AVA-0003` | 422 | dominio | UC-0012 | A avaliação {0} já foi concluída. |
| `AVA-0004` | 422 | dominio | RN-0024 | O percentual acumulado da obra fica entre 0 e 100%. |
| `AVA-0005` | 422 | dominio | RN-0024 | A medição nunca diminui: a anterior apurou {0}%. |

## Bem e categorias (DIM-0001)

| Código | Status | Tipo | Regra | Mensagem |
|---|---|---|---|---|
| `BEM-0001` | 422 | dominio | DIM-0001 | Tipo de bem não financiado: {0}. Tipos aceitos: {1}. |
| `BEM-0002` | 422 | dominio | RN-0011 | Informe o ano-modelo do veículo usado. |
| `BEM-0003` | 422 | dominio | RN-0011 | O veículo terá {0} anos ao fim do contrato ({1}); o máximo é {2}. |
| `BEM-0004` | 422 | dominio | RN-0012 | Idade do proponente somada ao prazo passa de 80 anos e 6 meses; o prazo máximo possível é de {0} meses. |
| `BEM-0005` | 422 | dominio | RN-0014 | Informe se há construção averbada na matrícula do terreno. |
| `BEM-0006` | 422 | dominio | RN-0014 | Terreno com construção averbada não é financiado como terreno. |

## Contratação (DOM-0004)

| Código | Status | Tipo | Regra | Mensagem |
|---|---|---|---|---|
| `CON-0001` | 404 | dominio | — | Contratação {0} não encontrada. |
| `CON-0002` | 404 | dominio | — | Contrato {0} não encontrado. |
| `CON-0003` | 422 | dominio | AGR-0001 | As condições desta proposta já foram aceitas: uma proposta gera no máximo um contrato. |
| `CON-0004` | 422 | dominio | RN-0025 | Seguro opcional não oferecido para este tipo de bem: {0}. |
| `CON-0005` | 422 | dominio | RN-0025 | O seguro {0} não faz parte desta contratação. |
| `CON-0006` | 422 | dominio | RN-0025 | O seguro {0} já tem a apólice {1}. |
| `CON-0007` | 422 | dominio | RN-0025 | O contrato só é emitido com os seguros exigidos contratados. |
| `CON-0008` | 422 | dominio | RN-0025 | O contrato só é emitido com os seguros contratados. |
| `CON-0009` | 422 | dominio | RN-0026 | Cônjuges assinam só no imóvel e no terreno. |
| `CON-0010` | 422 | dominio | RN-0026 | O contrato precisa de signatários. |
| `CON-0011` | 422 | dominio | RN-0026 | Quem não é signatário exigido não assina o contrato. |
| `CON-0012` | 422 | dominio | UC-0014 | No imóvel na planta, informe a previsão de entrega da obra. |
| `CON-0013` | 422 | dominio | UC-0014 | Previsão de entrega da obra só no imóvel na planta. |
| `CON-0014` | 422 | dominio | RN-0027 | A nova data precisa ser posterior à previsão atual ({0}). |
| `CON-0015` | 422 | dominio | RN-0027 | Não há prorrogação pendente. |
| `CON-0016` | 422 | dominio | CV-0002 | O contrato {0} não aceita {1}. |
| `CON-0017` | 422 | dominio | CV-0002 | O contrato {0} não vai para {1} com {2}. |

## Crédito (DOM-0002)

| Código | Status | Tipo | Regra | Mensagem |
|---|---|---|---|---|
| `CRE-0001` | 404 | dominio | — | Análise de crédito {0} não encontrada. |
| `CRE-0002` | 422 | dominio | UC-0011 | A análise {0} já foi decidida como {1}. |
| `CRE-0003` | 422 | dominio | RN-0020 | Aprovação com ressalva precisa da ressalva e das condições sugeridas. |

## Desembolso (DOM-0006)

| Código | Status | Tipo | Regra | Mensagem |
|---|---|---|---|---|
| `DES-0001` | 404 | dominio | — | Desembolso {0} não encontrado. |
| `DES-0002` | 404 | dominio | — | O contrato {0} ainda não tem desembolso. |
| `DES-0003` | 404 | dominio | — | Protocolo de liberação {0} não é nosso. |
| `DES-0004` | 422 | dominio | RN-0030 | Nenhuma liberação antes de a garantia estar registrada e confirmada. |
| `DES-0005` | 422 | dominio | RN-0031 | A soma das liberações passaria do valor financiado. |
| `DES-0006` | 422 | dominio | RN-0031 | Protocolo de liberação desconhecido: {0}. |
| `DES-0007` | 422 | dominio | UC-0022 | Só se recompõe a entrada com o FGTS negado. |
| `DES-0008` | 422 | dominio | UC-0013 | Medição de obra só no imóvel na planta. |
| `DES-0009` | 422 | dominio | UC-0013 | Medição só para contrato de imóvel na planta com a garantia constituída. |

## Documentos (DOM-0008)

| Código | Status | Tipo | Regra | Mensagem |
|---|---|---|---|---|
| `DOC-0001` | 404 | dominio | — | Documento {0} não encontrado. |
| `DOC-0002` | 422 | dominio | UC-0024 | O arquivo está vazio. |
| `DOC-0003` | 422 | dominio | UC-0024 | O arquivo passa de 10 MB. |
| `DOC-0004` | 422 | dominio | UC-0024 | Envie PDF, JPEG ou PNG. |
| `DOC-0005` | 422 | dominio | UC-0024 | O conteúdo do arquivo não é {0}. |
| `DOC-0006` | 422 | dominio | RN-0009 | Informe qual documento é. |
| `DOC-0007` | 422 | dominio | UC-0024 | O documento {0} está {1} e não pode ser conferido de novo. |
| `DOC-0008` | 422 | dominio | UC-0024 | Informe o motivo da recusa: ilegível, vencido ou divergente. |
| `DOC-0009` | 422 | dominio | RN-0009 | O documento {0} já foi aceito. |

## Garantia (DOM-0005)

| Código | Status | Tipo | Regra | Mensagem |
|---|---|---|---|---|
| `GAR-0001` | 404 | dominio | — | Registro de garantia {0} não encontrado. |
| `GAR-0002` | 404 | dominio | — | Protocolo {0} não é nosso. |
| `GAR-0003` | 404 | dominio | — | O contrato {0} não tem registro de garantia. |
| `GAR-0004` | 422 | dominio | UC-0019 | Só se reenvia o registro recusado ou com divergência de identificação. |
| `GAR-0005` | 422 | dominio | UC-0020 | Só há exigência a cumprir no registro em exigência. |
| `GAR-0006` | 422 | dominio | UC-0019 | Só se desfaz o contrato com o registro recusado. |

## Originação (DOM-0001)

| Código | Status | Tipo | Regra | Mensagem |
|---|---|---|---|---|
| `ORI-0001` | 404 | dominio | — | Proposta {0} não encontrada. |
| `ORI-0002` | 422 | dominio | AGR-0001 | Só o rascunho e a proposta com pendência aceitam alteração; esta está {0}. |
| `ORI-0003` | 422 | dominio | CV-0001 | A proposta {0} não aceita {1}. |
| `ORI-0004` | 422 | dominio | RN-0009 | O titular {0} não é o bem nem um participante da proposta. |
| `ORI-0005` | 422 | dominio | RN-0009 | O documento {0} ({1}) já foi aceito. |
| `ORI-0006` | 422 | dominio | UC-0024 | A conferência é feita na proposta submetida e ainda em análise; esta está {0}. |
| `ORI-0007` | 422 | dominio | UC-0024 | O documento {0} não é o vigente desta proposta. |
| `ORI-0008` | 422 | dominio | UC-0014 | A proposta está {0}; só a aprovada é contratada. |
| `ORI-0009` | 422 | dominio | RN-0021 | A proposta passou da validade de 30 dias e expirou. |
| `ORI-0010` | 422 | dominio | RN-0022 | A avaliação do bem venceu; a proposta ficou com pendência para reavaliar. |
| `ORI-0011` | 422 | dominio | RN-0018 | Informe o proponente. |
| `ORI-0012` | 422 | dominio | RN-0018 | Informe a identificação do bem. |
| `ORI-0013` | 422 | dominio | RN-0018 | Informe o valor de compra do bem. |
| `ORI-0014` | 422 | dominio | RN-0018 | Informe o vendedor. |
| `ORI-0015` | 422 | dominio | RN-0018 | Informe entrada, prazo e parcela. |
| `ORI-0016` | 422 | dominio | RN-0018 | Informe a parcela simulada. |
| `ORI-0017` | 422 | dominio | RN-0001 | A proposta tem {0} participantes; o máximo é 5 (1 proponente e até 4 coobrigados). |
| `ORI-0018` | 422 | dominio | RN-0001 | A proposta tem {0} proponentes; deve ter exatamente 1. |
| `ORI-0019` | 422 | dominio | RN-0001 | Participante repetido: {0}. |
| `ORI-0020` | 422 | dominio | RN-0001 | Informe a data de nascimento de {0}. |
| `ORI-0021` | 422 | dominio | RN-0001 | {0} não é maior de idade. |
| `ORI-0022` | 422 | dominio | RN-0033 | {0} inválido: {1}. |
| `ORI-0023` | 422 | dominio | RN-0002 | Este tipo de bem não aceita pessoa jurídica como proponente. |
| `ORI-0024` | 422 | dominio | RN-0002 | Coobrigado pessoa jurídica só em proposta de proponente pessoa jurídica. |
| `ORI-0025` | 422 | dominio | RN-0003 | CPF ou CNPJ do vendedor inválido: {0}. |
| `ORI-0026` | 422 | dominio | RN-0003 | O vendedor não pode ser o proponente. |
| `ORI-0027` | 422 | dominio | RN-0004 | O bem já está em outra proposta ativa. |
| `ORI-0028` | 422 | dominio | RN-0005 | O valor financiado precisa ser maior que zero. |
| `ORI-0029` | 422 | dominio | RN-0006 | Valor financiado de {0} passa da cota máxima de {1}% do valor de referência ({2}). A entrada mínima é {3}. |
| `ORI-0030` | 422 | dominio | RN-0007 | Prazo de {0} meses fora da faixa do tipo: {1} a {2}. |
| `ORI-0031` | 422 | dominio | RN-0008 | Parcela de {0} passa de 30% da renda comprovada somada ({1}). |
| `ORI-0032` | 422 | dominio | RN-0009 | Falta o documento {0}: {1}. |
| `ORI-0033` | 422 | dominio | RN-0015 | Só imóvel residencial de proponente pessoa física aceita FGTS na entrada. |

## Acesso (autenticação e autorização, ADR 0012)

| Código | Status | Tipo | Regra | Mensagem |
|---|---|---|---|---|
| `SEG-0001` | 401 | dominio | — | Autenticação necessária: envie um token válido do emissor. |
| `SEG-0002` | 403 | dominio | — | Seu papel não permite esta operação. |
| `SEG-0003` | 403 | dominio | — | Você não participa deste {0}. |
| `SEG-0004` | 403 | dominio | — | Cada signatário assina por si: o documento informado não é o seu. |
