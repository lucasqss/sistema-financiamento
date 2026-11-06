# 0011. Fluxo comum de validação e catálogo de erros

**Status:** Aceita · **Data:** 2026-11-06

## Contexto
Cada requisição tratava erros do seu jeito. Um campo nulo estourava mais adiante, e as regras lançavam uma exceção
genérica com texto livre. A empresa tem um **catálogo corporativo de erros**, e o sistema precisa mapear cada erro
para ele. Queremos que **toda** requisição siga o mesmo caminho, com as validações variando conforme o request e os
objetos de domínio que ela usa.

## Decisão
**Catálogo de erros e uma exceção por tipo**
- Todo erro de negócio é uma entrada de catálogo (`ErroCatalogado`):
  - código estável (`ORI-0001`, `ATR-0003`), que não se reaproveita nem se renumera;
  - tipo (ATRIBUTO ou DOMINIO), status HTTP e mensagem com parâmetros;
  - a regra do modelo de negócio, quando há (RN-, CV-, UC-, AGR-).
- Os erros de forma, comuns a todos, ficam em `shared/validacao/atributo/ErroDeAtributo`. Os de domínio ficam no domínio de cada módulo (`ErrosDaOriginacao`, `ErrosDaContratacao`…).
- Duas exceções levam a lista de falhas, cada falha com o erro, o campo e os parâmetros:
  - `ErroDeAtributoException`: 400;
  - `ErroDeDominioException`: 422, ou 404 quando o que se pede não existe.
- **Mapeamento corporativo** num lugar só, `catalogo-corporativo.properties` (nosso código → código corporativo). A resposta traz os dois:
  `{tipo, falhas: [{codigo, codigoCorporativo, campo, regra, mensagem}]}`.

**Duas cadeias de validação (Chain of Responsibility)**
- Um `Elo` recebe o contexto e a continuação, e decide se segue.
- **Atributo:** cada request tem um `<Request>Validador`, ao lado dele em `entrypoint`, que monta uma `CadeiaDeAtributos` com os elos reutilizáveis (`ElosDeAtributo`: obrigatório, tamanho, formato, CPF/CNPJ, faixas, itens de lista).
  - O elo de atributo registra a falha e **sempre segue**: a requisição volta com todos os campos errados de uma vez.
  - O resource chama o validador antes de converter o request em comando.
  - JSON malformado e valor que não cabe no campo viram falha de atributo do mesmo formato.
- **Domínio:** cada caso de uso monta a sua cadeia na aplicação, com os elos do módulo (`ElosDaProposta`, `Contratacoes.aceita(...)`…) sobre um contexto (`ContextoDeDominio`).
  - O primeiro elo carrega a entidade (404 se não existe); os seguintes conferem estado, transição do ciclo de vida e entidades vinculadas.
  - O elo de domínio **para** a cadeia no primeiro erro, porque um depende do anterior.
  - O caso de uso usa a entidade que a cadeia carregou, sem recarregar.
- **Os agregados mantêm as invariantes como rede de segurança**, com os mesmos códigos do catálogo. Elas protegem os caminhos que não passam por requisição: os ouvintes entre módulos (ADR 0009) e o agendamento de prazos.

## Consequências
- Mapear um erro para o catálogo corporativo é uma linha num arquivo.
- Fitness functions (`ValidacaoTest`), que quebram a build:
  - todo `*Request` tem `*RequestValidador`, e o resource que o recebe chama `validar`;
  - todo caso de uso chamado por uma entrada (API ou retorno), exceto as consultas `Consultar*`, roda uma cadeia de domínio;
  - nenhuma exceção própria fora de `shared.validacao`;
  - catálogos no domínio do módulo, com códigos únicos;
  - cadeia de atributo só na entrada, cadeia de domínio só na aplicação.
- Uma regra de estado pode aparecer em dois lugares, no elo da cadeia e na invariante do agregado. As duas usam o mesmo código do catálogo, e a do agregado é a rede de segurança.
- O CPF com dígito errado passou a ser erro de atributo (400). "CPF regular na Receita", quando existir, será de domínio.
