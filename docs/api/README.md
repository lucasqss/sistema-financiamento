# Guia de integração da API

O sistema de financiamento é **só API**: as telas (portal do proponente, fila do analista) e os sistemas externos
integram por aqui. Três arquivos bastam para integrar:

| Arquivo | O que é |
|---|---|
| [`openapi.yaml`](openapi.yaml) (e [`openapi.json`](openapi.json)) | O contrato: endpoints, corpos, respostas e papéis de cada operação. Gerado do código a cada build; toda mudança de contrato aparece no diff. Com a aplicação no ar, também em `/q/openapi` (e Swagger UI em `/q/swagger-ui`, só em dev). |
| [`catalogo-de-erros.md`](catalogo-de-erros.md) | Todos os códigos de erro, com status, regra do modelo e mensagem. Gerado do código e conferido por teste. |
| Este guia | Como autenticar, como tratar erros e a jornada na ordem das chamadas. |

As regras de negócio por trás de cada operação estão no modelo, em [`../dominio/especificacao.md`](../dominio/especificacao.md).
Cada operação do contrato cita o caso de uso (UC-…) que ela realiza.

## Autenticação

A autenticação é de um **emissor externo** ([ADR 0012](../arquitetura/adr/0012-autenticacao-delegada-e-autorizacao.md)).
Envie o JWT dele em toda chamada:

```http
Authorization: Bearer <token>
```

| Claim | Conteúdo |
|---|---|
| `iss` | O emissor configurado em `JWT_ISSUER` |
| `groups` | Os papéis (configurável em `JWT_CLAIM_PAPEIS`) |
| `cpf` | O CPF/CNPJ de quem age (configurável em `JWT_CLAIM_DOCUMENTO`) |

**Papéis de pessoas** (os atores do modelo):
- `proponente` e `coobrigado`;
- `vendedor` (inclui a incorporadora);
- `correspondente`;
- `analista`;
- `engenheiro` (medição da obra);
- `conjuge` (signatário no imóvel).

**Papéis de sistema** (*client credentials*, um por retorno):
`sistema-bureau`, `sistema-avaliador`, `sistema-seguradora`, `sistema-registro`, `sistema-fgts` e `sistema-pagamento`.

O contrato lista, em cada operação, os papéis aceitos (`security: jwt: [...]`). Além do papel:
- **proponente, coobrigado, vendedor, correspondente e cônjuge só agem no que participam**: quem iniciou a proposta, os participantes, o vendedor e os signatários do contrato;
- **cada signatário assina por si**: o `documento` da assinatura é o CPF do token;
- **analista e engenheiro** trabalham sobre todos os casos.

## Erros

Toda resposta de erro tem o mesmo formato ([ADR 0011](../arquitetura/adr/0011-fluxo-comum-de-validacao.md)):

```json
{
  "tipo": "ATRIBUTO",
  "falhas": [
    { "codigo": "ATR-0001", "codigoCorporativo": null, "campo": "participantes[0].nome", "regra": null, "mensagem": "Campo obrigatório." }
  ]
}
```

| Status | Quando | O que a tela faz |
|---|---|---|
| 400 | Campo fora de forma ou formato (`ATR-`). Vêm **todos** os campos errados de uma vez. | Marca cada `falhas[].campo` no formulário. |
| 401 | Sem token, inválido, expirado ou de outro emissor (`SEG-0001`) | Renova o login no emissor. |
| 403 | O papel não permite (`SEG-0002`), não participa (`SEG-0003`) ou assinatura de outro (`SEG-0004`) | Esconde a ação ou avisa. |
| 404 | O que se pede não existe | Avisa. |
| 422 | A regra de negócio não deixa: o `codigo` diz qual, e a `regra` aponta a regra do modelo (RN-, CV-, UC-, AGR-) | Mostra a `mensagem`, que está em linguagem de negócio. |

Use o `codigo` (ou o `codigoCorporativo`) para decidir o comportamento, nunca o texto da mensagem.

## Jornada, na ordem das chamadas

Os endpoints de pessoas ficam em `/web/...`; os retornos de sistemas externos, em `/retornos/...`.

