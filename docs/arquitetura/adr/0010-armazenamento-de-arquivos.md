# 0010. Armazenamento de arquivos dos documentos

**Status:** Aceita · **Data:** 2026-11-06

## Contexto
A análise documental do MVP (DOM-0008, UC-0024) precisa receber, guardar e mostrar ao analista os arquivos dos
documentos: identidade, comprovantes, matrícula, CRLV. São arquivos pessoais (LGPD, F-0005), que precisam continuar
íntegros depois de conferidos. Nenhum ADR anterior trata de onde eles moram. O banco (ADR 0004) guarda os metadados,
mas não é o lugar de arquivos de até alguns megabytes.

## Decisão
- O módulo `documentos` define uma **porta `ArmazemDeArquivos`** (guardar, ler). O domínio só conhece a chave do arquivo.
- Adaptadores:
  - **sistema de arquivos**, com diretório configurável (`documentos.armazem.diretorio`): para dev, teste e um MVP de uma instância;
  - em produção com mais de uma instância, **armazenamento de objetos compatível com S3**, atrás da mesma porta e sem mudar o domínio.
- Regras do arquivo, conferidas antes de guardar:
  - só PDF, JPEG e PNG, conferidos pelo começo do conteúdo e não só pelo tipo declarado;
  - até 10 MB;
  - SHA-256 guardado com o metadado e conferido a cada leitura.
- O arquivo só sai pelo sistema (`/web/documentos/{id}/arquivo`), nunca por endereço público do armazém.

## Consequências
- Trocar o sistema de arquivos pelo armazenamento de objetos é um adaptador novo e uma configuração.
- O arquivo é gravado antes do commit da transação. Se a transação for desfeita (por exemplo, a proposta não aceita o
  envio), fica um arquivo órfão no armazém, sem metadado que aponte para ele. É inofensivo e pode ser limpo por rotina.
- O controle de acesso por perfil (proponente só vê os seus, analista vê a fila) vem com a autenticação.
