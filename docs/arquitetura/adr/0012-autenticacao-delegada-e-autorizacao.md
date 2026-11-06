# 0012. Autenticação delegada (JWT) e autorização por papel e dono

**Status:** Aceita · **Data:** 2026-11-06

## Contexto
A API estava aberta: qualquer chamada fazia qualquer coisa, inclusive baixar documentos pessoais dos participantes
(LGPD, F-0005). A empresa já tem um emissor de identidade, e o sistema não deve guardar senhas nem cuidar do login.
Os atores do modelo (proponente, coobrigado, vendedor, correspondente, analista, engenheiro e os sistemas externos)
fazem coisas diferentes, e um cliente não pode ver a proposta de outro.

## Decisão
**Autenticação delegada**
- O emissor externo (Keycloak, Entra ID, Okta ou o IdP corporativo) emite um **JWT**. A aplicação só o valida, com `quarkus-smallrye-jwt`:
  - assinatura pelas chaves públicas do emissor (JWKS, `JWT_JWKS_URL`);
  - emissor (`JWT_ISSUER`) e validade.
- **Claims no padrão MP-JWT, configuráveis:**
  - os papéis vêm em `groups` (`JWT_CLAIM_PAPEIS`);
  - o CPF/CNPJ do usuário vem em `cpf` (`JWT_CLAIM_DOCUMENTO`).
- **Sistemas externos** (bureau, avaliador, seguradora, cartório e órgão de trânsito, FGTS, pagamento) usam *client credentials* no mesmo emissor. Cada um tem um papel próprio (`sistema-bureau`…), e cada `/retornos/...` aceita só o seu.

**Autorização por papel**
- Os papéis são os atores do modelo (`shared/seguranca/Papeis`).
- Cada endpoint declara os seus com `@RolesAllowed`, pelos atores de cada caso de uso.
- **Nega por padrão:** endpoint sem anotação não responde (`deny-unannotated-endpoints`).

**Autorização por dono**
- Proponente, coobrigado, vendedor, correspondente e cônjuge só agem no que participam: quem iniciou a proposta, os participantes, o vendedor e os signatários do contrato.
- Isso é conferido como **elo da cadeia de domínio** (ADR 0011), logo depois de carregar a proposta, o contrato ou a contratação.
- Analista e engenheiro trabalham sobre todos os casos. Os sistemas respondem pelo protocolo que receberam.
- Fora de requisição (agendamento de prazos) não há usuário, e a conferência não se aplica.
- **Cada signatário assina por si:** o documento informado na assinatura é o do token.

**Erros no catálogo** (ADR 0011)
| Código | Status | Situação |
|---|---|---|
| `SEG-0001` | 401 | não autenticado (sem token, token inválido, expirado ou de outro emissor) |
| `SEG-0002` | 403 | o papel não permite |
| `SEG-0003` | 403 | não participa |
| `SEG-0004` | 403 | assinatura de outro |

A autenticação roda no fluxo do JAX-RS (`proactive=false`), para os erros saírem no formato único.

## Consequências
- O sistema não guarda senha nem sessão. Trocar de emissor é configuração.
- Fitness function (`SegurancaArquiteturaTest`): todo endpoint declara `@RolesAllowed`, e nenhum usa `@PermitAll`.
- Nos testes, os tokens são assinados com uma chave de teste (`src/test/resources`):
  - os testes de jornada falam como um operador com todos os papéis;
  - `SegurancaWebTest` testa o acesso de cada ator.
- Os papéis vêm do emissor. Atribuí-los a cada pessoa (quem é analista, quem é correspondente) é configuração do emissor, fora deste sistema.
- O proponente acompanha os seus documentos pelo detalhe da proposta. A lista, o arquivo e a conferência são do analista.