**1. Montar e submeter a proposta** (proponente ou correspondente)
1. `POST /web/propostas` com `{"tipoDeBem": "MOTO_USADA"}`. Os tipos estão na dimensão "Tipo de bem" do modelo: `CARRO_NOVO`, `CARRO_USADO`, `MOTO_NOVA`, `MOTO_USADA`, `RESIDENCIAL_PRONTO`, `RESIDENCIAL_NA_PLANTA`, `COMERCIAL_PRONTO`, `COMERCIAL_NA_PLANTA`, `TERRENO_URBANO`, `TERRENO_RURAL`.
2. `PUT /web/propostas/{id}/participantes`, `PUT .../bem` e `PUT .../condicoes`. Cada resposta traz `pendenciasDeEnquadramento`: o que falta ou viola as regras, para a tela mostrar antes de submeter.
3. `POST /web/documentos`, em multipart: `propostaId`, `titular` ("bem" ou o CPF/CNPJ do participante), `tipo` e `arquivo` (PDF, JPEG ou PNG, até 10 MB). Os documentos exigidos aparecem nas pendências (RN-0009).
4. `POST /web/propostas/{id}/submissao`. Se ainda não enquadra, volta 422 com a lista do que falta.

**2. Análise** (analista e sistemas)
1. A submissão pede a análise de crédito e a avaliação do bem. Os resultados chegam em `POST /retornos/bureau-de-credito/{analiseId}` e `POST /retornos/avaliador/{avaliacaoId}`.
2. O analista confere cada documento: `GET /web/documentos?situacao=ENVIADO` e `POST /web/documentos/{id}/conferencia`.
3. A proposta é aprovada quando há crédito aprovado, avaliação válida e todos os documentos aceitos.
4. Pelo caminho podem acontecer:
   - **documento recusado:** a proposta fica com pendência; o proponente reenvia o documento e resolve em `POST /web/propostas/{id}/pendencia/resolucao`;
   - **contraproposta:** o proponente responde em `POST .../contraproposta/aceite` ou `.../recusa`.

**3. Contratar e liberar**
1. `POST /web/contratacoes` (proponente) aceita as condições finais. No imóvel na planta, informe também `previsaoDeEntregaDaObra`.
2. A seguradora devolve as apólices em `POST /retornos/seguradora/{contratacaoId}`, e o contrato é emitido.
3. Cada signatário assina em `POST /web/contratos/{id}/assinaturas`, com o próprio token.
4. Assinado, o registro da garantia começa sozinho. O cartório ou o órgão de trânsito responde em `POST /retornos/orgao-de-registro`.
5. Com a garantia constituída, a liberação é pedida. O pagamento é confirmado em `POST /retornos/pagamento`.
   - **Com FGTS:** antes, `POST /retornos/fgts/{desembolsoId}`.
   - **Na planta:** cada `POST /web/medicoes` (engenheiro) gera uma parcela.

Acompanhe em `GET /web/propostas/{id}`, `GET /web/contratacoes/{id}` e `GET /web/contratos/{id}`.

## Regras de integração

- **Os sistemas externos recebem os pedidos por HTTP e devolvem em `/retornos/...`** ([ADR 0013](../arquitetura/adr/0013-integracao-http-com-externos-simulados.md)). Cada pedido leva a `urlDeRetorno`. O formato de cada um e os simuladores estão em [externos simulados](externos-simulados.md).
- **Retornos de sistemas externos são idempotentes:** o mesmo retorno repetido não tem novo efeito. Um retorno do órgão de registro nunca é recusado; é classificado (RN-0029).
- **O reenvio de um documento ainda não aceito substitui o anterior.** O substituído continua consultável, com o motivo da recusa.
- **Ids são números; protocolos são textos nossos** (`GAR-<contrato>-<n>`, `LIB-<contrato>-<n>`), que os sistemas externos devolvem nos retornos.
- **As consultas de lista exigem o filtro:** `GET /web/avaliacoes` e `GET /web/analises-de-credito` pedem `proposta`; `GET /web/medicoes`, `GET /web/registros` e `GET /web/desembolsos` pedem `contrato`. Sem ele, a resposta é 400 (`ATR-0001`).
- **Quem não participa recebe 403 antes de qualquer regra de negócio:** a resposta não revela nada sobre a proposta ou o contrato alheio.
- **Campos desconhecidos na resposta devem ser ignorados.** Campos novos podem entrar sem aviso; remoção ou mudança de significado só com nova versão.
