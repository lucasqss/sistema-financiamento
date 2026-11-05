# Sistema de Financiamento de Bens

## Índice

- 1.1 [DOC-0001 - Glossário](#doc-0001)
  - 1.1.1 [S-0001 - Pessoas](#s-0001)
    - 1.1.1.1 [GLO-0001 - Participante](#glo-0001)
    - 1.1.1.2 [GLO-0002 - Pessoa física](#glo-0002)
    - 1.1.1.3 [GLO-0003 - Pessoa jurídica](#glo-0003)
    - 1.1.1.4 [GLO-0004 - Renda comprovada](#glo-0004)
    - 1.1.1.5 [GLO-0005 - Incorporadora](#glo-0005)
  - 1.1.2 [S-0002 - Bem](#s-0002)
    - 1.1.2.1 [GLO-0006 - Bem](#glo-0006)
    - 1.1.2.2 [GLO-0007 - Categoria](#glo-0007)
    - 1.1.2.3 [GLO-0008 - Subtipo](#glo-0008)
    - 1.1.2.4 [GLO-0009 - Condição do bem](#glo-0009)
    - 1.1.2.5 [GLO-0010 - Imóvel na planta](#glo-0010)
    - 1.1.2.6 [GLO-0011 - Terreno rural](#glo-0011)
    - 1.1.2.7 [GLO-0012 - Identificação do bem](#glo-0012)
    - 1.1.2.8 [GLO-0013 - Valor de compra](#glo-0013)
    - 1.1.2.9 [GLO-0014 - Valor de avaliação](#glo-0014)
    - 1.1.2.10 [GLO-0015 - Valor de referência](#glo-0015)
    - 1.1.2.11 [GLO-0016 - Ano-modelo](#glo-0016)
    - 1.1.2.12 [GLO-0017 - Laudo de avaliação](#glo-0017)
    - 1.1.2.13 [GLO-0018 - Vistoria do veículo](#glo-0018)
    - 1.1.2.14 [GLO-0019 - Medição da obra](#glo-0019)
    - 1.1.2.15 [GLO-0020 - Validade da avaliação](#glo-0020)
  - 1.1.3 [S-0003 - Condições](#s-0003)
    - 1.1.3.1 [GLO-0021 - Entrada](#glo-0021)
    - 1.1.3.2 [GLO-0022 - Recurso do FGTS](#glo-0022)
    - 1.1.3.3 [GLO-0023 - Valor financiado](#glo-0023)
    - 1.1.3.4 [GLO-0024 - Cota de financiamento](#glo-0024)
    - 1.1.3.5 [GLO-0025 - Prazo](#glo-0025)
    - 1.1.3.6 [GLO-0026 - Parcela](#glo-0026)
    - 1.1.3.7 [GLO-0027 - Comprometimento de renda](#glo-0027)
    - 1.1.3.8 [GLO-0028 - Condições do financiamento](#glo-0028)
    - 1.1.3.9 [GLO-0029 - Seguro exigido](#glo-0029)
    - 1.1.3.10 [GLO-0030 - MIP](#glo-0030)
    - 1.1.3.11 [GLO-0031 - DFI](#glo-0031)
  - 1.1.4 [S-0004 - Jornada](#s-0004)
    - 1.1.4.1 [GLO-0032 - Proposta](#glo-0032)
    - 1.1.4.2 [GLO-0033 - Enquadramento](#glo-0033)
    - 1.1.4.3 [GLO-0034 - Submissão](#glo-0034)
    - 1.1.4.4 [GLO-0035 - Análise de crédito](#glo-0035)
    - 1.1.4.5 [GLO-0036 - Ressalva](#glo-0036)
    - 1.1.4.6 [GLO-0037 - Contraproposta](#glo-0037)
    - 1.1.4.7 [GLO-0038 - Pendência](#glo-0038)
    - 1.1.4.8 [GLO-0039 - Exigência](#glo-0039)
    - 1.1.4.9 [GLO-0040 - Contrato](#glo-0040)
    - 1.1.4.10 [GLO-0041 - Alienação fiduciária](#glo-0041)
    - 1.1.4.11 [GLO-0042 - Gravame](#glo-0042)
    - 1.1.4.12 [GLO-0043 - Registro da garantia](#glo-0043)
    - 1.1.4.13 [GLO-0044 - Liberação do recurso](#glo-0044)
    - 1.1.4.14 [GLO-0045 - Prorrogação da obra](#glo-0045)
- 1.2 [DOC-0002 - Dimensões de variação](#doc-0002)
  - 1.2.1 [DIM-0001 - Tipo de bem](#dim-0001)
  - 1.2.2 [DIM-0002 - Forma do participante](#dim-0002)
- 1.3 [DOC-0003 - Fontes normativas](#doc-0003)
  - 1.3.1 [F-0001 - Lei 9.514/1997](#f-0001)
  - 1.3.2 [F-0002 - Código Civil, arts. 1.361 e seguintes](#f-0002)
  - 1.3.3 [F-0003 - Normas do CONTRAN e do sistema nacional de gravames](#f-0003)
  - 1.3.4 [F-0004 - Lei 8.036/1990 (FGTS)](#f-0004)
  - 1.3.5 [F-0005 - Lei 13.709/2018 (LGPD)](#f-0005)
- 1.4 [DOC-0004 - Requisitos não funcionais do sistema](#doc-0004)
  - 1.4.1 [RNFS-0001 - Canais disponíveis 24x7](#rnfs-0001)
- 1.5 [DOC-0005 - Atores](#doc-0005)
  - 1.5.1 [ATR-0001 - Proponente](#atr-0001)
  - 1.5.2 [ATR-0002 - Coobrigado](#atr-0002)
  - 1.5.3 [ATR-0003 - Vendedor](#atr-0003)
  - 1.5.4 [ATR-0004 - Correspondente](#atr-0004)
  - 1.5.5 [ATR-0005 - Analista](#atr-0005)
  - 1.5.6 [ATR-0006 - Engenheiro de medição](#atr-0006)
  - 1.5.7 [ATR-0007 - Avaliador](#atr-0007)
  - 1.5.8 [ATR-0008 - Bureau de crédito](#atr-0008)
  - 1.5.9 [ATR-0009 - Cartório de Registro de Imóveis](#atr-0009)
  - 1.5.10 [ATR-0010 - Órgão de trânsito](#atr-0010)
  - 1.5.11 [ATR-0011 - Seguradora](#atr-0011)
  - 1.5.12 [ATR-0012 - Sistema do FGTS](#atr-0012)
- 1.6 [DOC-0006 - Questões em aberto](#doc-0006)
  - 1.6.1 [Q-0001 - O proponente pode ser pessoa jurídica?](#q-0001)
  - 1.6.2 [Q-0002 - A proposta aceita coobrigado? Quantos?](#q-0002)
  - 1.6.3 [Q-0003 - Quem pode vender veículo?](#q-0003)
  - 1.6.4 [Q-0004 - Imóvel na planta entra no modelo?](#q-0004)
  - 1.6.5 [Q-0005 - Terreno é subtipo de imóvel ou categoria própria?](#q-0005)
  - 1.6.6 [Q-0006 - Terreno rural entra no modelo?](#q-0006)
  - 1.6.7 [Q-0007 - Libera o dinheiro com o gravame só solicitado?](#q-0007)
  - 1.6.8 [Q-0008 - Seguros obrigatórios entram no modelo?](#q-0008)
  - 1.6.9 [Q-0009 - Quem pode iniciar uma proposta?](#q-0009)
  - 1.6.10 [Q-0010 - O que acontece com a aprovação com ressalva?](#q-0010)
  - 1.6.11 [Q-0011 - A entrada do imóvel pode usar FGTS ou subsídio?](#q-0011)
  - 1.6.12 [Q-0012 - O mesmo bem pode estar em mais de uma proposta ativa?](#q-0012)
  - 1.6.13 [Q-0013 - A avaliação do bem tem validade?](#q-0013)
  - 1.6.14 [Q-0014 - Qual o limite de prorrogações da obra?](#q-0014)
  - 1.6.15 [Q-0015 - Qual o prazo de validade da contraproposta?](#q-0015)
  - 1.6.16 [Q-0016 - Qual a validade exata da avaliação em cada categoria?](#q-0016)
  - 1.6.17 [Q-0017 - Quais documentos são exigidos de participante pessoa jurídica?](#q-0017)
- 1.7 [DOC-0007 - Decisões](#doc-0007)
  - 1.7.1 [DEC-0001 - Pessoa jurídica só em carro novo, moto nova e imóvel comercial](#dec-0001)
  - 1.7.2 [DEC-0002 - Até 4 coobrigados; em proposta de PJ, coobrigado PF ou PJ](#dec-0002)
  - 1.7.3 [DEC-0003 - Proponente, vendedor e correspondente iniciam a proposta](#dec-0003)
  - 1.7.4 [DEC-0004 - Vendedor de veículo pode ser loja ou particular](#dec-0004)
  - 1.7.5 [DEC-0005 - Na planta é condição do imóvel residencial e comercial](#dec-0005)
  - 1.7.6 [DEC-0006 - Liberação da planta por medição, com prorrogação limitada](#dec-0006)
  - 1.7.7 [DEC-0007 - Terreno é subtipo de imóvel, urbano e rural](#dec-0007)
  - 1.7.8 [DEC-0008 - Nenhuma liberação sem garantia confirmada](#dec-0008)
  - 1.7.9 [DEC-0009 - Ressalva vira contraproposta com validade](#dec-0009)
  - 1.7.10 [DEC-0010 - Um bem em no máximo uma proposta ativa](#dec-0010)
  - 1.7.11 [DEC-0011 - A avaliação tem validade por categoria](#dec-0011)
  - 1.7.12 [DEC-0012 - Seguros entram como exigência, sem cálculo](#dec-0012)
  - 1.7.13 [DEC-0013 - FGTS na entrada só no imóvel residencial de pessoa física](#dec-0013)
  - 1.7.14 [DEC-0014 - Prorrogação da obra até 180 dias acumulados](#dec-0014)
  - 1.7.15 [DEC-0015 - Validade da contraproposta por categoria](#dec-0015)
  - 1.7.16 [DEC-0016 - Validade da avaliação por tipo de bem](#dec-0016)
  - 1.7.17 [DEC-0017 - Documentos de participante pessoa jurídica](#dec-0017)
- 1.8 [DOM-0001 - Originação](#dom-0001)
  - 1.8.1 [VIS-0001 - Visão do Domínio](#vis-0001)
  - 1.8.2 [AGR-0001 - Proposta](#agr-0001)
  - 1.8.3 [CV-0001 - Ciclo de vida da proposta](#cv-0001)
  - 1.8.4 [S-0005 - Eventos](#s-0005)
    - 1.8.4.1 [EVT-0001 - PropostaIniciada](#evt-0001)
    - 1.8.4.2 [EVT-0002 - PropostaSubmetida](#evt-0002)
    - 1.8.4.3 [EVT-0003 - PropostaComPendencia](#evt-0003)
    - 1.8.4.4 [EVT-0004 - PendenciaResolvida](#evt-0004)
    - 1.8.4.5 [EVT-0005 - PropostaAprovada](#evt-0005)
    - 1.8.4.6 [EVT-0006 - PropostaReprovada](#evt-0006)
    - 1.8.4.7 [EVT-0007 - PropostaCancelada](#evt-0007)
    - 1.8.4.8 [EVT-0008 - PropostaExpirada](#evt-0008)
    - 1.8.4.9 [EVT-0009 - ContrapropostaEmitida](#evt-0009)
    - 1.8.4.10 [EVT-0010 - ContrapropostaAceita](#evt-0010)
    - 1.8.4.11 [EVT-0011 - ContrapropostaRecusada](#evt-0011)
  - 1.8.5 [DOC-0008 - Regras de Negócio](#doc-0008)
    - 1.8.5.1 [S-0006 - Regras comuns](#s-0006)
      - 1.8.5.1.1 [RN-0001 - Participantes da proposta](#rn-0001)
      - 1.8.5.1.2 [RN-0002 - Pessoa jurídica como proponente](#rn-0002)
      - 1.8.5.1.3 [RN-0003 - Vendedor identificado](#rn-0003)
      - 1.8.5.1.4 [RN-0004 - Bem em uma só proposta ativa](#rn-0004)
      - 1.8.5.1.5 [RN-0005 - Valor financiado](#rn-0005)
      - 1.8.5.1.6 [RN-0006 - Limite da cota de financiamento](#rn-0006)
      - 1.8.5.1.7 [RN-0007 - Prazo dentro da faixa do tipo](#rn-0007)
      - 1.8.5.1.8 [RN-0008 - Comprometimento de renda](#rn-0008)
      - 1.8.5.1.9 [RN-0009 - Documentos obrigatórios](#rn-0009)
    - 1.8.5.2 [S-0007 - Regras por tipo de bem](#s-0007)
      - 1.8.5.2.1 [RN-0010 - Parâmetros por tipo de bem](#rn-0010)
      - 1.8.5.2.2 [RN-0011 - Idade máxima do veículo usado](#rn-0011)
      - 1.8.5.2.3 [RN-0012 - Idade do proponente no imóvel](#rn-0012)
      - 1.8.5.2.4 [RN-0013 - Veículo usado sem restrição](#rn-0013)
      - 1.8.5.2.5 [RN-0014 - Terreno sem construção](#rn-0014)
      - 1.8.5.2.6 [RN-0015 - Uso do FGTS na entrada](#rn-0015)
      - 1.8.5.2.7 [RN-0016 - Documentos do bem por tipo](#rn-0016)
      - 1.8.5.2.8 [RN-0017 - Documentos dos participantes](#rn-0017)
    - 1.8.5.3 [S-0008 - Regras da jornada](#s-0008)
      - 1.8.5.3.1 [RN-0018 - Enquadramento para submeter](#rn-0018)
      - 1.8.5.3.2 [RN-0019 - Aprovação da proposta](#rn-0019)
      - 1.8.5.3.3 [RN-0020 - Contraproposta](#rn-0020)
      - 1.8.5.3.4 [RN-0021 - Validade da proposta aprovada](#rn-0021)
      - 1.8.5.3.5 [RN-0022 - Avaliação válida para contratar](#rn-0022)
  - 1.8.6 [S-0009 - Casos de uso](#s-0009)
    - 1.8.6.1 [UC-0001 - Iniciar proposta](#uc-0001)
    - 1.8.6.2 [UC-0002 - Informar participantes](#uc-0002)
    - 1.8.6.3 [UC-0003 - Informar bem e vendedor](#uc-0003)
    - 1.8.6.4 [UC-0004 - Simular e escolher condições](#uc-0004)
    - 1.8.6.5 [UC-0005 - Submeter proposta](#uc-0005)
    - 1.8.6.6 [UC-0006 - Decidir proposta](#uc-0006)
    - 1.8.6.7 [UC-0007 - Expirar proposta](#uc-0007)
    - 1.8.6.8 [UC-0008 - Resolver pendência](#uc-0008)
    - 1.8.6.9 [UC-0009 - Responder contraproposta](#uc-0009)
    - 1.8.6.10 [UC-0010 - Cancelar proposta](#uc-0010)
- 1.9 [DOM-0002 - Crédito](#dom-0002)
  - 1.9.1 [VIS-0002 - Visão do Domínio](#vis-0002)
  - 1.9.2 [S-0010 - Eventos](#s-0010)
    - 1.9.2.1 [EVT-0012 - AnaliseCreditoSolicitada](#evt-0012)
    - 1.9.2.2 [EVT-0013 - CreditoAprovado](#evt-0013)
    - 1.9.2.3 [EVT-0014 - CreditoAprovadoComRessalva](#evt-0014)
    - 1.9.2.4 [EVT-0015 - CreditoReprovado](#evt-0015)
  - 1.9.3 [RNFD-0001 - Resposta do crédito em até 1 dia útil](#rnfd-0001)
  - 1.9.4 [S-0011 - Casos de uso](#s-0011)
    - 1.9.4.1 [UC-0011 - Analisar crédito](#uc-0011)
- 1.10 [DOM-0003 - Avaliação](#dom-0003)
  - 1.10.1 [VIS-0003 - Visão do Domínio](#vis-0003)
  - 1.10.2 [S-0012 - Eventos](#s-0012)
    - 1.10.2.1 [EVT-0016 - AvaliacaoSolicitada](#evt-0016)
    - 1.10.2.2 [EVT-0017 - BemAvaliado](#evt-0017)
    - 1.10.2.3 [EVT-0018 - BemRecusadoComoGarantia](#evt-0018)
    - 1.10.2.4 [EVT-0019 - AvaliacaoVencida](#evt-0019)
    - 1.10.2.5 [EVT-0020 - MedicaoRealizada](#evt-0020)
  - 1.10.3 [DOC-0009 - Regras de Negócio](#doc-0009)
    - 1.10.3.1 [RN-0023 - Fonte da avaliação por tipo de bem](#rn-0023)
    - 1.10.3.2 [RN-0024 - Medição da obra](#rn-0024)
  - 1.10.4 [S-0013 - Casos de uso](#s-0013)
    - 1.10.4.1 [UC-0012 - Avaliar bem](#uc-0012)
    - 1.10.4.2 [UC-0013 - Medir obra](#uc-0013)
- 1.11 [DOM-0004 - Contratação](#dom-0004)
  - 1.11.1 [VIS-0004 - Visão do Domínio](#vis-0004)
  - 1.11.2 [AGR-0002 - Contrato](#agr-0002)
  - 1.11.3 [CV-0002 - Ciclo de vida do contrato](#cv-0002)
  - 1.11.4 [S-0014 - Eventos](#s-0014)
    - 1.11.4.1 [EVT-0021 - CondicoesAceitas](#evt-0021)
    - 1.11.4.2 [EVT-0022 - SegurosContratados](#evt-0022)
    - 1.11.4.3 [EVT-0023 - ContratoEmitido](#evt-0023)
    - 1.11.4.4 [EVT-0024 - ContratoAssinado](#evt-0024)
    - 1.11.4.5 [EVT-0025 - ContratoCancelado](#evt-0025)
    - 1.11.4.6 [EVT-0026 - PrazoDaObraProrrogado](#evt-0026)
  - 1.11.5 [DOC-0010 - Regras de Negócio](#doc-0010)
    - 1.11.5.1 [RN-0025 - Seguros exigidos por tipo de bem](#rn-0025)
    - 1.11.5.2 [RN-0026 - Assinaturas exigidas](#rn-0026)
    - 1.11.5.3 [RN-0027 - Prorrogação do prazo da obra](#rn-0027)
  - 1.11.6 [RNFD-0002 - Contrato disponível em até 2 dias úteis](#rnfd-0002)
  - 1.11.7 [S-0015 - Casos de uso](#s-0015)
    - 1.11.7.1 [UC-0014 - Emitir contrato](#uc-0014)
    - 1.11.7.2 [UC-0015 - Assinar contrato](#uc-0015)
    - 1.11.7.3 [UC-0016 - Cancelar contrato](#uc-0016)
    - 1.11.7.4 [UC-0017 - Prorrogar prazo da obra](#uc-0017)
- 1.12 [DOM-0005 - Garantia](#dom-0005)
  - 1.12.1 [VIS-0005 - Visão do Domínio](#vis-0005)
  - 1.12.2 [S-0016 - Eventos](#s-0016)
    - 1.12.2.1 [EVT-0027 - RegistroSolicitado](#evt-0027)
    - 1.12.2.2 [EVT-0028 - GarantiaRegistrada](#evt-0028)
    - 1.12.2.3 [EVT-0029 - ExigenciaEmitida](#evt-0029)
    - 1.12.2.4 [EVT-0030 - ExigenciaCumprida](#evt-0030)
    - 1.12.2.5 [EVT-0031 - RegistroRecusado](#evt-0031)
  - 1.12.3 [DOC-0011 - Regras de Negócio](#doc-0011)
    - 1.12.3.1 [RN-0028 - Órgão de registro por categoria](#rn-0028)
    - 1.12.3.2 [RN-0029 - Retorno de registro é fato, não pedido](#rn-0029)
  - 1.12.4 [S-0017 - Casos de uso](#s-0017)
    - 1.12.4.1 [UC-0018 - Registrar garantia](#uc-0018)
    - 1.12.4.2 [UC-0019 - Tratar registro recusado](#uc-0019)
    - 1.12.4.3 [UC-0020 - Cumprir exigência](#uc-0020)
- 1.13 [DOM-0006 - Desembolso](#dom-0006)
  - 1.13.1 [VIS-0006 - Visão do Domínio](#vis-0006)
  - 1.13.2 [S-0018 - Eventos](#s-0018)
    - 1.13.2.1 [EVT-0032 - LiberacaoSolicitada](#evt-0032)
    - 1.13.2.2 [EVT-0033 - RecursoLiberado](#evt-0033)
    - 1.13.2.3 [EVT-0034 - ParcelaDaObraLiberada](#evt-0034)
    - 1.13.2.4 [EVT-0035 - FgtsSolicitado](#evt-0035)
    - 1.13.2.5 [EVT-0036 - FgtsLiberado](#evt-0036)
    - 1.13.2.6 [EVT-0037 - FgtsNegado](#evt-0037)
  - 1.13.3 [DOC-0012 - Regras de Negócio](#doc-0012)
    - 1.13.3.1 [RN-0030 - Nenhuma liberação sem garantia confirmada](#rn-0030)
    - 1.13.3.2 [RN-0031 - Soma das liberações](#rn-0031)
    - 1.13.3.3 [RN-0032 - Liberação por medição](#rn-0032)
  - 1.13.4 [S-0019 - Casos de uso](#s-0019)
    - 1.13.4.1 [UC-0021 - Liberar recurso](#uc-0021)
    - 1.13.4.2 [UC-0022 - Recompor entrada](#uc-0022)
    - 1.13.4.3 [UC-0023 - Liberar parcela da obra](#uc-0023)
- 1.14 [DOM-0007 - Cadastro de pessoas](#dom-0007)
  - 1.14.1 [VIS-0007 - Visão do Domínio](#vis-0007)
  - 1.14.2 [DOC-0013 - Regras de Negócio](#doc-0013)
    - 1.14.2.1 [RN-0033 - CPF ou CNPJ válido](#rn-0033)
- 1.15 [DOM-0008 - Documentos](#dom-0008)
  - 1.15.1 [VIS-0008 - Visão do Domínio](#vis-0008)
  - 1.15.2 [S-0020 - Eventos](#s-0020)
    - 1.15.2.1 [EVT-0038 - DocumentoRecusado](#evt-0038)
    - 1.15.2.2 [EVT-0039 - DocumentoAceito](#evt-0039)
  - 1.15.3 [S-0021 - Casos de uso](#s-0021)
    - 1.15.3.1 [UC-0024 - Conferir documento](#uc-0024)

---


# <a id="p-0001"></a>1 P-0001 - Sistema de Financiamento de Bens

Modelo de domínio de um **sistema-modelo de financiamento de bens**, reaproveitável em vários projetos.
Cobre a jornada da **proposta** até o **contrato formalizado**, com a garantia registrada e o dinheiro
liberado ao vendedor. Esta etapa fica no **espaço do problema**: não há arquitetura, tecnologia nem banco.

## Bens financiados

| Categoria | Subtipo | Condição | Aceita proponente PJ |
|---|---|---|---|
| Veículo | carro | novo | sim |
| Veículo | carro | usado | não |
| Veículo | moto | nova | sim |
| Veículo | moto | usada | não |
| Imóvel | residencial | pronto ou na planta | não |
| Imóvel | comercial | pronto ou na planta | sim |
| Imóvel | terreno (urbano ou rural) | — | não |

O que se repete em todos os tipos é a **jornada**. O que varia é a **regra**: prazo, quanto pode ser
financiado, documentos, avaliação, seguros e onde a garantia é registrada.

```mermaid
flowchart TD
    BEM[Bem] --> VEI[Veículo]
    BEM --> IMO[Imóvel]
    VEI --> CAR[Carro] --> CN[novo] & CU[usado]
    VEI --> MOT[Moto] --> MN[nova] & MU[usada]
    IMO --> RES[Residencial] --> RP[pronto] & RPL[na planta]
    IMO --> COM[Comercial] --> CP[pronto] & CPL[na planta]
    IMO --> TER[Terreno] --> TU[urbano] & TR[rural]
```

## Mapa de contexto

Gerado pelo episteme a partir das dependências que cada visão de domínio declara (`DEPENDE_DE`): domínios do
sistema com a classificação (principal em destaque, genéricos tracejados) e, tracejados, os sistemas externos.

```mermaid
flowchart LR
    subgraph sistema[Domínios do sistema]
        DOM_0001["Originação<br/>principal"]
        DOM_0002["Crédito<br/>suporte"]
        DOM_0003["Avaliação<br/>suporte"]
        DOM_0004["Contratação<br/>suporte"]
        DOM_0005["Garantia<br/>suporte"]
        DOM_0006["Desembolso<br/>suporte"]
        DOM_0007["Cadastro de pessoas<br/>genérico"]
        DOM_0008["Documentos<br/>genérico"]
    end
    ATR_0007[("Avaliador")]
    ATR_0008[("Bureau de crédito")]
    ATR_0009[("Cartório de Registro de Imóveis")]
    ATR_0010[("Órgão de trânsito")]
    ATR_0011[("Seguradora")]
    ATR_0012[("Sistema do FGTS")]
    DOM_0001 -->|depende de| DOM_0002
    DOM_0001 -->|depende de| DOM_0003
    DOM_0001 -->|depende de| DOM_0007
    DOM_0001 -->|depende de| DOM_0008
    DOM_0002 -.->|depende de| ATR_0008
    DOM_0003 -.->|depende de| ATR_0007
    DOM_0004 -.->|depende de| ATR_0011
    DOM_0004 -->|depende de| DOM_0001
    DOM_0005 -.->|depende de| ATR_0009
    DOM_0005 -.->|depende de| ATR_0010
    DOM_0005 -->|depende de| DOM_0004
    DOM_0006 -.->|depende de| ATR_0012
    DOM_0006 -->|depende de| DOM_0003
    DOM_0006 -->|depende de| DOM_0005
    classDef principal stroke-width:3px,font-weight:bold
    classDef generico stroke-dasharray:4 3
    class DOM_0001 principal
    class DOM_0007,DOM_0008 generico
```

Toda dependência de sistema externo passa por uma camada anticorrupção: códigos e formatos externos não entram no modelo. Os retornos
externos são **assíncronos** e podem chegar repetidos ou fora de ordem.

## Fora de escopo nesta etapa

- Vida do contrato depois de formalizado: cobrança, atraso, renegociação, quitação.
- Cálculo financeiro (taxa, CET, PRICE/SAC) e cálculo de prêmio de seguro: a parcela é um dado da simulação.
- Subsídio habitacional, consórcio, leasing e refinanciamento.

> Os números das regras são **valores de referência do modelo**, parametrizáveis: cada projeto que usar este modelo ajusta os seus.

## <a id="doc-0001"></a>1.1 DOC-0001 - Glossário

Linguagem ubíqua: cada termo tem um só nome e um só significado em conversas, documentos e código.

### Termos a evitar

| Evite | Use | Por quê |
|---|---|---|
| pedido, solicitação, operação | proposta (antes do contrato) ou contrato (depois) | são fases diferentes, com regras diferentes |
| cliente | proponente, coobrigado, participante | "cliente" não diz o papel na dívida |
| aprovado, sem dizer o quê | crédito aprovado, garantia registrada, contrato formalizado | há várias aprovações independentes |
| valor do bem | valor de compra, valor de avaliação ou valor de referência | os três diferem e a regra usa o menor |

### <a id="s-0001"></a>1.1.1 S-0001 - Pessoas

Quem participa da proposta e do contrato.

#### <a id="glo-0001"></a>1.1.1.1 GLO-0001 - Participante

Pessoa que integra a proposta: o proponente ou um coobrigado. Uma proposta tem de 1 a 5 participantes.

#### <a id="glo-0002"></a>1.1.1.2 GLO-0002 - Pessoa física

Participante identificado por CPF, com renda pessoal comprovada.

#### <a id="glo-0003"></a>1.1.1.3 GLO-0003 - Pessoa jurídica

Participante identificado por CNPJ (empresa), com faturamento comprovado. Só pode ser proponente em carro novo, moto nova e imóvel comercial.

#### <a id="glo-0004"></a>1.1.1.4 GLO-0004 - Renda comprovada

Renda mensal (PF) ou faturamento mensal médio dos últimos 12 meses (PJ), aceito após análise dos comprovantes. A da proposta é a soma da dos participantes.

#### <a id="glo-0005"></a>1.1.1.5 GLO-0005 - Incorporadora

Empresa que constrói e vende o imóvel na planta. É o vendedor nesse caso e recebe o dinheiro por medição da obra.

### <a id="s-0002"></a>1.1.2 S-0002 - Bem

O objeto financiado e como ele é classificado e avaliado.

#### <a id="glo-0006"></a>1.1.2.1 GLO-0006 - Bem

Objeto comprado com o financiamento. É também a garantia do contrato.

#### <a id="glo-0007"></a>1.1.2.2 GLO-0007 - Categoria

Família do bem: Veículo ou Imóvel. Define a identificação do bem, a avaliação e onde a garantia é registrada.

#### <a id="glo-0008"></a>1.1.2.3 GLO-0008 - Subtipo

Especialização da categoria: carro e moto (veículo); residencial, comercial e terreno (imóvel). Define prazo, cota de financiamento e documentos.

#### <a id="glo-0009"></a>1.1.2.4 GLO-0009 - Condição do bem

Estado do bem dentro do subtipo: novo ou usado (veículo); pronto ou na planta (imóvel residencial e comercial). Terreno não tem condição.

#### <a id="glo-0010"></a>1.1.2.5 GLO-0010 - Imóvel na planta

Imóvel residencial ou comercial em construção, vendido pela incorporadora, com o dinheiro liberado conforme a obra avança.

#### <a id="glo-0011"></a>1.1.2.6 GLO-0011 - Terreno rural

Terreno fora do perímetro urbano, com documentos próprios: CCIR, ITR e CAR.

#### <a id="glo-0012"></a>1.1.2.7 GLO-0012 - Identificação do bem

Como o bem é reconhecido fora do sistema: chassi, placa e RENAVAM (veículo); matrícula e cartório (imóvel e terreno). Um bem só pode estar em uma proposta ativa.

#### <a id="glo-0013"></a>1.1.2.8 GLO-0013 - Valor de compra

Preço acordado entre comprador e vendedor.

#### <a id="glo-0014"></a>1.1.2.9 GLO-0014 - Valor de avaliação

Valor atribuído ao bem por fonte independente: laudo, tabela de referência ou nota fiscal do novo.

#### <a id="glo-0015"></a>1.1.2.10 GLO-0015 - Valor de referência

O menor entre o valor de compra e o valor de avaliação. Base do limite de financiamento.

#### <a id="glo-0016"></a>1.1.2.11 GLO-0016 - Ano-modelo

Ano de modelo do veículo; base da regra de idade máxima.

#### <a id="glo-0017"></a>1.1.2.12 GLO-0017 - Laudo de avaliação

Documento do avaliador credenciado sobre um imóvel ou terreno: valor, situação e aceitação como garantia.

#### <a id="glo-0018"></a>1.1.2.13 GLO-0018 - Vistoria do veículo

Inspeção do veículo usado vendido por particular: confere identificação, estado e que o vendedor é o dono.

#### <a id="glo-0019"></a>1.1.2.14 GLO-0019 - Medição da obra

Vistoria periódica de um engenheiro que apura o percentual executado da obra do imóvel na planta. Cada medição libera a parcela correspondente.

#### <a id="glo-0020"></a>1.1.2.15 GLO-0020 - Validade da avaliação

Prazo em que a avaliação vale para contratar. Varia por categoria; vencida, o bem é reavaliado.

### <a id="s-0003"></a>1.1.3 S-0003 - Condições

Valores e prazos do financiamento.

#### <a id="glo-0021"></a>1.1.3.1 GLO-0021 - Entrada

Parte do valor de compra paga sem financiamento: recurso próprio e, no imóvel residencial de pessoa física, também FGTS.

#### <a id="glo-0022"></a>1.1.3.2 GLO-0022 - Recurso do FGTS

Parte da entrada paga com saldo do FGTS do proponente, liberado pelo sistema do FGTS diretamente ao vendedor.

#### <a id="glo-0023"></a>1.1.3.3 GLO-0023 - Valor financiado

Valor de compra menos a entrada. É o que o credor paga ao vendedor.

#### <a id="glo-0024"></a>1.1.3.4 GLO-0024 - Cota de financiamento

Valor financiado dividido pelo valor de referência (LTV). Cada tipo de bem tem um máximo.

#### <a id="glo-0025"></a>1.1.3.5 GLO-0025 - Prazo

Quantidade de parcelas mensais. Cada tipo de bem tem mínimo e máximo.

#### <a id="glo-0026"></a>1.1.3.6 GLO-0026 - Parcela

Valor mensal resultante da simulação. Nesta etapa é um dado: o cálculo fica fora do escopo.

#### <a id="glo-0027"></a>1.1.3.7 GLO-0027 - Comprometimento de renda

Parcela dividida pela renda comprovada da proposta. Tem um teto.

#### <a id="glo-0028"></a>1.1.3.8 GLO-0028 - Condições do financiamento

Conjunto de valor de compra, entrada (própria e FGTS), valor financiado, prazo e parcela.

#### <a id="glo-0029"></a>1.1.3.9 GLO-0029 - Seguro exigido

Seguro que o tipo de bem obriga a contratar antes do contrato: MIP, DFI ou seguro do bem. O prestamista é opcional.

#### <a id="glo-0030"></a>1.1.3.10 GLO-0030 - MIP

Seguro de morte e invalidez permanente: quita a dívida do imóvel ou terreno se o devedor morrer ou ficar inválido.

#### <a id="glo-0031"></a>1.1.3.11 GLO-0031 - DFI

Seguro de danos físicos ao imóvel construído (pronto ou na planta).

### <a id="s-0004"></a>1.1.4 S-0004 - Jornada

Etapas e instrumentos da proposta ao contrato.

#### <a id="glo-0032"></a>1.1.4.1 GLO-0032 - Proposta

Pedido de financiamento de um bem por um ou mais participantes. Vai do rascunho até virar contrato ou ser encerrada.

#### <a id="glo-0033"></a>1.1.4.2 GLO-0033 - Enquadramento

Verificação de que a proposta respeita as regras do seu tipo de bem.

#### <a id="glo-0034"></a>1.1.4.3 GLO-0034 - Submissão

Momento em que a proposta completa e enquadrada segue para avaliação e análise de crédito. Antes disso é rascunho.

#### <a id="glo-0035"></a>1.1.4.4 GLO-0035 - Análise de crédito

Decisão sobre a capacidade e o histórico de pagamento dos participantes: aprovada, aprovada com ressalva ou reprovada.

#### <a id="glo-0036"></a>1.1.4.5 GLO-0036 - Ressalva

Aprovação de crédito condicionada a condições diferentes das pedidas (valor menor, entrada maior, prazo menor).

#### <a id="glo-0037"></a>1.1.4.6 GLO-0037 - Contraproposta

Nova versão das condições, emitida pelo credor a partir de uma ressalva. Tem validade; o proponente aceita ou recusa.

#### <a id="glo-0038"></a>1.1.4.7 GLO-0038 - Pendência

Algo que impede a proposta de seguir e pede ação de alguém (documento ilegível, avaliação vencida, cota estourada).

#### <a id="glo-0039"></a>1.1.4.8 GLO-0039 - Exigência

Pendência formal emitida pelo cartório ou pelo órgão de trânsito ao registrar a garantia.

#### <a id="glo-0040"></a>1.1.4.9 GLO-0040 - Contrato

Instrumento assinado que formaliza a dívida e a garantia. Nasce de uma proposta aprovada.

#### <a id="glo-0041"></a>1.1.4.10 GLO-0041 - Alienação fiduciária

Garantia em que o bem fica em nome do credor até a quitação; o devedor tem a posse.

#### <a id="glo-0042"></a>1.1.4.11 GLO-0042 - Gravame

Registro da alienação fiduciária do veículo no sistema nacional de gravames / órgão de trânsito.

#### <a id="glo-0043"></a>1.1.4.12 GLO-0043 - Registro da garantia

Averbação da alienação fiduciária na matrícula (imóvel e terreno) ou inclusão do gravame (veículo).

#### <a id="glo-0044"></a>1.1.4.13 GLO-0044 - Liberação do recurso

Pagamento do valor financiado ao vendedor. Única no bem pronto; por medição no imóvel na planta.

#### <a id="glo-0045"></a>1.1.4.14 GLO-0045 - Prorrogação da obra

Extensão do prazo previsto da obra do imóvel na planta, registrada no contrato até um limite.

## <a id="doc-0002"></a>1.2 DOC-0002 - Dimensões de variação

O que faz as regras variarem. Uma regra que varia por uma dimensão traz uma tabela com um valor para cada item dela, conferida pelo episteme.

### <a id="dim-0001"></a>1.2.1 DIM-0001 - Tipo de bem

Combinação de [categoria](#glo-0007), [subtipo](#glo-0008) e [condição](#glo-0009) do bem. Uma regra pode dar
um valor para um nível intermediário ("Veículo usado") quando ele vale para todos os tipos abaixo.

- Veículo
  - Veículo novo
    - Carro novo
    - Moto nova
  - Veículo usado
    - Carro usado
    - Moto usada
- Imóvel
  - Residencial ou comercial
    - Residencial pronto
    - Residencial na planta
    - Comercial pronto
    - Comercial na planta
  - Terreno
    - Terreno urbano
    - Terreno rural

### <a id="dim-0002"></a>1.2.2 DIM-0002 - Forma do participante

Se o [participante](#glo-0001) é pessoa física ou jurídica. Muda os documentos exigidos e em quais tipos de bem
ele pode ser proponente.

- Pessoa física
- Pessoa jurídica

## <a id="doc-0003"></a>1.3 DOC-0003 - Fontes normativas

Leis e normas em que as regras de negócio se apoiam.

### <a id="f-0001"></a>1.3.1 F-0001 - Lei 9.514/1997

Institui a alienação fiduciária de coisa imóvel: base da garantia do imóvel e do terreno e do seu registro na matrícula, no cartório de registro de imóveis.

### <a id="f-0002"></a>1.3.2 F-0002 - Código Civil, arts. 1.361 e seguintes

Propriedade fiduciária de bem móvel: base da alienação fiduciária do veículo.

### <a id="f-0003"></a>1.3.3 F-0003 - Normas do CONTRAN e do sistema nacional de gravames

Regras para o registro do contrato e a inclusão do gravame do veículo no órgão de trânsito.

### <a id="f-0004"></a>1.3.4 F-0004 - Lei 8.036/1990 (FGTS)

Regras do Fundo de Garantia do Tempo de Serviço, inclusive o uso do saldo na compra de imóvel residencial.

### <a id="f-0005"></a>1.3.5 F-0005 - Lei 13.709/2018 (LGPD)

Lei Geral de Proteção de Dados Pessoais: os dados dos participantes só são tratados para a finalidade do financiamento.

## <a id="doc-0004"></a>1.4 DOC-0004 - Requisitos não funcionais do sistema

Qualidades de negócio que valem para o sistema todo.

### <a id="rnfs-0001"></a>1.4.1 RNFS-0001 - Canais disponíveis 24x7

Proponente, vendedor e correspondente conseguem iniciar, acompanhar e responder propostas a qualquer hora, todos os dias. Etapas com decisão humana (análise manual, exigências) correm em horário comercial.

## <a id="doc-0005"></a>1.5 DOC-0005 - Atores

Pessoas e sistemas que agem na jornada do financiamento.

### <a id="atr-0001"></a>1.5.1 ATR-0001 - Proponente

Quem pede o financiamento e se torna devedor. Pessoa física em todos os tipos; pessoa jurídica só em carro novo, moto nova e imóvel comercial.

### <a id="atr-0002"></a>1.5.2 ATR-0002 - Coobrigado

Compõe renda e responde junto pela dívida. Até 4 por proposta. Em proposta de pessoa jurídica, pode ser pessoa física (sócio, avalista) ou jurídica.

### <a id="atr-0003"></a>1.5.3 ATR-0003 - Vendedor

Quem vende o bem e recebe o dinheiro: concessionária, loja, particular (veículo), incorporadora (na planta) ou proprietário (imóvel pronto e terreno).

### <a id="atr-0004"></a>1.5.4 ATR-0004 - Correspondente

Correspondente bancário ou corretor que inicia e acompanha propostas em nome do proponente, com sua própria carteira.

### <a id="atr-0005"></a>1.5.5 ATR-0005 - Analista

Trata pendências, exigências e casos fora da alçada automática.

### <a id="atr-0006"></a>1.5.6 ATR-0006 - Engenheiro de medição

Vistoria a obra do imóvel na planta e apura o percentual executado.

### <a id="atr-0007"></a>1.5.7 ATR-0007 - Avaliador

Sistema ou empresa externa que define o valor de avaliação: laudo (imóvel e terreno) ou tabela de referência (veículo usado).

### <a id="atr-0008"></a>1.5.8 ATR-0008 - Bureau de crédito

Sistema externo que informa score, restrições e endividamento dos participantes.

### <a id="atr-0009"></a>1.5.9 ATR-0009 - Cartório de Registro de Imóveis

Registra a alienação fiduciária do imóvel ou terreno na matrícula. Pode emitir exigências.

### <a id="atr-0010"></a>1.5.10 ATR-0010 - Órgão de trânsito

Sistema de gravames e órgão de trânsito: incluem o gravame do veículo.

### <a id="atr-0011"></a>1.5.11 ATR-0011 - Seguradora

Emite os seguros exigidos pelo tipo de bem.

### <a id="atr-0012"></a>1.5.12 ATR-0012 - Sistema do FGTS

Decide a elegibilidade e libera o saldo de FGTS usado na entrada.

## <a id="doc-0006"></a>1.6 DOC-0006 - Questões em aberto

Perguntas de negócio que mudam o modelo. Cada questão respondida aponta para a decisão que a responde.

### <a id="q-0001"></a>1.6.1 Q-0001 - O proponente pode ser pessoa jurídica?

Define se o [participante](#glo-0001) tem duas formas e se a análise de crédito e os documentos variam.

### <a id="q-0002"></a>1.6.2 Q-0002 - A proposta aceita coobrigado? Quantos?

Define a composição de renda e quem assina o contrato.

### <a id="q-0003"></a>1.6.3 Q-0003 - Quem pode vender veículo?

Só loja e concessionária, ou também particular?

### <a id="q-0004"></a>1.6.4 Q-0004 - Imóvel na planta entra no modelo?

Muda a liberação do recurso (em parcelas, por obra) e o ciclo de vida do contrato.

### <a id="q-0005"></a>1.6.5 Q-0005 - Terreno é subtipo de imóvel ou categoria própria?

Define o reuso de identificação, avaliação e registro.

### <a id="q-0006"></a>1.6.6 Q-0006 - Terreno rural entra no modelo?

O rural exige documentos próprios (CCIR, ITR, CAR).

### <a id="q-0007"></a>1.6.7 Q-0007 - Libera o dinheiro com o gravame só solicitado?

No veículo novo, a concessionária só entrega o carro com o pagamento; o gravame é incluído no primeiro emplacamento.

### <a id="q-0008"></a>1.6.8 Q-0008 - Seguros obrigatórios entram no modelo?

MIP e DFI no imóvel, prestamista e seguro do bem no veículo.

### <a id="q-0009"></a>1.6.9 Q-0009 - Quem pode iniciar uma proposta?

Proponente, vendedor, correspondente?

### <a id="q-0010"></a>1.6.10 Q-0010 - O que acontece com a aprovação com ressalva?

O proponente aceita no sistema, volta a rascunho ou recebe uma contraproposta?

### <a id="q-0011"></a>1.6.11 Q-0011 - A entrada do imóvel pode usar FGTS ou subsídio?

Muda a composição da entrada e acrescenta um sistema externo.

### <a id="q-0012"></a>1.6.12 Q-0012 - O mesmo bem pode estar em mais de uma proposta ativa?

Evita duas garantias sobre o mesmo bem.

### <a id="q-0013"></a>1.6.13 Q-0013 - A avaliação do bem tem validade?

Se vencer antes da contratação, precisa ser refeita?

### <a id="q-0014"></a>1.6.14 Q-0014 - Qual o limite de prorrogações da obra?

Quantidade de prorrogações ou meses acumulados aceitos antes de virar pendência para o analista.

### <a id="q-0015"></a>1.6.15 Q-0015 - Qual o prazo de validade da contraproposta?

Prazo para o proponente aceitar ou recusar as condições da contraproposta.

### <a id="q-0016"></a>1.6.16 Q-0016 - Qual a validade exata da avaliação em cada categoria?

Sugestão inicial: laudo de imóvel e terreno 180 dias; tabela de referência do veículo usado, o mês da consulta; nota fiscal do novo, 30 dias.

### <a id="q-0017"></a>1.6.17 Q-0017 - Quais documentos são exigidos de participante pessoa jurídica?

Contrato social, balanço, faturamento, procuração dos sócios?

## <a id="doc-0007"></a>1.7 DOC-0007 - Decisões

Decisões de negócio tomadas nas entrevistas de 2026-11-05 (registro completo em docs/definicoes).

### <a id="dec-0001"></a>1.7.1 DEC-0001 - Pessoa jurídica só em carro novo, moto nova e imóvel comercial

Responde [Q-0001](#q-0001).

Veículo usado, imóvel residencial e terreno: só [pessoa física](#glo-0002).

### <a id="dec-0002"></a>1.7.2 DEC-0002 - Até 4 coobrigados; em proposta de PJ, coobrigado PF ou PJ

Responde [Q-0002](#q-0002).

Uma proposta tem de 1 a 5 participantes. A renda da proposta é a soma da renda comprovada de todos.

### <a id="dec-0003"></a>1.7.3 DEC-0003 - Proponente, vendedor e correspondente iniciam a proposta

Responde [Q-0009](#q-0009).

O [ATR-0004](#atr-0004) passa a ser ator próprio, com sua carteira de propostas.

### <a id="dec-0004"></a>1.7.4 DEC-0004 - Vendedor de veículo pode ser loja ou particular

Responde [Q-0003](#q-0003).

Veículo usado vendido por particular exige [vistoria](#glo-0018) e conferência de que o vendedor é o dono.

### <a id="dec-0005"></a>1.7.5 DEC-0005 - Na planta é condição do imóvel residencial e comercial

Responde [Q-0004](#q-0004).

O imóvel ganha a condição pronto × na planta, como novo × usado no veículo. O vendedor do imóvel na planta é a [GLO-0005](#glo-0005). Terreno não tem condição.

### <a id="dec-0006"></a>1.7.6 DEC-0006 - Liberação da planta por medição, com prorrogação limitada

Responde [Q-0004](#q-0004).

Cada [medição](#glo-0019) libera a parcela correspondente ao avanço da obra; a garantia é registrada antes da primeira liberação. Atraso aceita prorrogação até um limite; acima dele vira pendência para o [ATR-0005](#atr-0005).

### <a id="dec-0007"></a>1.7.7 DEC-0007 - Terreno é subtipo de imóvel, urbano e rural

Responde [Q-0005](#q-0005) e [Q-0006](#q-0006).

Reaproveita matrícula, laudo e cartório. O rural tem documentos próprios.

### <a id="dec-0008"></a>1.7.8 DEC-0008 - Nenhuma liberação sem garantia confirmada

Responde [Q-0007](#q-0007).

Vale para todos os tipos de bem, inclusive o veículo novo.

### <a id="dec-0009"></a>1.7.9 DEC-0009 - Ressalva vira contraproposta com validade

Responde [Q-0010](#q-0010).

A [ressalva](#glo-0036) gera uma [contraproposta](#glo-0037). Aceita, a proposta segue com as novas condições; recusada ou vencida, é reprovada.

### <a id="dec-0010"></a>1.7.10 DEC-0010 - Um bem em no máximo uma proposta ativa

Responde [Q-0012](#q-0012).

A identificação do bem (chassi ou matrícula) é única entre propostas ativas.

### <a id="dec-0011"></a>1.7.11 DEC-0011 - A avaliação tem validade por categoria

Responde [Q-0013](#q-0013).

Vencida antes da contratação, a proposta fica com pendência e o bem é reavaliado.

### <a id="dec-0012"></a>1.7.12 DEC-0012 - Seguros entram como exigência, sem cálculo

Responde [Q-0008](#q-0008).

Imóvel pronto ou na planta: [MIP](#glo-0030) e [DFI](#glo-0031). Terreno: MIP. Veículo: seguro do bem. Prestamista opcional em todos. O prêmio não é calculado.

### <a id="dec-0013"></a>1.7.13 DEC-0013 - FGTS na entrada só no imóvel residencial de pessoa física

Responde [Q-0011](#q-0011).

Elegibilidade e saldo são decididos pelo [ATR-0012](#atr-0012). Sem subsídio habitacional.

### <a id="dec-0014"></a>1.7.14 DEC-0014 - Prorrogação da obra até 180 dias acumulados

Responde [Q-0014](#q-0014).

As prorrogações somadas podem estender o prazo previsto da obra em até 180 dias, em quantas vezes for preciso. Acima disso vira pendência para o [ATR-0005](#atr-0005).

### <a id="dec-0015"></a>1.7.15 DEC-0015 - Validade da contraproposta por categoria

Responde [Q-0015](#q-0015).

Veículo: 5 dias corridos, porque a venda é rápida e a loja segura o bem por pouco tempo. Imóvel e terreno: 15 dias corridos.

### <a id="dec-0016"></a>1.7.16 DEC-0016 - Validade da avaliação por tipo de bem

Responde [Q-0016](#q-0016).

Veículo novo: 30 dias (nota fiscal). Veículo usado: o mês da consulta à tabela de referência. Imóvel e terreno: 180 dias (laudo).

### <a id="dec-0017"></a>1.7.17 DEC-0017 - Documentos de participante pessoa jurídica

Responde [Q-0017](#q-0017).

Contrato social e alterações, faturamento dos últimos 12 meses e documentos dos sócios, com procuração quando quem assina não é sócio. O faturamento é a [renda comprovada](#glo-0004) da [pessoa jurídica](#glo-0003).

## <a id="dom-0001"></a>1.8 DOM-0001 - Originação

**Classificação:** Principal

Monta a proposta, enquadra nas regras do tipo de bem e acompanha até virar contrato. É onde mora a variação entre os tipos de bem: o coração do modelo.

### <a id="vis-0001"></a>1.8.1 VIS-0001 - Visão do Domínio

#### Propósito e fronteiras

Recebe o pedido de financiamento de um bem, reúne participantes, bem, condições e documentos, aplica as regras
do tipo de bem e conduz a proposta pela avaliação e pela análise de crédito até a aprovação. Entrega a proposta
aprovada à Contratação.

**Depende de:** [Crédito](#dom-0002), [Avaliação](#dom-0003), [Cadastro de pessoas](#dom-0007) e [Documentos](#dom-0008).

**Guarda:** o agregado [AGR-0001](#agr-0001) e o seu [CV-0001](#cv-0001).

**Atores:** [ATR-0001](#atr-0001), [ATR-0002](#atr-0002), [ATR-0003](#atr-0003), [ATR-0004](#atr-0004), [ATR-0005](#atr-0005).

**Sujeito a:** [RNFS-0001](#rnfs-0001).

#### Jornada

```mermaid
flowchart LR
    A[Montagem<br/>da proposta] --> B[Submissão e<br/>enquadramento]
    B --> C[Avaliação<br/>do bem]
    B --> D[Análise<br/>de crédito]
    C --> E[Aprovação]
    D --> E
    D -. ressalva .-> X[Contraproposta]
    X -. aceita .-> E
    E --> F[Contratação]
```

Avaliação e análise de crédito correm em paralelo; a proposta só é aprovada quando as duas concluíram.

#### Jornada em eventos

Gerada pelo episteme (event storming): eventos em ordem, quem os provoca e o que disparam.

```mermaid
flowchart LR
    subgraph DOM_0001_es["Originação"]
        direction LR
        EVT_0001["PropostaIniciada"]:::evento
        AGR_0001{{"Proposta"}}:::agregado
        UC_0001["Iniciar proposta"]:::comando
        ATR_0001(["Proponente"]):::ator
        ATR_0003(["Vendedor"]):::ator
        ATR_0004(["Correspondente"]):::ator
        EVT_0002["PropostaSubmetida"]:::evento
        UC_0005["Submeter proposta"]:::comando
        UC_0011["Analisar crédito"]:::comando
        UC_0012["Avaliar bem"]:::comando
        EVT_0003["PropostaComPendencia"]:::evento
        UC_0006["Decidir proposta"]:::comando
        EVT_0004["PendenciaResolvida"]:::evento
        UC_0008["Resolver pendência"]:::comando
        ATR_0005(["Analista"]):::ator
        EVT_0005["PropostaAprovada"]:::evento
        UC_0014["Emitir contrato"]:::comando
        EVT_0006["PropostaReprovada"]:::evento
        EVT_0007["PropostaCancelada"]:::evento
        UC_0010["Cancelar proposta"]:::comando
        EVT_0008["PropostaExpirada"]:::evento
        UC_0007["Expirar proposta"]:::comando
        EVT_0009["ContrapropostaEmitida"]:::evento
        EVT_0010["ContrapropostaAceita"]:::evento
        UC_0009["Responder contraproposta"]:::comando
        EVT_0011["ContrapropostaRecusada"]:::evento
    end
    AGR_0001 -.- EVT_0001
    UC_0001 --> EVT_0001
    ATR_0001 --- UC_0001
    ATR_0003 --- UC_0001
    ATR_0004 --- UC_0001
    EVT_0001 ~~~ EVT_0002
    AGR_0001 -.- EVT_0002
    UC_0005 --> EVT_0002
    ATR_0001 --- UC_0005
    ATR_0004 --- UC_0005
    EVT_0002 ~~~ EVT_0003
    AGR_0001 -.- EVT_0003
    UC_0006 --> EVT_0003
    EVT_0003 ~~~ EVT_0004
    AGR_0001 -.- EVT_0004
    UC_0008 --> EVT_0004
    ATR_0001 --- UC_0008
    ATR_0004 --- UC_0008
    ATR_0005 --- UC_0008
    EVT_0004 ~~~ EVT_0005
    AGR_0001 -.- EVT_0005
    UC_0006 --> EVT_0005
    EVT_0005 ~~~ EVT_0006
    AGR_0001 -.- EVT_0006
    UC_0006 --> EVT_0006
    EVT_0006 ~~~ EVT_0007
    AGR_0001 -.- EVT_0007
    UC_0010 --> EVT_0007
    ATR_0001 --- UC_0010
    EVT_0007 ~~~ EVT_0008
    AGR_0001 -.- EVT_0008
    UC_0007 --> EVT_0008
    EVT_0008 ~~~ EVT_0009
    AGR_0001 -.- EVT_0009
    EVT_0009 ~~~ EVT_0010
    AGR_0001 -.- EVT_0010
    UC_0009 --> EVT_0010
    ATR_0001 --- UC_0009
    EVT_0010 ~~~ EVT_0011
    AGR_0001 -.- EVT_0011
    UC_0009 --> EVT_0011
    EVT_0002 -.->|sempre que| UC_0011
    EVT_0002 -.->|sempre que| UC_0012
    EVT_0005 -.->|sempre que| UC_0014
    classDef evento fill:#fdba74,stroke:#c2410c,color:#1c1917
    classDef comando fill:#93c5fd,stroke:#1d4ed8,color:#0c0a09
    classDef ator fill:#fde047,stroke:#a16207,color:#1c1917
    classDef agregado fill:#fef3c7,stroke:#b45309,color:#1c1917
    classDef semOrigem stroke-dasharray:5 4,stroke-width:2px
```

### <a id="agr-0001"></a>1.8.2 AGR-0001 - Proposta

Fronteira de consistência da originação: [participantes](#glo-0001), [bem](#glo-0006), [condições](#glo-0028),
documentos e os resultados da avaliação e do crédito **como chegaram à proposta**.

#### Composição

| Elemento | Natureza | Observação |
|---|---|---|
| Participante | entidade | 1 a 5; um é o proponente; PF ou PJ |
| Bem | entidade | categoria, subtipo e condição fixados ao iniciar |
| Condições | objeto de valor | versionadas: cada contraproposta é uma nova versão |
| Documento | entidade | exigidos pelo tipo de bem e pela forma do participante |
| Avaliação | objeto de valor | valor, fonte, data e validade, vindos da Avaliação |
| Análise de crédito | objeto de valor | resultado e ressalvas, vindos do Crédito |
| Vendedor | referência | por identificador |

#### Invariantes

1. Categoria, subtipo e condição nunca mudam depois de iniciada.
2. Só o rascunho e a proposta com pendência aceitam alteração de dados.
3. Participantes: [RN-0001](#rn-0001) e [RN-0002](#rn-0002).
4. Bem: [RN-0004](#rn-0004).
5. Para submeter: completa e enquadrada ([RN-0018](#rn-0018)).
6. Toda mudança de valor, entrada, prazo ou avaliação dispara novo enquadramento.
7. Só é aprovada com avaliação válida ([RN-0022](#rn-0022)) e crédito aprovado, ambos posteriores à última versão das condições.
8. Uma proposta gera no máximo um contrato.

**Emite:** [EVT-0001](#evt-0001), [EVT-0002](#evt-0002), [EVT-0003](#evt-0003), [EVT-0004](#evt-0004), [EVT-0019](#evt-0019), [EVT-0005](#evt-0005), [EVT-0006](#evt-0006), [EVT-0007](#evt-0007), [EVT-0008](#evt-0008), [EVT-0009](#evt-0009), [EVT-0010](#evt-0010), [EVT-0011](#evt-0011).

Decisões que moldam o agregado: [DEC-0002](#dec-0002), [DEC-0009](#dec-0009), [DEC-0010](#dec-0010).

#### Por que a proposta e o contrato são agregados separados

- A proposta muda muito e é editada por pessoas; o contrato é estável e reage a sistemas externos.
- Depois da emissão, nenhuma invariante do contrato precisa da proposta mudando junto.
- A proposta pode terminar sem contrato (reprovada, expirada, cancelada), que é a maioria dos casos.

### <a id="cv-0001"></a>1.8.3 CV-0001 - Ciclo de vida da proposta

Estados da [proposta](#agr-0001) e o evento de cada transição.

```mermaid
stateDiagram-v2
    [*] --> Rascunho: PropostaIniciada
    Rascunho --> EmAnalise: PropostaSubmetida
    Rascunho --> Cancelada: PropostaCancelada
    EmAnalise --> ComPendencia: PropostaComPendencia
    ComPendencia --> EmAnalise: PendenciaResolvida
    EmAnalise --> EmContraproposta: ContrapropostaEmitida
    EmContraproposta --> EmAnalise: ContrapropostaAceita
    EmContraproposta --> Reprovada: ContrapropostaRecusada (recusada ou vencida)
    EmAnalise --> Aprovada: PropostaAprovada
    EmAnalise --> Reprovada: PropostaReprovada
    ComPendencia --> Cancelada: PropostaCancelada
    Aprovada --> ComPendencia: AvaliacaoVencida
    Aprovada --> Contratada: ContratoEmitido
    Aprovada --> Expirada: PropostaExpirada
    Aprovada --> Cancelada: PropostaCancelada
    Reprovada --> [*]
    Cancelada --> [*]
    Expirada --> [*]
    Contratada --> [*]
```

| De | Para | Evento | Regra que guarda |
|---|---|---|---|
| Rascunho | Em análise | [EVT-0002](#evt-0002) | [RN-0018](#rn-0018) |
| Em análise | Com pendência | [EVT-0003](#evt-0003) | [RN-0006](#rn-0006), [RN-0022](#rn-0022) |
| Com pendência | Em análise | [EVT-0004](#evt-0004) | [RN-0018](#rn-0018) |
| Em contraproposta | Reprovada | [EVT-0011](#evt-0011) | [RN-0020](#rn-0020) |
| Em análise | Em contraproposta | [EVT-0009](#evt-0009) | [RN-0020](#rn-0020) |
| Em análise | Aprovada | [EVT-0005](#evt-0005) | [RN-0019](#rn-0019) |
| Em análise | Reprovada | [EVT-0006](#evt-0006) | [RN-0019](#rn-0019) |
| Aprovada | Expirada | [EVT-0008](#evt-0008) | [RN-0021](#rn-0021) |

### <a id="s-0005"></a>1.8.4 S-0005 - Eventos

Fatos do domínio da originação.

#### <a id="evt-0001"></a>1.8.4.1 EVT-0001 - PropostaIniciada

Uma [proposta](#glo-0032) foi aberta para um bem de categoria, subtipo e condição definidos. Provocado por [ATR-0001](#atr-0001), [ATR-0003](#atr-0003) ou [ATR-0004](#atr-0004).

#### <a id="evt-0002"></a>1.8.4.2 EVT-0002 - PropostaSubmetida

A proposta completa e enquadrada foi submetida. Dispara [UC-0012](#uc-0012) e [UC-0011](#uc-0011).

#### <a id="evt-0003"></a>1.8.4.3 EVT-0003 - PropostaComPendencia

A proposta não pode seguir até alguém agir: cota estourada após a avaliação, avaliação vencida, documento recusado. Ver [RN-0006](#rn-0006).

#### <a id="evt-0004"></a>1.8.4.4 EVT-0004 - PendenciaResolvida

Quem agiu resolveu a pendência (documento reenviado, entrada recomposta, bem reavaliado) e a proposta volta à análise, reenquadrada.

#### <a id="evt-0005"></a>1.8.4.5 EVT-0005 - PropostaAprovada

Bem avaliado e crédito aprovado, com enquadramento válido. Começa a contar a validade ([RN-0021](#rn-0021)). Dispara [UC-0014](#uc-0014).

#### <a id="evt-0006"></a>1.8.4.6 EVT-0006 - PropostaReprovada

Crédito reprovado, bem recusado como garantia ou contraproposta recusada ou vencida.

#### <a id="evt-0007"></a>1.8.4.7 EVT-0007 - PropostaCancelada

O [ATR-0001](#atr-0001) desistiu antes do contrato.

#### <a id="evt-0008"></a>1.8.4.8 EVT-0008 - PropostaExpirada

A proposta aprovada não foi contratada dentro da validade.

#### <a id="evt-0009"></a>1.8.4.9 EVT-0009 - ContrapropostaEmitida

O crédito aprovou com ressalva e o credor emitiu nova versão das condições, com validade ([RN-0020](#rn-0020)).

#### <a id="evt-0010"></a>1.8.4.10 EVT-0010 - ContrapropostaAceita

O [ATR-0001](#atr-0001) aceitou a contraproposta; a proposta segue com as novas condições.

#### <a id="evt-0011"></a>1.8.4.11 EVT-0011 - ContrapropostaRecusada

O proponente recusou a contraproposta, ou ela venceu. A proposta é reprovada.

### <a id="doc-0008"></a>1.8.5 DOC-0008 - Regras de Negócio

Regras da originação: comuns a todos os bens e variáveis por tipo de bem.

#### <a id="s-0006"></a>1.8.5.1 S-0006 - Regras comuns

Valem para qualquer tipo de bem.

##### <a id="rn-0001"></a>1.8.5.1.1 RN-0001 - Participantes da proposta

Uma proposta tem um [ATR-0001](#atr-0001) e até 4 [coobrigados](#atr-0002): de 1 a 5 [participantes](#glo-0001), sem repetir CPF ou CNPJ ([RN-0033](#rn-0033)). Pessoa física é maior e capaz. Os dados pessoais servem só ao financiamento ([F-0005](#f-0005)). Decisão: [DEC-0002](#dec-0002).

##### <a id="rn-0002"></a>1.8.5.1.2 RN-0002 - Pessoa jurídica como proponente

O proponente pode ser [pessoa jurídica](#glo-0003) só em carro novo, moto nova e imóvel comercial (pronto ou na planta). Em proposta de PJ, o coobrigado pode ser PF ou PJ. Decisão: [DEC-0001](#dec-0001).

##### <a id="rn-0003"></a>1.8.5.1.3 RN-0003 - Vendedor identificado

O [ATR-0003](#atr-0003) é identificado e diferente do proponente. Veículo usado vendido por particular exige [vistoria](#glo-0018) aprovada. [Imóvel na planta](#glo-0010) é vendido por [incorporadora](#glo-0005). Decisões: [DEC-0004](#dec-0004), [DEC-0005](#dec-0005).

##### <a id="rn-0004"></a>1.8.5.1.4 RN-0004 - Bem em uma só proposta ativa

A [identificação do bem](#glo-0012) é única entre propostas ativas (rascunho, em análise, com pendência, em contraproposta, aprovada). Decisão: [DEC-0010](#dec-0010).

##### <a id="rn-0005"></a>1.8.5.1.5 RN-0005 - Valor financiado

[Valor financiado](#glo-0023) = [valor de compra](#glo-0013) − [entrada](#glo-0021), e é maior que zero. A entrada soma recurso próprio e, quando permitido, FGTS ([RN-0015](#rn-0015)).

##### <a id="rn-0006"></a>1.8.5.1.6 RN-0006 - Limite da cota de financiamento

Valor financiado ≤ cota máxima do tipo de bem × [valor de referência](#glo-0015) (o menor entre compra e avaliação). A cota máxima de cada tipo está em [RN-0010](#rn-0010).

**Detalhes/Exemplo:**
Moto usada: compra R$ 20.000, tabela R$ 18.000, entrada R$ 4.000. Referência = 18.000; financiado 16.000 = 88,9% > 70% → não enquadra. A entrada mínima passa a ser R$ 7.400.

##### <a id="rn-0007"></a>1.8.5.1.7 RN-0007 - Prazo dentro da faixa do tipo

O [prazo](#glo-0025) fica entre o mínimo e o máximo do tipo de bem ([RN-0010](#rn-0010)).

##### <a id="rn-0008"></a>1.8.5.1.8 RN-0008 - Comprometimento de renda

[Comprometimento de renda](#glo-0027): [parcela](#glo-0026) ≤ 30% da [renda comprovada](#glo-0004) somada dos participantes.

##### <a id="rn-0009"></a>1.8.5.1.9 RN-0009 - Documentos obrigatórios

Os documentos exigidos pela categoria, pelo subtipo e pela forma de cada participante (PF ou PJ) estão presentes, sem documento repetido. Listas em [RN-0016](#rn-0016) e [RN-0017](#rn-0017).

#### <a id="s-0007"></a>1.8.5.2 S-0007 - Regras por tipo de bem

O que varia por categoria, subtipo e condição. Uma regra é definida no nível mais alto em que não varia.

##### <a id="rn-0010"></a>1.8.5.2.1 RN-0010 - Parâmetros por tipo de bem

Varia por [DIM-0001](#dim-0001). **Valores de referência do modelo**, parametrizáveis por projeto.

| Parâmetro | Carro novo | Carro usado | Moto nova | Moto usada | Residencial pronto | Residencial na planta | Comercial pronto | Comercial na planta | Terreno urbano | Terreno rural |
|---|---|---|---|---|---|---|---|---|---|---|
| Prazo (meses) | 12–60 | 12–60 | 12–48 | 12–36 | 60–420 | 60–420 | 60–240 | 60–240 | 60–180 | 60–120 |
| Cota máxima | 90% | 80% | 90% | 70% | 80% | 80% | 70% | 70% | 60% | 50% |
| Aceita PJ | sim | não | sim | não | não | não | sim | sim | não | não |
| FGTS na entrada | não | não | não | não | sim | sim | não | não | não | não |
| Idade máxima ao fim do contrato | — | 15 anos | — | 10 anos | — | — | — | — | — | — |
| Liberação | única | única | única | única | única | por medição | única | por medição | única | única |

Cota de financiamento = [LTV](#glo-0024). Regras que usam esta tabela: [RN-0006](#rn-0006), [RN-0007](#rn-0007), [RN-0002](#rn-0002), [RN-0015](#rn-0015), [RN-0011](#rn-0011).

##### <a id="rn-0011"></a>1.8.5.2.2 RN-0011 - Idade máxima do veículo usado

No fim do contrato, o veículo usado tem no máximo 15 anos (carro) ou 10 anos (moto), contados do [ano-modelo](#glo-0016).

**Detalhes/Exemplo:**
Carro usado, ano-modelo 2016, prazo de 60 meses contratado em 2026: termina em 2031 = 2016 + 15 → enquadra, no limite.

##### <a id="rn-0012"></a>1.8.5.2.3 RN-0012 - Idade do proponente no imóvel

No imóvel e no terreno, a idade do proponente pessoa física somada ao [prazo](#glo-0025) não passa de 80 anos e 6 meses.

**Detalhes/Exemplo:**
Proponente com 60 anos pedindo 420 meses: 60 + 35 = 95 anos → não enquadra; o prazo máximo possível é de 246 meses.

##### <a id="rn-0013"></a>1.8.5.2.4 RN-0013 - Veículo usado sem restrição

O veículo usado não tem restrição, bloqueio ou outro gravame no [ATR-0010](#atr-0010).

##### <a id="rn-0014"></a>1.8.5.2.5 RN-0014 - Terreno sem construção

O terreno não tem construção averbada na matrícula. O [terreno rural](#glo-0011) apresenta CCIR, ITR e CAR. Decisão: [DEC-0007](#dec-0007).

##### <a id="rn-0015"></a>1.8.5.2.6 RN-0015 - Uso do FGTS na entrada

Só o imóvel residencial (pronto ou na planta) de proponente [pessoa física](#glo-0002) aceita [recurso do FGTS](#glo-0022) na entrada. A elegibilidade e o saldo são decididos pelo [ATR-0012](#atr-0012). Base legal: [F-0004](#f-0004). Decisão: [DEC-0013](#dec-0013).

##### <a id="rn-0016"></a>1.8.5.2.7 RN-0016 - Documentos do bem por tipo

Varia por [DIM-0001](#dim-0001).

| Tipo | Documentos do bem |
|---|---|
| Veículo novo | nota fiscal ou pedido da concessionária |
| Veículo usado | CRLV; laudo de vistoria se vendido por particular |
| Residencial pronto / Comercial pronto | matrícula atualizada, habite-se (comercial), certidões do vendedor |
| Residencial na planta / Comercial na planta | contrato com a incorporadora, memorial de incorporação, cronograma da obra |
| Terreno urbano | matrícula, IPTU, certidões do vendedor |
| Terreno rural | matrícula, CCIR, ITR, CAR, certidões do vendedor |

##### <a id="rn-0017"></a>1.8.5.2.8 RN-0017 - Documentos dos participantes

Varia por [DIM-0001](#dim-0001) e por [DIM-0002](#dim-0002): cada cruzamento diz o que o participante entrega.
"—" é onde a [pessoa jurídica](#glo-0003) não pode ser proponente ([RN-0002](#rn-0002), [DEC-0001](#dec-0001)). Documentos da PJ: [DEC-0017](#dec-0017).

| Tipo | Pessoa física | Pessoa jurídica |
|---|---|---|
| Veículo novo | identidade, comprovante de renda, comprovante de residência | contrato social, faturamento de 12 meses, documentos dos sócios e procuração |
| Veículo usado | identidade, comprovante de renda, comprovante de residência | — |
| Residencial pronto / Residencial na planta | identidade, comprovante de renda, comprovante de residência, estado civil | — |
| Comercial pronto / Comercial na planta | identidade, comprovante de renda, comprovante de residência, estado civil | contrato social, faturamento de 12 meses, documentos dos sócios e procuração |
| Terreno | identidade, comprovante de renda, comprovante de residência, estado civil | — |

#### <a id="s-0008"></a>1.8.5.3 S-0008 - Regras da jornada

Submissão, aprovação, contraproposta e validade.

##### <a id="rn-0018"></a>1.8.5.3.1 RN-0018 - Enquadramento para submeter

Para a [submissão](#glo-0034), o [enquadramento](#glo-0033) exige a proposta completa e respeitando [RN-0001](#rn-0001), [RN-0002](#rn-0002), [RN-0003](#rn-0003), [RN-0004](#rn-0004), [RN-0005](#rn-0005), [RN-0006](#rn-0006), [RN-0007](#rn-0007), [RN-0008](#rn-0008), [RN-0009](#rn-0009) e as regras do seu tipo de bem: [RN-0011](#rn-0011), [RN-0012](#rn-0012), [RN-0013](#rn-0013), [RN-0014](#rn-0014) e [RN-0015](#rn-0015).

##### <a id="rn-0019"></a>1.8.5.3.2 RN-0019 - Aprovação da proposta

A proposta é aprovada quando há avaliação válida aceita como garantia **e** crédito aprovado, e o enquadramento continua válido com o valor de avaliação. Crédito reprovado ou bem recusado como garantia reprova a proposta.

##### <a id="rn-0020"></a>1.8.5.3.3 RN-0020 - Contraproposta

Crédito aprovado com [ressalva](#glo-0036) gera uma [contraproposta](#glo-0037) com validade. Aceita, as
novas condições são reenquadradas e a proposta segue; recusada ou vencida, a proposta é reprovada.
Decisões: [DEC-0009](#dec-0009), [DEC-0015](#dec-0015). Varia por [DIM-0001](#dim-0001).

| Tipo | Validade da contraproposta |
|---|---|
| Veículo | 5 dias corridos |
| Imóvel | 15 dias corridos |

##### <a id="rn-0021"></a>1.8.5.3.4 RN-0021 - Validade da proposta aprovada

Proposta aprovada vale 30 dias para virar contrato; depois expira.

##### <a id="rn-0022"></a>1.8.5.3.5 RN-0022 - Avaliação válida para contratar

A avaliação precisa estar dentro da [validade](#glo-0020) do seu tipo de bem ([RN-0023](#rn-0023)) na emissão do contrato; vencida, a proposta fica com pendência e o bem é reavaliado. Decisões: [DEC-0011](#dec-0011), [DEC-0016](#dec-0016).

### <a id="s-0009"></a>1.8.6 S-0009 - Casos de uso

Casos de uso da originação.

#### <a id="uc-0001"></a>1.8.6.1 UC-0001 - Iniciar proposta

**Atores:** [ATR-0001](#atr-0001), [ATR-0003](#atr-0003) ou [ATR-0004](#atr-0004). Decisão: [DEC-0003](#dec-0003).

**Fluxo principal**
1. O ator escolhe a categoria, o subtipo e a condição do bem.
2. O sistema cria a proposta em rascunho com esses dados fixos.

**Pós-condição:** proposta em rascunho. Emite [EVT-0001](#evt-0001).

#### <a id="uc-0002"></a>1.8.6.2 UC-0002 - Informar participantes

**Atores:** [ATR-0001](#atr-0001) ou [ATR-0004](#atr-0004).

**Fluxo principal**
1. O ator informa o proponente (PF ou PJ, conforme [RN-0002](#rn-0002)) e até 4 coobrigados.
2. O sistema confere [RN-0001](#rn-0001) e anexa os documentos de cada um.

**Fluxo alternativo:** proponente PJ num tipo de bem que não aceita PJ → o sistema recusa e explica a regra.

#### <a id="uc-0003"></a>1.8.6.3 UC-0003 - Informar bem e vendedor

**Atores:** [ATR-0001](#atr-0001), [ATR-0003](#atr-0003) ou [ATR-0004](#atr-0004).

**Fluxo principal**
1. O ator informa a identificação do bem, o valor de compra e o vendedor.
2. O sistema confere [RN-0004](#rn-0004) e [RN-0003](#rn-0003).
3. Para veículo usado de particular, o sistema exige a vistoria.

#### <a id="uc-0004"></a>1.8.6.4 UC-0004 - Simular e escolher condições

**Ator:** [ATR-0001](#atr-0001).

**Fluxo principal**
1. O proponente informa entrada (própria e, se permitido, FGTS — [RN-0015](#rn-0015)) e prazo.
2. O sistema mostra a parcela simulada e o enquadramento em [RN-0006](#rn-0006), [RN-0007](#rn-0007) e [RN-0008](#rn-0008).

#### <a id="uc-0005"></a>1.8.6.5 UC-0005 - Submeter proposta

**Ator:** [ATR-0001](#atr-0001) ou [ATR-0004](#atr-0004). Faz a [submissão](#glo-0034) da proposta.

**Fluxo principal**
1. O ator submete a proposta em rascunho.
2. O sistema aplica [RN-0018](#rn-0018).
3. O sistema muda a proposta para em análise. Emite [EVT-0002](#evt-0002).

**Fluxo alternativo:** incompleta ou fora do enquadramento → continua em rascunho, com a lista do que falta.

#### <a id="uc-0006"></a>1.8.6.6 UC-0006 - Decidir proposta

**Ator:** nenhum: é automático, disparado pelos resultados da avaliação e do crédito.

**Fluxo principal**
1. Com bem avaliado e crédito aprovado, o sistema confere [RN-0019](#rn-0019) e [RN-0018](#rn-0018) com o valor de avaliação.
2. A proposta é aprovada. Emite [EVT-0005](#evt-0005).

**Fluxos alternativos**
- Crédito reprovado ou bem recusado como garantia → a proposta é reprovada. Emite [EVT-0006](#evt-0006).
- Cota estourada com o valor de avaliação → emite [EVT-0003](#evt-0003).
- Crédito aprovado com ressalva → segue para a contraproposta ([RN-0020](#rn-0020)).

#### <a id="uc-0007"></a>1.8.6.7 UC-0007 - Expirar proposta

**Ator:** nenhum: acontece pela passagem do tempo.

**Fluxo principal:** a proposta aprovada passa da validade sem contrato ([RN-0021](#rn-0021)) e é encerrada. Emite [EVT-0008](#evt-0008).

#### <a id="uc-0008"></a>1.8.6.8 UC-0008 - Resolver pendência

**Atores:** [ATR-0001](#atr-0001), [ATR-0004](#atr-0004) ou [ATR-0005](#atr-0005).

**Fluxo principal**
1. O ator vê a [pendência](#glo-0038) e age: reenvia documento, recompõe a entrada ou pede nova avaliação.
2. O sistema reenquadra ([RN-0018](#rn-0018)) e a proposta volta à análise. Emite [EVT-0004](#evt-0004).

#### <a id="uc-0009"></a>1.8.6.9 UC-0009 - Responder contraproposta

**Ator:** [ATR-0001](#atr-0001).

**Fluxo principal**
1. O proponente vê as condições da contraproposta e a validade ([RN-0020](#rn-0020)).
2. O proponente aceita; o sistema reenquadra. Emite [EVT-0010](#evt-0010).

**Fluxo alternativo:** recusa ou vencimento → a proposta é reprovada. Emite [EVT-0011](#evt-0011).

#### <a id="uc-0010"></a>1.8.6.10 UC-0010 - Cancelar proposta

**Ator:** [ATR-0001](#atr-0001).

**Pré-condição:** proposta ainda sem contrato.

**Fluxo principal:** o proponente desiste; o sistema encerra a proposta. Emite [EVT-0007](#evt-0007).

## <a id="dom-0002"></a>1.9 DOM-0002 - Crédito

**Classificação:** Suporte

Decide sobre os participantes. A decisão é em boa parte terceirizada (bureau, motor de crédito); o domínio orquestra e guarda o resultado.

### <a id="vis-0002"></a>1.9.1 VIS-0002 - Visão do Domínio

#### Propósito e fronteiras

Recebe a proposta submetida, consulta o [ATR-0008](#atr-0008) e decide: aprovada, aprovada com ressalva ou reprovada.
A decisão volta para a Originação como fato.

**Depende de:** [ATR-0008](#atr-0008).

**Atores:** [ATR-0008](#atr-0008), [ATR-0005](#atr-0005). **Eventos:** [EVT-0012](#evt-0012), [EVT-0013](#evt-0013), [EVT-0014](#evt-0014), [EVT-0015](#evt-0015).

#### Jornada em eventos

Gerada pelo episteme (event storming): eventos em ordem, quem os provoca e o que disparam.

```mermaid
flowchart LR
    subgraph DOM_0002_es["Crédito"]
        direction LR
        EVT_0012["AnaliseCreditoSolicitada"]:::evento
        UC_0011["Analisar crédito"]:::comando
        ATR_0005(["Analista"]):::ator
        ATR_0008(["Bureau de crédito"]):::ator
        EVT_0013["CreditoAprovado"]:::evento
        UC_0006["Decidir proposta"]:::comando
        EVT_0014["CreditoAprovadoComRessalva"]:::evento
        UC_0009["Responder contraproposta"]:::comando
        EVT_0015["CreditoReprovado"]:::evento
    end
    UC_0011 --> EVT_0012
    ATR_0005 --- UC_0011
    ATR_0008 --- UC_0011
    EVT_0012 ~~~ EVT_0013
    UC_0011 --> EVT_0013
    EVT_0013 ~~~ EVT_0014
    UC_0011 --> EVT_0014
    EVT_0014 ~~~ EVT_0015
    UC_0011 --> EVT_0015
    EVT_0013 -.->|sempre que| UC_0006
    EVT_0014 -.->|sempre que| UC_0009
    EVT_0015 -.->|sempre que| UC_0006
    classDef evento fill:#fdba74,stroke:#c2410c,color:#1c1917
    classDef comando fill:#93c5fd,stroke:#1d4ed8,color:#0c0a09
    classDef ator fill:#fde047,stroke:#a16207,color:#1c1917
    classDef agregado fill:#fef3c7,stroke:#b45309,color:#1c1917
    classDef semOrigem stroke-dasharray:5 4,stroke-width:2px
```

### <a id="s-0010"></a>1.9.2 S-0010 - Eventos

Fatos do domínio de crédito.

#### <a id="evt-0012"></a>1.9.2.1 EVT-0012 - AnaliseCreditoSolicitada

A análise dos participantes foi pedida após a submissão.

#### <a id="evt-0013"></a>1.9.2.2 EVT-0013 - CreditoAprovado

Os participantes têm capacidade e histórico para as condições pedidas. Dispara [UC-0006](#uc-0006).

#### <a id="evt-0014"></a>1.9.2.3 EVT-0014 - CreditoAprovadoComRessalva

Aprovação condicionada a outras condições. Dispara [UC-0009](#uc-0009) por meio da contraproposta ([RN-0020](#rn-0020)).

#### <a id="evt-0015"></a>1.9.2.4 EVT-0015 - CreditoReprovado

Os participantes não têm capacidade ou histórico. Dispara [UC-0006](#uc-0006), que reprova a proposta.

### <a id="rnfd-0001"></a>1.9.3 RNFD-0001 - Resposta do crédito em até 1 dia útil

O proponente recebe a decisão de crédito (aprovada, com ressalva ou reprovada) em até 1 dia útil após a submissão. Casos em análise manual podem levar mais, e o proponente é avisado.

### <a id="s-0011"></a>1.9.4 S-0011 - Casos de uso

Casos de uso do crédito.

#### <a id="uc-0011"></a>1.9.4.1 UC-0011 - Analisar crédito

**Atores:** [ATR-0008](#atr-0008), [ATR-0005](#atr-0005). Faz a [análise de crédito](#glo-0035) dos participantes, no prazo de [RNFD-0001](#rnfd-0001).

**Fluxo principal**
1. O sistema consulta o bureau para cada participante. Emite [EVT-0012](#evt-0012).
2. O sistema confere [RN-0008](#rn-0008) com a renda comprovada.
3. O sistema registra a decisão. Emite [EVT-0013](#evt-0013), [EVT-0014](#evt-0014) ou [EVT-0015](#evt-0015).

**Fluxo alternativo:** fora da alçada automática → o analista decide.

## <a id="dom-0003"></a>1.10 DOM-0003 - Avaliação

**Classificação:** Suporte

Define o valor de avaliação do bem e, no imóvel na planta, mede o avanço da obra.

### <a id="vis-0003"></a>1.10.1 VIS-0003 - Visão do Domínio

#### Propósito e fronteiras

Define o [valor de avaliação](#glo-0014) pela fonte certa para cada tipo de bem e controla a validade
da avaliação. No imóvel na planta, faz as [medições](#glo-0019) que liberam as parcelas.

**Depende de:** [ATR-0007](#atr-0007).

**Atores:** [ATR-0007](#atr-0007), [ATR-0006](#atr-0006).

#### Jornada em eventos

Gerada pelo episteme (event storming): eventos em ordem, quem os provoca e o que disparam.

```mermaid
flowchart LR
    subgraph DOM_0003_es["Avaliação"]
        direction LR
        EVT_0016["AvaliacaoSolicitada"]:::evento
        UC_0012["Avaliar bem"]:::comando
        ATR_0007(["Avaliador"]):::ator
        EVT_0017["BemAvaliado"]:::evento
        UC_0006["Decidir proposta"]:::comando
        EVT_0018["BemRecusadoComoGarantia"]:::evento
        EVT_0019["AvaliacaoVencida"]:::evento
        AGR_0001{{"Proposta"}}:::agregado
        EVT_0020["MedicaoRealizada"]:::evento
        UC_0013["Medir obra"]:::comando
        ATR_0006(["Engenheiro de medição"]):::ator
        UC_0023["Liberar parcela da obra"]:::comando
    end
    UC_0012 --> EVT_0016
    ATR_0007 --- UC_0012
    EVT_0016 ~~~ EVT_0017
    UC_0012 --> EVT_0017
    EVT_0017 ~~~ EVT_0018
    UC_0012 --> EVT_0018
    EVT_0018 ~~~ EVT_0019
    AGR_0001 -.- EVT_0019
    EVT_0019 ~~~ EVT_0020
    UC_0013 --> EVT_0020
    ATR_0006 --- UC_0013
    EVT_0017 -.->|sempre que| UC_0006
    EVT_0018 -.->|sempre que| UC_0006
    EVT_0019 -.->|sempre que| UC_0012
    EVT_0020 -.->|sempre que| UC_0023
    classDef evento fill:#fdba74,stroke:#c2410c,color:#1c1917
    classDef comando fill:#93c5fd,stroke:#1d4ed8,color:#0c0a09
    classDef ator fill:#fde047,stroke:#a16207,color:#1c1917
    classDef agregado fill:#fef3c7,stroke:#b45309,color:#1c1917
    classDef semOrigem stroke-dasharray:5 4,stroke-width:2px
```

### <a id="s-0012"></a>1.10.2 S-0012 - Eventos

Fatos do domínio de avaliação.

#### <a id="evt-0016"></a>1.10.2.1 EVT-0016 - AvaliacaoSolicitada

A avaliação do bem foi pedida após a submissão.

#### <a id="evt-0017"></a>1.10.2.2 EVT-0017 - BemAvaliado

O bem recebeu valor de avaliação e foi aceito como garantia. A Originação recalcula o valor de referência e o enquadramento ([RN-0006](#rn-0006)). Dispara [UC-0006](#uc-0006).

#### <a id="evt-0018"></a>1.10.2.3 EVT-0018 - BemRecusadoComoGarantia

O avaliador não aceitou o bem como garantia. Dispara [UC-0006](#uc-0006), que reprova a proposta.

#### <a id="evt-0019"></a>1.10.2.4 EVT-0019 - AvaliacaoVencida

A avaliação passou da validade antes da contratação ([RN-0022](#rn-0022)). Dispara [UC-0012](#uc-0012).

#### <a id="evt-0020"></a>1.10.2.5 EVT-0020 - MedicaoRealizada

O [ATR-0006](#atr-0006) apurou o percentual executado da obra. Dispara [UC-0023](#uc-0023).

### <a id="doc-0009"></a>1.10.3 DOC-0009 - Regras de Negócio

Regras da avaliação.

#### <a id="rn-0023"></a>1.10.3.1 RN-0023 - Fonte da avaliação por tipo de bem

Varia por [DIM-0001](#dim-0001).

| Tipo | Fonte | Validade ([DEC-0016](#dec-0016)) |
|---|---|---|
| Veículo novo | nota fiscal ou preço de fábrica | 30 dias |
| Veículo usado | tabela de referência | mês da consulta |
| Residencial ou comercial | [laudo](#glo-0017) de avaliador credenciado | 180 dias |
| Terreno | laudo de avaliador credenciado | 180 dias |

#### <a id="rn-0024"></a>1.10.3.2 RN-0024 - Medição da obra

A [medição](#glo-0019) apura o percentual acumulado executado; nunca diminui em relação à anterior. Decisão: [DEC-0006](#dec-0006).

### <a id="s-0013"></a>1.10.4 S-0013 - Casos de uso

Casos de uso da avaliação.

#### <a id="uc-0012"></a>1.10.4.1 UC-0012 - Avaliar bem

**Ator:** [ATR-0007](#atr-0007).

**Fluxo principal**
1. O sistema pede a avaliação na fonte do tipo de bem ([RN-0023](#rn-0023)). Emite [EVT-0016](#evt-0016).
2. O avaliador devolve valor e aceitação. Emite [EVT-0017](#evt-0017) ou [EVT-0018](#evt-0018).

#### <a id="uc-0013"></a>1.10.4.2 UC-0013 - Medir obra

**Ator:** [ATR-0006](#atr-0006).

**Pré-condição:** contrato de imóvel na planta com garantia constituída.

**Fluxo principal:** o engenheiro vistoria a obra e informa o percentual acumulado ([RN-0024](#rn-0024)). Emite [EVT-0020](#evt-0020).

## <a id="dom-0004"></a>1.11 DOM-0004 - Contratação

**Classificação:** Suporte

Emite e acompanha o contrato a partir da proposta aprovada: seguros, assinaturas e, no imóvel na planta, o prazo da obra.

### <a id="vis-0004"></a>1.11.1 VIS-0004 - Visão do Domínio

#### Propósito e fronteiras

Transforma a proposta aprovada em [contrato](#glo-0040): exige os seguros, emite o instrumento, colhe as assinaturas
e entrega o contrato assinado à Garantia.

**Depende de:** [Originação](#dom-0001) e [ATR-0011](#atr-0011).

**Guarda:** o agregado [AGR-0002](#agr-0002) e o seu [CV-0002](#cv-0002). **Atores:** [ATR-0001](#atr-0001), [ATR-0002](#atr-0002), [ATR-0003](#atr-0003), [ATR-0011](#atr-0011).

#### Jornada em eventos

Gerada pelo episteme (event storming): eventos em ordem, quem os provoca e o que disparam.

```mermaid
flowchart LR
    subgraph DOM_0004_es["Contratação"]
        direction LR
        EVT_0021["CondicoesAceitas"]:::evento
        AGR_0002{{"Contrato"}}:::agregado
        UC_0014["Emitir contrato"]:::comando
        ATR_0001(["Proponente"]):::ator
        ATR_0011(["Seguradora"]):::ator
        EVT_0022["SegurosContratados"]:::evento
        EVT_0023["ContratoEmitido"]:::evento
        EVT_0024["ContratoAssinado"]:::evento
        UC_0015["Assinar contrato"]:::comando
        ATR_0002(["Coobrigado"]):::ator
        ATR_0003(["Vendedor"]):::ator
        UC_0018["Registrar garantia"]:::comando
        EVT_0025["ContratoCancelado"]:::evento
        UC_0016["Cancelar contrato"]:::comando
        ATR_0005(["Analista"]):::ator
        EVT_0026["PrazoDaObraProrrogado"]:::evento
        UC_0017["Prorrogar prazo da obra"]:::comando
    end
    AGR_0002 -.- EVT_0021
    UC_0014 --> EVT_0021
    ATR_0001 --- UC_0014
    ATR_0011 --- UC_0014
    EVT_0021 ~~~ EVT_0022
    AGR_0002 -.- EVT_0022
    UC_0014 --> EVT_0022
    EVT_0022 ~~~ EVT_0023
    AGR_0002 -.- EVT_0023
    UC_0014 --> EVT_0023
    EVT_0023 ~~~ EVT_0024
    AGR_0002 -.- EVT_0024
    UC_0015 --> EVT_0024
    ATR_0001 --- UC_0015
    ATR_0002 --- UC_0015
    ATR_0003 --- UC_0015
    EVT_0024 ~~~ EVT_0025
    AGR_0002 -.- EVT_0025
    UC_0016 --> EVT_0025
    ATR_0001 --- UC_0016
    ATR_0005 --- UC_0016
    EVT_0025 ~~~ EVT_0026
    AGR_0002 -.- EVT_0026
    UC_0017 --> EVT_0026
    ATR_0003 --- UC_0017
    ATR_0005 --- UC_0017
    EVT_0024 -.->|sempre que| UC_0018
    classDef evento fill:#fdba74,stroke:#c2410c,color:#1c1917
    classDef comando fill:#93c5fd,stroke:#1d4ed8,color:#0c0a09
    classDef ator fill:#fde047,stroke:#a16207,color:#1c1917
    classDef agregado fill:#fef3c7,stroke:#b45309,color:#1c1917
    classDef semOrigem stroke-dasharray:5 4,stroke-width:2px
```

### <a id="agr-0002"></a>1.11.2 AGR-0002 - Contrato

Fronteira de consistência da formalização e da garantia: condições finais (cópia congelada da proposta), seguros,
assinaturas, garantia, liberações e, no imóvel na planta, o cronograma da obra. Referencia a [proposta](#agr-0001)
pelo identificador, sem alterá-la.

#### Composição

| Elemento | Natureza | Observação |
|---|---|---|
| Condições finais | objeto de valor | imutáveis após a emissão |
| Seguro exigido | entidade | conforme o tipo de bem |
| Assinatura | entidade | uma por signatário exigido |
| Garantia | entidade | órgão, protocolo, situação, exigências |
| Liberação | entidade | única ou por medição |
| Cronograma da obra | objeto de valor | só na planta: prazo previsto e prorrogações |

#### Invariantes

1. Condições imutáveis após a emissão.
2. Só é emitido com os seguros exigidos contratados ([RN-0025](#rn-0025)).
3. Só vai a registro com todas as assinaturas exigidas ([RN-0026](#rn-0026)).
4. Retorno de registro só vale se a identificação registrada for a do contrato ([RN-0029](#rn-0029)).
5. Nenhuma liberação sem garantia constituída ([RN-0030](#rn-0030)); soma liberada ≤ valor financiado ([RN-0031](#rn-0031)).
6. Prorrogações da obra até o limite ([RN-0027](#rn-0027)).

**Emite:** [EVT-0021](#evt-0021), [EVT-0022](#evt-0022), [EVT-0023](#evt-0023), [EVT-0024](#evt-0024), [EVT-0026](#evt-0026), [EVT-0025](#evt-0025).

### <a id="cv-0002"></a>1.11.3 CV-0002 - Ciclo de vida do contrato

Estados do [contrato](#agr-0002). O estado "Em obra" só existe no imóvel na planta.

```mermaid
stateDiagram-v2
    [*] --> AguardandoAssinatura: ContratoEmitido
    AguardandoAssinatura --> Assinado: ContratoAssinado
    AguardandoAssinatura --> Cancelado: ContratoCancelado (desistência)
    Assinado --> EmRegistro: RegistroSolicitado
    EmRegistro --> EmExigencia: ExigenciaEmitida
    EmExigencia --> EmRegistro: ExigenciaCumprida
    EmRegistro --> RegistroRecusado: RegistroRecusado
    RegistroRecusado --> EmRegistro: RegistroSolicitado (reenvio)
    RegistroRecusado --> Cancelado: ContratoCancelado (negócio desfeito)
    EmRegistro --> GarantiaConstituida: GarantiaRegistrada
    GarantiaConstituida --> Formalizado: RecursoLiberado (bem pronto)
    GarantiaConstituida --> EmObra: ParcelaDaObraLiberada (na planta)
    EmObra --> EmObra: MedicaoRealizada / PrazoDaObraProrrogado
    EmObra --> EmObra: ParcelaDaObraLiberada (intermediária)
    EmObra --> Formalizado: ParcelaDaObraLiberada (última)
    Formalizado --> [*]
    Cancelado --> [*]
```

| Transição | Evento | Regra |
|---|---|---|
| Emissão | [EVT-0023](#evt-0023) | [RN-0025](#rn-0025), [RN-0022](#rn-0022) |
| Assinatura | [EVT-0024](#evt-0024) | [RN-0026](#rn-0026) |
| Garantia constituída | [EVT-0028](#evt-0028) | [RN-0029](#rn-0029) |
| Liberação | [EVT-0033](#evt-0033), [EVT-0034](#evt-0034) | [RN-0030](#rn-0030) |
| Prorrogação | [EVT-0026](#evt-0026) | [RN-0027](#rn-0027) |
| Cancelamento | [EVT-0025](#evt-0025) | — |

### <a id="s-0014"></a>1.11.4 S-0014 - Eventos

Fatos do domínio de contratação.

#### <a id="evt-0021"></a>1.11.4.1 EVT-0021 - CondicoesAceitas

O [ATR-0001](#atr-0001) aceitou as condições finais da proposta aprovada.

#### <a id="evt-0022"></a>1.11.4.2 EVT-0022 - SegurosContratados

Os seguros exigidos pelo tipo de bem foram emitidos pela [ATR-0011](#atr-0011) ([RN-0025](#rn-0025)).

#### <a id="evt-0023"></a>1.11.4.3 EVT-0023 - ContratoEmitido

O contrato foi emitido com as condições finais.

#### <a id="evt-0024"></a>1.11.4.4 EVT-0024 - ContratoAssinado

Todas as assinaturas exigidas foram colhidas. Dispara [UC-0018](#uc-0018).

#### <a id="evt-0025"></a>1.11.4.5 EVT-0025 - ContratoCancelado

O contrato foi desfeito antes de formalizado: desistência antes de assinar ou registro recusado sem solução. A garantia, se houver, é baixada.

#### <a id="evt-0026"></a>1.11.4.6 EVT-0026 - PrazoDaObraProrrogado

O prazo previsto da obra foi estendido dentro do limite ([RN-0027](#rn-0027)).

### <a id="doc-0010"></a>1.11.5 DOC-0010 - Regras de Negócio

Regras da contratação.

#### <a id="rn-0025"></a>1.11.5.1 RN-0025 - Seguros exigidos por tipo de bem

O contrato só é emitido com os [seguros exigidos](#glo-0029) contratados. Varia por [DIM-0001](#dim-0001).
Decisão: [DEC-0012](#dec-0012).

| Tipo | Obrigatórios | Opcional |
|---|---|---|
| Residencial ou comercial | [MIP](#glo-0030) e [DFI](#glo-0031) | prestamista |
| Terreno | MIP | prestamista |
| Veículo | seguro do bem | prestamista |

#### <a id="rn-0026"></a>1.11.5.2 RN-0026 - Assinaturas exigidas

Assinam todos os [participantes](#glo-0001). No imóvel e no terreno assinam também o [ATR-0003](#atr-0003) e os cônjuges, quando houver.

#### <a id="rn-0027"></a>1.11.5.3 RN-0027 - Prorrogação do prazo da obra

No imóvel na planta, o contrato aceita [prorrogações](#glo-0045) do prazo da obra que, somadas, não passem de 180 dias além do prazo previsto; acima disso vira pendência para o [ATR-0005](#atr-0005). Decisões: [DEC-0006](#dec-0006), [DEC-0014](#dec-0014).

**Detalhes/Exemplo:**
Obra prevista para 30/06; prorrogações de 90 e de 60 dias somam 150 → aceitas. Um novo pedido de 45 dias somaria 195 → pendência para o analista.

### <a id="rnfd-0002"></a>1.11.6 RNFD-0002 - Contrato disponível em até 2 dias úteis

Depois que o proponente aceita as condições finais, o contrato fica disponível para assinatura em até 2 dias úteis.

### <a id="s-0015"></a>1.11.7 S-0015 - Casos de uso

Casos de uso da contratação.

#### <a id="uc-0014"></a>1.11.7.1 UC-0014 - Emitir contrato

**Atores:** [ATR-0001](#atr-0001), [ATR-0011](#atr-0011).

**Pré-condição:** proposta aprovada, dentro da validade.

**Fluxo principal**
1. O proponente aceita as condições finais. No imóvel na planta, informa a previsão de entrega da obra, do cronograma da [incorporadora](#glo-0005): é o prazo previsto do cronograma da obra. Emite [EVT-0021](#evt-0021).
2. O sistema exige os seguros ([RN-0025](#rn-0025)) e confere a avaliação ([RN-0022](#rn-0022)). Emite [EVT-0022](#evt-0022).
3. O sistema emite o contrato, no prazo de [RNFD-0002](#rnfd-0002). Emite [EVT-0023](#evt-0023).

#### <a id="uc-0015"></a>1.11.7.2 UC-0015 - Assinar contrato

**Atores:** [ATR-0001](#atr-0001), [ATR-0002](#atr-0002), [ATR-0003](#atr-0003).

**Fluxo principal:** cada signatário exigido ([RN-0026](#rn-0026)) assina; com a última assinatura, emite [EVT-0024](#evt-0024).

#### <a id="uc-0016"></a>1.11.7.3 UC-0016 - Cancelar contrato

**Atores:** [ATR-0001](#atr-0001) (desistência antes de assinar) ou [ATR-0005](#atr-0005) (registro recusado sem solução).

**Fluxo principal:** o contrato ainda não formalizado é desfeito e a garantia, se registrada, é baixada. Emite [EVT-0025](#evt-0025).

#### <a id="uc-0017"></a>1.11.7.4 UC-0017 - Prorrogar prazo da obra

**Atores:** [ATR-0003](#atr-0003) (incorporadora), [ATR-0005](#atr-0005).

**Fluxo principal:** a incorporadora pede nova data; o sistema aplica [RN-0027](#rn-0027) e registra. Emite [EVT-0026](#evt-0026).

**Fluxo alternativo:** acima do limite → pendência para o analista decidir.

## <a id="dom-0005"></a>1.12 DOM-0005 - Garantia

**Classificação:** Suporte

Constitui e acompanha a alienação fiduciária no cartório (imóvel e terreno) ou no órgão de trânsito (veículo).

### <a id="vis-0005"></a>1.12.1 VIS-0005 - Visão do Domínio

#### Propósito e fronteiras

Registra a [alienação fiduciária](#glo-0041): averbação na matrícula ou [gravame](#glo-0042) do veículo. Trata
[exigências](#glo-0039) e retornos assíncronos, que podem chegar repetidos ou fora de ordem.

**Depende de:** [Contratação](#dom-0004), [ATR-0009](#atr-0009) e [ATR-0010](#atr-0010). **Atores:** [ATR-0009](#atr-0009), [ATR-0010](#atr-0010), [ATR-0005](#atr-0005).

#### Jornada em eventos

Gerada pelo episteme (event storming): eventos em ordem, quem os provoca e o que disparam.

```mermaid
flowchart LR
    subgraph DOM_0005_es["Garantia"]
        direction LR
        EVT_0027["RegistroSolicitado"]:::evento
        UC_0018["Registrar garantia"]:::comando
        ATR_0009(["Cartório de Registro de Imóveis"]):::ator
        ATR_0010(["Órgão de trânsito"]):::ator
        EVT_0028["GarantiaRegistrada"]:::evento
        UC_0021["Liberar recurso"]:::comando
        EVT_0029["ExigenciaEmitida"]:::evento
        UC_0020["Cumprir exigência"]:::comando
        EVT_0030["ExigenciaCumprida"]:::evento
        ATR_0005(["Analista"]):::ator
        EVT_0031["RegistroRecusado"]:::evento
        UC_0019["Tratar registro recusado"]:::comando
    end
    UC_0018 --> EVT_0027
    ATR_0009 --- UC_0018
    ATR_0010 --- UC_0018
    EVT_0027 ~~~ EVT_0028
    UC_0018 --> EVT_0028
    EVT_0028 ~~~ EVT_0029
    UC_0018 --> EVT_0029
    EVT_0029 ~~~ EVT_0030
    UC_0020 --> EVT_0030
    ATR_0005 --- UC_0020
    EVT_0030 ~~~ EVT_0031
    UC_0018 --> EVT_0031
    EVT_0028 -.->|sempre que| UC_0021
    EVT_0029 -.->|sempre que| UC_0020
    EVT_0030 -.->|sempre que| UC_0018
    EVT_0031 -.->|sempre que| UC_0019
    classDef evento fill:#fdba74,stroke:#c2410c,color:#1c1917
    classDef comando fill:#93c5fd,stroke:#1d4ed8,color:#0c0a09
    classDef ator fill:#fde047,stroke:#a16207,color:#1c1917
    classDef agregado fill:#fef3c7,stroke:#b45309,color:#1c1917
    classDef semOrigem stroke-dasharray:5 4,stroke-width:2px
```

### <a id="s-0016"></a>1.12.2 S-0016 - Eventos

Fatos do domínio de garantia.

#### <a id="evt-0027"></a>1.12.2.1 EVT-0027 - RegistroSolicitado

O contrato assinado foi enviado ao órgão de registro com um protocolo nosso.

#### <a id="evt-0028"></a>1.12.2.2 EVT-0028 - GarantiaRegistrada

O órgão confirmou o registro do bem do contrato ([RN-0029](#rn-0029)). Dispara [UC-0021](#uc-0021).

#### <a id="evt-0029"></a>1.12.2.3 EVT-0029 - ExigenciaEmitida

O [ATR-0009](#atr-0009) emitiu exigência para registrar. Dispara [UC-0020](#uc-0020).

#### <a id="evt-0030"></a>1.12.2.4 EVT-0030 - ExigenciaCumprida

O [ATR-0005](#atr-0005) cumpriu a exigência. Dispara [UC-0018](#uc-0018) (reenvio).

#### <a id="evt-0031"></a>1.12.2.5 EVT-0031 - RegistroRecusado

O órgão recusou o registro. Dispara [UC-0019](#uc-0019).

### <a id="doc-0011"></a>1.12.3 DOC-0011 - Regras de Negócio

Regras da garantia.

#### <a id="rn-0028"></a>1.12.3.1 RN-0028 - Órgão de registro por categoria

Imóvel e terreno: [ATR-0009](#atr-0009) da matrícula ([F-0001](#f-0001)). Veículo: [ATR-0010](#atr-0010), pelo sistema de gravames ([F-0002](#f-0002), [F-0003](#f-0003)).

#### <a id="rn-0029"></a>1.12.3.2 RN-0029 - Retorno de registro é fato, não pedido

O retorno do órgão é classificado, não recusado: se a identificação registrada é a do bem do contrato, a garantia está constituída; se é de outro bem, vira pendência para o [ATR-0005](#atr-0005). Retorno repetido do mesmo protocolo não tem novo efeito.

### <a id="s-0017"></a>1.12.4 S-0017 - Casos de uso

Casos de uso da garantia.

#### <a id="uc-0018"></a>1.12.4.1 UC-0018 - Registrar garantia

**Atores:** [ATR-0009](#atr-0009) ou [ATR-0010](#atr-0010). Faz o [registro da garantia](#glo-0043).

**Fluxo principal**
1. O sistema envia o contrato ao órgão da categoria ([RN-0028](#rn-0028)). Emite [EVT-0027](#evt-0027).
2. O órgão devolve o resultado; o sistema classifica ([RN-0029](#rn-0029)). Emite [EVT-0028](#evt-0028).

**Fluxos alternativos:** exigência → emite [EVT-0029](#evt-0029); recusa → emite [EVT-0031](#evt-0031).

#### <a id="uc-0019"></a>1.12.4.2 UC-0019 - Tratar registro recusado

**Ator:** [ATR-0005](#atr-0005).

**Fluxo principal:** o analista corrige o motivo da recusa e reenvia; o sistema volta a [UC-0018](#uc-0018).

**Fluxo alternativo:** sem solução → [UC-0016](#uc-0016).

#### <a id="uc-0020"></a>1.12.4.3 UC-0020 - Cumprir exigência

**Ator:** [ATR-0005](#atr-0005).

**Fluxo principal:** o analista providencia o que o cartório pediu e reenvia. Emite [EVT-0030](#evt-0030).

## <a id="dom-0006"></a>1.13 DOM-0006 - Desembolso

**Classificação:** Suporte

Libera o dinheiro ao vendedor — de uma vez, ou por medição no imóvel na planta — e o FGTS da entrada.

### <a id="vis-0006"></a>1.13.1 VIS-0006 - Visão do Domínio

#### Propósito e fronteiras

Paga o [ATR-0003](#atr-0003) quando a garantia está constituída: [liberação](#glo-0044) única no bem pronto, ou por
[medição](#glo-0019) no imóvel na planta. Pede ao [ATR-0012](#atr-0012) a liberação do FGTS da entrada.

**Depende de:** [Garantia](#dom-0005), [Avaliação](#dom-0003) (medições) e [ATR-0012](#atr-0012).

#### Jornada em eventos

Gerada pelo episteme (event storming): eventos em ordem, quem os provoca e o que disparam.

```mermaid
flowchart LR
    subgraph DOM_0006_es["Desembolso"]
        direction LR
        EVT_0032["LiberacaoSolicitada"]:::evento
        UC_0021["Liberar recurso"]:::comando
        ATR_0003(["Vendedor"]):::ator
        ATR_0012(["Sistema do FGTS"]):::ator
        EVT_0033["RecursoLiberado"]:::evento
        EVT_0034["ParcelaDaObraLiberada"]:::evento
        UC_0023["Liberar parcela da obra"]:::comando
        EVT_0035["FgtsSolicitado"]:::evento
        EVT_0036["FgtsLiberado"]:::evento
        EVT_0037["FgtsNegado"]:::evento
        UC_0022["Recompor entrada"]:::comando
    end
    UC_0021 --> EVT_0032
    ATR_0003 --- UC_0021
    ATR_0012 --- UC_0021
    EVT_0032 ~~~ EVT_0033
    UC_0021 --> EVT_0033
    EVT_0033 ~~~ EVT_0034
    UC_0023 --> EVT_0034
    ATR_0003 --- UC_0023
    EVT_0034 ~~~ EVT_0035
    UC_0021 --> EVT_0035
    EVT_0035 ~~~ EVT_0036
    UC_0021 --> EVT_0036
    EVT_0036 ~~~ EVT_0037
    UC_0021 --> EVT_0037
    EVT_0037 -.->|sempre que| UC_0022
    classDef evento fill:#fdba74,stroke:#c2410c,color:#1c1917
    classDef comando fill:#93c5fd,stroke:#1d4ed8,color:#0c0a09
    classDef ator fill:#fde047,stroke:#a16207,color:#1c1917
    classDef agregado fill:#fef3c7,stroke:#b45309,color:#1c1917
    classDef semOrigem stroke-dasharray:5 4,stroke-width:2px
```

### <a id="s-0018"></a>1.13.2 S-0018 - Eventos

Fatos do domínio de desembolso.

#### <a id="evt-0032"></a>1.13.2.1 EVT-0032 - LiberacaoSolicitada

A liberação do valor financiado (ou de uma parcela da obra) foi pedida.

#### <a id="evt-0033"></a>1.13.2.2 EVT-0033 - RecursoLiberado

O vendedor recebeu o valor financiado. O contrato está formalizado.

#### <a id="evt-0034"></a>1.13.2.3 EVT-0034 - ParcelaDaObraLiberada

A incorporadora recebeu a parcela correspondente à última medição.

#### <a id="evt-0035"></a>1.13.2.4 EVT-0035 - FgtsSolicitado

O uso do FGTS na entrada foi pedido ao [ATR-0012](#atr-0012).

#### <a id="evt-0036"></a>1.13.2.5 EVT-0036 - FgtsLiberado

O FGTS da entrada foi liberado ao vendedor.

#### <a id="evt-0037"></a>1.13.2.6 EVT-0037 - FgtsNegado

O sistema do FGTS negou o uso. Dispara [UC-0022](#uc-0022).

### <a id="doc-0012"></a>1.13.3 DOC-0012 - Regras de Negócio

Regras do desembolso.

#### <a id="rn-0030"></a>1.13.3.1 RN-0030 - Nenhuma liberação sem garantia confirmada

Nenhum valor é liberado antes de a garantia estar registrada e confirmada pelo órgão, em todos os tipos de bem. Decisão: [DEC-0008](#dec-0008).

#### <a id="rn-0031"></a>1.13.3.2 RN-0031 - Soma das liberações

A soma das liberações nunca passa do valor financiado. Cada liberação é paga uma única vez, ao vendedor da proposta.

#### <a id="rn-0032"></a>1.13.3.3 RN-0032 - Liberação por medição

No imóvel na planta, cada parcela = valor financiado × (percentual medido acumulado − percentual já liberado) ([RN-0024](#rn-0024)). Decisão: [DEC-0006](#dec-0006).

### <a id="s-0019"></a>1.13.4 S-0019 - Casos de uso

Casos de uso do desembolso.

#### <a id="uc-0021"></a>1.13.4.1 UC-0021 - Liberar recurso

**Atores:** [ATR-0003](#atr-0003) (favorecido) e [ATR-0012](#atr-0012) (quando a entrada usa FGTS).

**Pré-condição:** garantia constituída ([RN-0030](#rn-0030)).

**Fluxo principal**
1. O sistema pede a liberação. Emite [EVT-0032](#evt-0032).
2. Se a entrada usa FGTS, o sistema pede ao sistema do FGTS ([RN-0015](#rn-0015)). Emite [EVT-0035](#evt-0035) e, com a liberação confirmada, [EVT-0036](#evt-0036).
3. O sistema paga o vendedor ([RN-0031](#rn-0031)). Emite [EVT-0033](#evt-0033).

**Fluxo alternativo:** FGTS negado → emite [EVT-0037](#evt-0037) e a liberação espera a entrada recomposta.

#### <a id="uc-0022"></a>1.13.4.2 UC-0022 - Recompor entrada

**Ator:** [ATR-0001](#atr-0001).

**Fluxo principal:** com o FGTS negado, o proponente paga com recurso próprio a parte da entrada que viria do FGTS ([RN-0005](#rn-0005)); a liberação segue em [UC-0021](#uc-0021).

#### <a id="uc-0023"></a>1.13.4.3 UC-0023 - Liberar parcela da obra

**Ator:** [ATR-0003](#atr-0003) (incorporadora).

**Pré-condição:** imóvel na planta com garantia constituída e nova medição.

**Fluxo principal:** o sistema calcula a parcela ([RN-0032](#rn-0032), [RN-0031](#rn-0031)) e paga a incorporadora. Emite [EVT-0034](#evt-0034).

## <a id="dom-0007"></a>1.14 DOM-0007 - Cadastro de pessoas

**Classificação:** Genérico

CPF, CNPJ, endereço e renda dos participantes e do vendedor. Pode vir de um cadastro corporativo.

### <a id="vis-0007"></a>1.14.1 VIS-0007 - Visão do Domínio

#### Propósito e fronteiras

Mantém os dados cadastrais de [pessoas físicas](#glo-0002) e [jurídicas](#glo-0003) usados pela Originação. Não decide nada
sobre a proposta.

### <a id="doc-0013"></a>1.14.2 DOC-0013 - Regras de Negócio

Regras do cadastro de pessoas.

#### <a id="rn-0033"></a>1.14.2.1 RN-0033 - CPF ou CNPJ válido

Todo participante e todo vendedor tem CPF ([pessoa física](#glo-0002)) ou CNPJ ([pessoa jurídica](#glo-0003)) válido e regular na Receita Federal. Os dados são tratados só para o financiamento ([F-0005](#f-0005)).

## <a id="dom-0008"></a>1.15 DOM-0008 - Documentos

**Classificação:** Genérico

Recebimento, guarda e conferência dos arquivos exigidos.

### <a id="vis-0008"></a>1.15.1 VIS-0008 - Visão do Domínio

#### Propósito e fronteiras

Recebe e guarda os documentos dos participantes e do bem e registra a conferência (aceito, recusado, ilegível).
Quais documentos são exigidos é regra da Originação, não deste domínio.

#### Jornada em eventos

```mermaid
flowchart LR
    subgraph DOM_0008_es["Documentos"]
        direction LR
        EVT_0038["DocumentoRecusado"]:::evento
        UC_0024["Conferir documento"]:::comando
        ATR_0005(["Analista"]):::ator
        EVT_0003["PropostaComPendencia"]:::evento
        EVT_0039["DocumentoAceito"]:::evento
    end
    UC_0024 --> EVT_0038
    ATR_0005 --- UC_0024
    EVT_0038 ~~~ EVT_0039
    UC_0024 --> EVT_0039
    EVT_0038 -.->|sempre que| EVT_0003
    classDef evento fill:#fdba74,stroke:#c2410c,color:#1c1917
    classDef comando fill:#93c5fd,stroke:#1d4ed8,color:#0c0a09
    classDef ator fill:#fde047,stroke:#a16207,color:#1c1917
    classDef agregado fill:#fef3c7,stroke:#b45309,color:#1c1917
    classDef semOrigem stroke-dasharray:5 4,stroke-width:2px
```

### <a id="s-0020"></a>1.15.2 S-0020 - Eventos

Fatos do domínio de documentos.

#### <a id="evt-0038"></a>1.15.2.1 EVT-0038 - DocumentoRecusado

Um documento foi recusado na conferência (ilegível, vencido ou divergente). Dispara [EVT-0003](#evt-0003) na proposta.

#### <a id="evt-0039"></a>1.15.2.2 EVT-0039 - DocumentoAceito

Um documento foi conferido e aceito.

### <a id="s-0021"></a>1.15.3 S-0021 - Casos de uso

Casos de uso de documentos.

#### <a id="uc-0024"></a>1.15.3.1 UC-0024 - Conferir documento

**Ator:** [ATR-0005](#atr-0005).

**Fluxo principal:** o analista confere o documento enviado contra o que é exigido ([RN-0009](#rn-0009)) e o aceita. Emite [EVT-0039](#evt-0039).

**Fluxo alternativo:** ilegível, vencido ou divergente → recusa. Emite [EVT-0038](#evt-0038).
