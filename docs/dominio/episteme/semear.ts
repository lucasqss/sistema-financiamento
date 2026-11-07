/**
 * Monta a base do episteme com o modelo de domínio do sistema de financiamento.
 *
 * O conteúdo do modelo mora aqui, no repositório do negócio; o episteme entra só
 * como ferramenta. Roda de dentro de um checkout do episteme (é de lá que vêm as
 * migrações, os serviços e as dependências):
 *
 *   cd ../episteme && npx tsx ../sistema-financiamento/docs/dominio/episteme/semear.ts
 *
 * Gera, ao lado deste arquivo, `financiamento.db` (abre no episteme) e, na pasta
 * acima, `especificacao.md` (o documento compilado, com diagramas Mermaid).
 *
 * Os textos citam outros artefatos por CHAVE (`[[@rn.prazo]]`), não por código:
 * os códigos (RN-0001...) só existem depois de criados, então a carga cria tudo
 * primeiro e escreve os textos numa segunda passada.
 */
import fs from 'fs';
import path from 'path';
import { createRequire } from 'module';
import { pathToFileURL, fileURLToPath } from 'url';

const AQUI = path.dirname(fileURLToPath(import.meta.url));
const EPISTEME = process.cwd();

type Ref = { id: number; codigo: string };
type Tipo =
  | 'PASTA' | 'DOMINIO' | 'VISAO' | 'CASO_DE_USO' | 'REGRA' | 'RNF_DOMINIO' | 'DOCUMENTO'
  | 'RNF_SISTEMA' | 'TERMO' | 'FONTE' | 'SECAO'
  | 'ATOR' | 'EVENTO' | 'AGREGADO' | 'CICLO_DE_VIDA' | 'DECISAO' | 'QUESTAO' | 'DIMENSAO';
type Situacao = 'RASCUNHO' | 'EM_REVISAO' | 'APROVADO';

interface Item {
  chave: string;
  pai: string | null;
  tipo: Tipo;
  titulo: string;
  texto: string;
  exemplo?: string;
  situacao?: Situacao;
  classificacao?: 'Principal' | 'Suporte' | 'Genérico';
}

// ---------------------------------------------------------------------------
// Conteúdo
// ---------------------------------------------------------------------------

const itens: Item[] = [];
const add = (i: Item) => { itens.push(i); return i.chave; };

/**
 * Situação de todo artefato do modelo. As definições negociais desta fase foram
 * aprovadas em 2026-11-05 (snapshot v2). Uma mudança futura num artefato aprovado
 * o rebaixa para "Em revisão" no próprio episteme.
 */
const SITUACAO: Situacao = 'APROVADO';

/**
 * Situação dos itens que estão sendo criados agora. A etapa da vida do contrato (2026-11-07) entra "Em revisão"
 * até o negócio validar; os itens aprovados que ela alterou também voltam a "Em revisão".
 */
let situacaoAtual: Situacao = SITUACAO;
const EM_REVISAO = { situacao: 'EM_REVISAO' as Situacao };

/** Atalho: item com a situação do modelo. */
const it = (chave: string, pai: string | null, tipo: Tipo, titulo: string, texto: string, extra: Partial<Item> = {}) =>
  add({ chave, pai, tipo, titulo, texto, situacao: situacaoAtual, ...extra });

// ===== Raiz ================================================================

it('raiz', null, 'PASTA', 'Sistema de Financiamento de Bens', `
Modelo de domínio de um **sistema-modelo de financiamento de bens**, reaproveitável em vários projetos.
Cobre a jornada da **proposta** até o **contrato formalizado**, com a garantia registrada e o dinheiro
liberado ao vendedor, e a vida do contrato até a **quitação**, com a garantia baixada, passando pela **renegociação**. Esta etapa fica no **espaço do problema**: não há arquitetura, tecnologia nem banco.

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

\`\`\`mermaid
flowchart TD
    BEM[Bem] --> VEI[Veículo]
    BEM --> IMO[Imóvel]
    VEI --> CAR[Carro] --> CN[novo] & CU[usado]
    VEI --> MOT[Moto] --> MN[nova] & MU[usada]
    IMO --> RES[Residencial] --> RP[pronto] & RPL[na planta]
    IMO --> COM[Comercial] --> CP[pronto] & CPL[na planta]
    IMO --> TER[Terreno] --> TU[urbano] & TR[rural]
\`\`\`

## Mapa de contexto

Gerado pelo episteme a partir das dependências que cada visão de domínio declara (\`DEPENDE_DE\`): domínios do
sistema com a classificação (principal em destaque, genéricos tracejados) e, tracejados, os sistemas externos.

\`\`\`mapa-de-contexto
\`\`\`

Toda dependência de sistema externo passa por uma camada anticorrupção: códigos e formatos externos não entram no modelo. Os retornos
externos são **assíncronos** e podem chegar repetidos ou fora de ordem.

## Fora de escopo nesta etapa

- Da vida do contrato depois de formalizado: cobrança das parcelas, atraso e retomada da garantia. A quitação e a
  renegociação estão no modelo (domínio Carteira de contratos).
- Cálculo financeiro (taxa, CET, PRICE/SAC, saldo devedor, desconto da quitação antecipada) e cálculo de prêmio de
  seguro: a parcela é um dado da simulação, e o que depende de cálculo vem do sistema financeiro.
- Subsídio habitacional, consórcio, leasing e refinanciamento.

> Os números das regras são **valores de referência do modelo**, parametrizáveis: cada projeto que usar este modelo ajusta os seus.
`, EM_REVISAO);

// ===== Glossário ===========================================================

it('glo', 'raiz', 'DOCUMENTO', 'Glossário', `
Linguagem ubíqua: cada termo tem um só nome e um só significado em conversas, documentos e código.

## Termos a evitar

| Evite | Use | Por quê |
|---|---|---|
| pedido, solicitação, operação | proposta (antes do contrato) ou contrato (depois) | são fases diferentes, com regras diferentes |
| cliente | proponente, coobrigado, participante | "cliente" não diz o papel na dívida |
| aprovado, sem dizer o quê | crédito aprovado, garantia registrada, contrato formalizado | há várias aprovações independentes |
| valor do bem | valor de compra, valor de avaliação ou valor de referência | os três diferem e a regra usa o menor |
`);
it('glo.pessoas', 'glo', 'SECAO', 'Pessoas', 'Quem participa da proposta e do contrato.');
it('glo.bem', 'glo', 'SECAO', 'Bem', 'O objeto financiado e como ele é classificado e avaliado.');
it('glo.condicoes', 'glo', 'SECAO', 'Condições', 'Valores e prazos do financiamento.');
it('glo.jornada', 'glo', 'SECAO', 'Jornada', 'Etapas e instrumentos da proposta ao contrato.');

const termo = (chave: string, secao: string, titulo: string, texto: string) => it(`t.${chave}`, `glo.${secao}`, 'TERMO', titulo, texto);

termo('participante', 'pessoas', 'Participante', 'Pessoa que integra a proposta: o proponente ou um coobrigado. Uma proposta tem de 1 a 5 participantes.');
termo('pf', 'pessoas', 'Pessoa física', 'Participante identificado por CPF, com renda pessoal comprovada.');
termo('pj', 'pessoas', 'Pessoa jurídica', 'Participante identificado por CNPJ (empresa), com faturamento comprovado. Só pode ser proponente em carro novo, moto nova e imóvel comercial.');
termo('renda', 'pessoas', 'Renda comprovada', 'Renda mensal (PF) ou faturamento mensal médio dos últimos 12 meses (PJ), aceito após análise dos comprovantes. A da proposta é a soma da dos participantes.');
termo('incorporadora', 'pessoas', 'Incorporadora', 'Empresa que constrói e vende o imóvel na planta. É o vendedor nesse caso e recebe o dinheiro por medição da obra.');

termo('bem', 'bem', 'Bem', 'Objeto comprado com o financiamento. É também a garantia do contrato.');
termo('categoria', 'bem', 'Categoria', 'Família do bem: Veículo ou Imóvel. Define a identificação do bem, a avaliação e onde a garantia é registrada.');
termo('subtipo', 'bem', 'Subtipo', 'Especialização da categoria: carro e moto (veículo); residencial, comercial e terreno (imóvel). Define prazo, cota de financiamento e documentos.');
termo('condicao', 'bem', 'Condição do bem', 'Estado do bem dentro do subtipo: novo ou usado (veículo); pronto ou na planta (imóvel residencial e comercial). Terreno não tem condição.');
termo('planta', 'bem', 'Imóvel na planta', 'Imóvel residencial ou comercial em construção, vendido pela incorporadora, com o dinheiro liberado conforme a obra avança.');
termo('terreno-rural', 'bem', 'Terreno rural', 'Terreno fora do perímetro urbano, com documentos próprios: CCIR, ITR e CAR.');
termo('identificacao', 'bem', 'Identificação do bem', 'Como o bem é reconhecido fora do sistema: chassi, placa e RENAVAM (veículo); matrícula e cartório (imóvel e terreno). Um bem só pode estar em uma proposta ativa.');
termo('valor-compra', 'bem', 'Valor de compra', 'Preço acordado entre comprador e vendedor.');
termo('valor-avaliacao', 'bem', 'Valor de avaliação', 'Valor atribuído ao bem por fonte independente: laudo, tabela de referência ou nota fiscal do novo.');
termo('valor-referencia', 'bem', 'Valor de referência', 'O menor entre o valor de compra e o valor de avaliação. Base do limite de financiamento.');
termo('ano-modelo', 'bem', 'Ano-modelo', 'Ano de modelo do veículo; base da regra de idade máxima.');
termo('laudo', 'bem', 'Laudo de avaliação', 'Documento do avaliador credenciado sobre um imóvel ou terreno: valor, situação e aceitação como garantia.');
termo('vistoria', 'bem', 'Vistoria do veículo', 'Inspeção do veículo usado vendido por particular: confere identificação, estado e que o vendedor é o dono.');
termo('medicao', 'bem', 'Medição da obra', 'Vistoria periódica de um engenheiro que apura o percentual executado da obra do imóvel na planta. Cada medição libera a parcela correspondente.');
termo('validade-avaliacao', 'bem', 'Validade da avaliação', 'Prazo em que a avaliação vale para contratar. Varia por categoria; vencida, o bem é reavaliado.');

termo('entrada', 'condicoes', 'Entrada', 'Parte do valor de compra paga sem financiamento: recurso próprio e, no imóvel residencial de pessoa física, também FGTS.');
termo('fgts', 'condicoes', 'Recurso do FGTS', 'Parte da entrada paga com saldo do FGTS do proponente, liberado pelo sistema do FGTS diretamente ao vendedor.');
termo('valor-financiado', 'condicoes', 'Valor financiado', 'Valor de compra menos a entrada. É o que o credor paga ao vendedor.');
termo('ltv', 'condicoes', 'Cota de financiamento', 'Valor financiado dividido pelo valor de referência (LTV). Cada tipo de bem tem um máximo.');
termo('prazo', 'condicoes', 'Prazo', 'Quantidade de parcelas mensais. Cada tipo de bem tem mínimo e máximo.');
termo('parcela', 'condicoes', 'Parcela', 'Valor mensal resultante da simulação. Nesta etapa é um dado: o cálculo fica fora do escopo.');
termo('comprometimento', 'condicoes', 'Comprometimento de renda', 'Parcela dividida pela renda comprovada da proposta. Tem um teto.');
termo('condicoes', 'condicoes', 'Condições do financiamento', 'Conjunto de valor de compra, entrada (própria e FGTS), valor financiado, prazo e parcela.');
termo('seguro-exigido', 'condicoes', 'Seguro exigido', 'Seguro que o tipo de bem obriga a contratar antes do contrato: MIP, DFI ou seguro do bem. O prestamista é opcional.');
termo('mip', 'condicoes', 'MIP', 'Seguro de morte e invalidez permanente: quita a dívida do imóvel ou terreno se o devedor morrer ou ficar inválido.');
termo('dfi', 'condicoes', 'DFI', 'Seguro de danos físicos ao imóvel construído (pronto ou na planta).');

termo('proposta', 'jornada', 'Proposta', 'Pedido de financiamento de um bem por um ou mais participantes. Vai do rascunho até virar contrato ou ser encerrada.');
termo('enquadramento', 'jornada', 'Enquadramento', 'Verificação de que a proposta respeita as regras do seu tipo de bem.');
termo('submissao', 'jornada', 'Submissão', 'Momento em que a proposta completa e enquadrada segue para avaliação e análise de crédito. Antes disso é rascunho.');
termo('analise', 'jornada', 'Análise de crédito', 'Decisão sobre a capacidade e o histórico de pagamento dos participantes: aprovada, aprovada com ressalva ou reprovada.');
termo('ressalva', 'jornada', 'Ressalva', 'Aprovação de crédito condicionada a condições diferentes das pedidas (valor menor, entrada maior, prazo menor).');
termo('contraproposta', 'jornada', 'Contraproposta', 'Nova versão das condições, emitida pelo credor a partir de uma ressalva. Tem validade; o proponente aceita ou recusa.');
termo('pendencia', 'jornada', 'Pendência', 'Algo que impede a proposta de seguir e pede ação de alguém (documento ilegível, avaliação vencida, cota estourada).');
termo('exigencia', 'jornada', 'Exigência', 'Pendência formal emitida pelo cartório ou pelo órgão de trânsito ao registrar a garantia.');
termo('contrato', 'jornada', 'Contrato', 'Instrumento assinado que formaliza a dívida e a garantia. Nasce de uma proposta aprovada.');
termo('alienacao', 'jornada', 'Alienação fiduciária', 'Garantia em que o bem fica em nome do credor até a quitação; o devedor tem a posse.');
termo('gravame', 'jornada', 'Gravame', 'Registro da alienação fiduciária do veículo no sistema nacional de gravames / órgão de trânsito.');
termo('registro', 'jornada', 'Registro da garantia', 'Averbação da alienação fiduciária na matrícula (imóvel e terreno) ou inclusão do gravame (veículo).');
termo('liberacao', 'jornada', 'Liberação do recurso', 'Pagamento do valor financiado ao vendedor. Única no bem pronto; por medição no imóvel na planta.');
termo('prorrogacao', 'jornada', 'Prorrogação da obra', 'Extensão do prazo previsto da obra do imóvel na planta, registrada no contrato até um limite.');

// ===== Dimensões de variação ===============================================

it('dimensoes', 'raiz', 'DOCUMENTO', 'Dimensões de variação', 'O que faz as regras variarem. Uma regra que varia por uma dimensão traz uma tabela com um valor para cada item dela, conferida pelo episteme.');
it('dim.tipo', 'dimensoes', 'DIMENSAO', 'Tipo de bem', `
Combinação de [[@t.categoria|categoria]], [[@t.subtipo|subtipo]] e [[@t.condicao|condição]] do bem. Uma regra pode dar
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
`);

it('dim.forma', 'dimensoes', 'DIMENSAO', 'Forma do participante', `
Se o [[@t.participante|participante]] é pessoa física ou jurídica. Muda os documentos exigidos e em quais tipos de bem
ele pode ser proponente.

- Pessoa física
- Pessoa jurídica
`);

// ===== Fontes normativas ===================================================

it('fontes', 'raiz', 'DOCUMENTO', 'Fontes normativas', 'Leis e normas em que as regras de negócio se apoiam.');
const fonte = (chave: string, titulo: string, texto: string) => it(`f.${chave}`, 'fontes', 'FONTE', titulo, texto);
fonte('9514', 'Lei 9.514/1997', 'Institui a alienação fiduciária de coisa imóvel: base da garantia do imóvel e do terreno e do seu registro na matrícula, no cartório de registro de imóveis.');
fonte('cc', 'Código Civil, arts. 1.361 e seguintes', 'Propriedade fiduciária de bem móvel: base da alienação fiduciária do veículo.');
fonte('gravame', 'Normas do CONTRAN e do sistema nacional de gravames', 'Regras para o registro do contrato e a inclusão do gravame do veículo no órgão de trânsito.');
fonte('8036', 'Lei 8.036/1990 (FGTS)', 'Regras do Fundo de Garantia do Tempo de Serviço, inclusive o uso do saldo na compra de imóvel residencial.');
fonte('lgpd', 'Lei 13.709/2018 (LGPD)', 'Lei Geral de Proteção de Dados Pessoais: os dados dos participantes só são tratados para a finalidade do financiamento.');

// ===== Requisitos não funcionais do sistema =================================

it('rnfs', 'raiz', 'DOCUMENTO', 'Requisitos não funcionais do sistema', 'Qualidades de negócio que valem para o sistema todo.');
it('rnfs.canais', 'rnfs', 'RNF_SISTEMA', 'Canais disponíveis 24x7', 'Proponente, vendedor e correspondente conseguem iniciar, acompanhar e responder propostas a qualquer hora, todos os dias. Etapas com decisão humana (análise manual, exigências) correm em horário comercial.');

// ===== Atores ==============================================================

it('atores', 'raiz', 'DOCUMENTO', 'Atores', 'Pessoas e sistemas que agem na jornada do financiamento.');
const ator = (chave: string, titulo: string, texto: string) => it(`a.${chave}`, 'atores', 'ATOR', titulo, texto);
ator('proponente', 'Proponente', 'Quem pede o financiamento e se torna devedor. Pessoa física em todos os tipos; pessoa jurídica só em carro novo, moto nova e imóvel comercial.');
ator('coobrigado', 'Coobrigado', 'Compõe renda e responde junto pela dívida. Até 4 por proposta. Em proposta de pessoa jurídica, pode ser pessoa física (sócio, avalista) ou jurídica.');
ator('vendedor', 'Vendedor', 'Quem vende o bem e recebe o dinheiro: concessionária, loja, particular (veículo), incorporadora (na planta) ou proprietário (imóvel pronto e terreno).');
ator('correspondente', 'Correspondente', 'Correspondente bancário ou corretor que inicia e acompanha propostas em nome do proponente, com sua própria carteira.');
ator('analista', 'Analista', 'Trata pendências, exigências e casos fora da alçada automática.');
ator('engenheiro', 'Engenheiro de medição', 'Vistoria a obra do imóvel na planta e apura o percentual executado.');
ator('avaliador', 'Avaliador', 'Sistema ou empresa externa que define o valor de avaliação: laudo (imóvel e terreno) ou tabela de referência (veículo usado).');
ator('bureau', 'Bureau de crédito', 'Sistema externo que informa score, restrições e endividamento dos participantes.');
ator('cartorio', 'Cartório de Registro de Imóveis', 'Registra a alienação fiduciária do imóvel ou terreno na matrícula. Pode emitir exigências.');
ator('transito', 'Órgão de trânsito', 'Sistema de gravames e órgão de trânsito: incluem o gravame do veículo.');
ator('seguradora', 'Seguradora', 'Emite os seguros exigidos pelo tipo de bem.');
ator('fgts', 'Sistema do FGTS', 'Decide a elegibilidade e libera o saldo de FGTS usado na entrada.');

// ===== Questões em aberto ==================================================

it('questoes', 'raiz', 'DOCUMENTO', 'Questões em aberto', 'Perguntas de negócio que mudam o modelo. Cada questão respondida aponta para a decisão que a responde.');
const q = (n: number, titulo: string, texto: string, aberta = false) =>
  add({ chave: `q${n}`, pai: 'questoes', tipo: 'QUESTAO', titulo, texto, situacao: aberta ? 'RASCUNHO' : 'APROVADO' });
q(1, 'O proponente pode ser pessoa jurídica?', 'Define se o [[@t.participante|participante]] tem duas formas e se a análise de crédito e os documentos variam.');
q(2, 'A proposta aceita coobrigado? Quantos?', 'Define a composição de renda e quem assina o contrato.');
q(3, 'Quem pode vender veículo?', 'Só loja e concessionária, ou também particular?');
q(4, 'Imóvel na planta entra no modelo?', 'Muda a liberação do recurso (em parcelas, por obra) e o ciclo de vida do contrato.');
q(5, 'Terreno é subtipo de imóvel ou categoria própria?', 'Define o reuso de identificação, avaliação e registro.');
q(6, 'Terreno rural entra no modelo?', 'O rural exige documentos próprios (CCIR, ITR, CAR).');
q(7, 'Libera o dinheiro com o gravame só solicitado?', 'No veículo novo, a concessionária só entrega o carro com o pagamento; o gravame é incluído no primeiro emplacamento.');
q(8, 'Seguros obrigatórios entram no modelo?', 'MIP e DFI no imóvel, prestamista e seguro do bem no veículo.');
q(9, 'Quem pode iniciar uma proposta?', 'Proponente, vendedor, correspondente?');
q(10, 'O que acontece com a aprovação com ressalva?', 'O proponente aceita no sistema, volta a rascunho ou recebe uma contraproposta?');
q(11, 'A entrada do imóvel pode usar FGTS ou subsídio?', 'Muda a composição da entrada e acrescenta um sistema externo.');
q(12, 'O mesmo bem pode estar em mais de uma proposta ativa?', 'Evita duas garantias sobre o mesmo bem.');
q(13, 'A avaliação do bem tem validade?', 'Se vencer antes da contratação, precisa ser refeita?');
q(14, 'Qual o limite de prorrogações da obra?', 'Quantidade de prorrogações ou meses acumulados aceitos antes de virar pendência para o analista.');
q(15, 'Qual o prazo de validade da contraproposta?', 'Prazo para o proponente aceitar ou recusar as condições da contraproposta.');
q(16, 'Qual a validade exata da avaliação em cada categoria?', 'Sugestão inicial: laudo de imóvel e terreno 180 dias; tabela de referência do veículo usado, o mês da consulta; nota fiscal do novo, 30 dias.');
q(17, 'Quais documentos são exigidos de participante pessoa jurídica?', 'Contrato social, balanço, faturamento, procuração dos sócios?');

// ===== Decisões ============================================================

it('decisoes', 'raiz', 'DOCUMENTO', 'Decisões', 'Decisões de negócio tomadas nas entrevistas de 2026-11-05 (registro completo em docs/definicoes).');
const d = (n: number, qs: number[], titulo: string, texto: string) =>
  add({
    chave: `d${n}`, pai: 'decisoes', tipo: 'DECISAO', titulo, situacao: 'APROVADO',
    texto: `Responde ${qs.map(x => `[[RESPONDE:@q${x}]]`).join(' e ')}.\n\n${texto}`
  });
d(1, [1], 'Pessoa jurídica só em carro novo, moto nova e imóvel comercial', 'Veículo usado, imóvel residencial e terreno: só [[@t.pf|pessoa física]].');
d(2, [2], 'Até 4 coobrigados; em proposta de PJ, coobrigado PF ou PJ', 'Uma proposta tem de 1 a 5 participantes. A renda da proposta é a soma da renda comprovada de todos.');
d(3, [9], 'Proponente, vendedor e correspondente iniciam a proposta', 'O [[@a.correspondente]] passa a ser ator próprio, com sua carteira de propostas.');
d(4, [3], 'Vendedor de veículo pode ser loja ou particular', 'Veículo usado vendido por particular exige [[@t.vistoria|vistoria]] e conferência de que o vendedor é o dono.');
d(5, [4], 'Na planta é condição do imóvel residencial e comercial', 'O imóvel ganha a condição pronto × na planta, como novo × usado no veículo. O vendedor do imóvel na planta é a [[@t.incorporadora]]. Terreno não tem condição.');
d(6, [4], 'Liberação da planta por medição, com prorrogação limitada', 'Cada [[@t.medicao|medição]] libera a parcela correspondente ao avanço da obra; a garantia é registrada antes da primeira liberação. Atraso aceita prorrogação até um limite; acima dele vira pendência para o [[@a.analista]].');
d(7, [5, 6], 'Terreno é subtipo de imóvel, urbano e rural', 'Reaproveita matrícula, laudo e cartório. O rural tem documentos próprios.');
d(8, [7], 'Nenhuma liberação sem garantia confirmada', 'Vale para todos os tipos de bem, inclusive o veículo novo.');
d(9, [10], 'Ressalva vira contraproposta com validade', 'A [[@t.ressalva|ressalva]] gera uma [[@t.contraproposta|contraproposta]]. Aceita, a proposta segue com as novas condições; recusada ou vencida, é reprovada.');
d(10, [12], 'Um bem em no máximo uma proposta ativa', 'A identificação do bem (chassi ou matrícula) é única entre propostas ativas.');
d(11, [13], 'A avaliação tem validade por categoria', 'Vencida antes da contratação, a proposta fica com pendência e o bem é reavaliado.');
d(12, [8], 'Seguros entram como exigência, sem cálculo', 'Imóvel pronto ou na planta: [[@t.mip|MIP]] e [[@t.dfi|DFI]]. Terreno: MIP. Veículo: seguro do bem. Prestamista opcional em todos. O prêmio não é calculado.');
d(13, [11], 'FGTS na entrada só no imóvel residencial de pessoa física', 'Elegibilidade e saldo são decididos pelo [[@a.fgts]]. Sem subsídio habitacional.');
d(14, [14], 'Prorrogação da obra até 180 dias acumulados', 'As prorrogações somadas podem estender o prazo previsto da obra em até 180 dias, em quantas vezes for preciso. Acima disso vira pendência para o [[@a.analista]].');
d(15, [15], 'Validade da contraproposta por categoria', 'Veículo: 5 dias corridos, porque a venda é rápida e a loja segura o bem por pouco tempo. Imóvel e terreno: 15 dias corridos.');
d(16, [16], 'Validade da avaliação por tipo de bem', 'Veículo novo: 30 dias (nota fiscal). Veículo usado: o mês da consulta à tabela de referência. Imóvel e terreno: 180 dias (laudo).');
d(17, [17], 'Documentos de participante pessoa jurídica', 'Contrato social e alterações, faturamento dos últimos 12 meses e documentos dos sócios, com procuração quando quem assina não é sócio. O faturamento é a [[@t.renda|renda comprovada]] da [[@t.pj|pessoa jurídica]].');

// ===== Domínios ============================================================

const dominio = (chave: string, titulo: string, classificacao: Item['classificacao'], texto: string) =>
  it(chave, 'raiz', 'DOMINIO', titulo, texto, { classificacao });

// Cada domínio nasce com a sua visão; o texto dela entra pela chave `<dominio>.vis`.
const visoes: Record<string, string> = {};
/** Visões alteradas ou criadas na etapa da vida do contrato: ficam "Em revisão". */
const visoesEmRevisao = new Set(['gar', 'car']);

// ----- Originação (principal) ----------------------------------------------

dominio('ori', 'Originação', 'Principal', 'Monta a proposta, enquadra nas regras do tipo de bem e acompanha até virar contrato. É onde mora a variação entre os tipos de bem: o coração do modelo.');
visoes['ori'] = `
## Propósito e fronteiras

Recebe o pedido de financiamento de um bem, reúne participantes, bem, condições e documentos, aplica as regras
do tipo de bem e conduz a proposta pela avaliação e pela análise de crédito até a aprovação. Entrega a proposta
aprovada à Contratação.

**Depende de:** [[DEPENDE_DE:@cre|Crédito]], [[DEPENDE_DE:@ava|Avaliação]], [[DEPENDE_DE:@pes|Cadastro de pessoas]] e [[DEPENDE_DE:@doc|Documentos]].

**Guarda:** o agregado [[@ori.agr.proposta]] e o seu [[@ori.cv.proposta]].

**Atores:** [[@a.proponente]], [[@a.coobrigado]], [[@a.vendedor]], [[@a.correspondente]], [[@a.analista]].

**Sujeito a:** [[@rnfs.canais]].

## Jornada

\`\`\`mermaid
flowchart LR
    A[Montagem<br/>da proposta] --> B[Submissão e<br/>enquadramento]
    B --> C[Avaliação<br/>do bem]
    B --> D[Análise<br/>de crédito]
    C --> E[Aprovação]
    D --> E
    D -. ressalva .-> X[Contraproposta]
    X -. aceita .-> E
    E --> F[Contratação]
\`\`\`

Avaliação e análise de crédito correm em paralelo; a proposta só é aprovada quando as duas concluíram.

## Jornada em eventos

Gerada pelo episteme (event storming): eventos em ordem, quem os provoca e o que disparam.

\`\`\`event-storming
\`\`\`
`;

it('ori.agr.proposta', 'ori', 'AGREGADO', 'Proposta', `
Fronteira de consistência da originação: [[@t.participante|participantes]], [[@t.bem|bem]], [[@t.condicoes|condições]],
documentos e os resultados da avaliação e do crédito **como chegaram à proposta**.

## Composição

| Elemento | Natureza | Observação |
|---|---|---|
| Participante | entidade | 1 a 5; um é o proponente; PF ou PJ |
| Bem | entidade | categoria, subtipo e condição fixados ao iniciar |
| Condições | objeto de valor | versionadas: cada contraproposta é uma nova versão |
| Documento | entidade | exigidos pelo tipo de bem e pela forma do participante |
| Avaliação | objeto de valor | valor, fonte, data e validade, vindos da Avaliação |
| Análise de crédito | objeto de valor | resultado e ressalvas, vindos do Crédito |
| Vendedor | referência | por identificador |

## Invariantes

1. Categoria, subtipo e condição nunca mudam depois de iniciada.
2. Só o rascunho e a proposta com pendência aceitam alteração de dados.
3. Participantes: [[USA:@rn.participantes]] e [[USA:@rn.pj]].
4. Bem: [[USA:@rn.bem-unico]].
5. Para submeter: completa e enquadrada ([[USA:@rn.enquadramento]]).
6. Toda mudança de valor, entrada, prazo ou avaliação dispara novo enquadramento.
7. Só é aprovada com avaliação válida ([[USA:@rn.validade-avaliacao]]) e crédito aprovado, ambos posteriores à última versão das condições, e com os documentos exigidos conferidos e aceitos.
8. Uma proposta gera no máximo um contrato.

**Emite:** [[EMITE:@evt.iniciada]], [[EMITE:@evt.submetida]], [[EMITE:@evt.pendencia]], [[EMITE:@evt.pendencia-resolvida]], [[EMITE:@evt.avaliacao-vencida]], [[EMITE:@evt.aprovada]], [[EMITE:@evt.reprovada]], [[EMITE:@evt.cancelada]], [[EMITE:@evt.expirada]], [[EMITE:@evt.contraproposta-emitida]], [[EMITE:@evt.contraproposta-aceita]], [[EMITE:@evt.contraproposta-recusada]].

Decisões que moldam o agregado: [[@d2]], [[@d9]], [[@d10]].

## Por que a proposta e o contrato são agregados separados

- A proposta muda muito e é editada por pessoas; o contrato é estável e reage a sistemas externos.
- Depois da emissão, nenhuma invariante do contrato precisa da proposta mudando junto.
- A proposta pode terminar sem contrato (reprovada, expirada, cancelada), que é a maioria dos casos.
`);

it('ori.cv.proposta', 'ori', 'CICLO_DE_VIDA', 'Ciclo de vida da proposta', `
Estados da [[@ori.agr.proposta|proposta]] e o evento de cada transição.

\`\`\`mermaid
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
\`\`\`

| De | Para | Evento | Regra que guarda |
|---|---|---|---|
| Rascunho | Em análise | [[@evt.submetida]] | [[@rn.enquadramento]] |
| Em análise | Com pendência | [[@evt.pendencia]] | [[@rn.ltv]], [[@rn.validade-avaliacao]] |
| Com pendência | Em análise | [[@evt.pendencia-resolvida]] | [[@rn.enquadramento]] |
| Em contraproposta | Reprovada | [[@evt.contraproposta-recusada]] | [[@rn.contraproposta]] |
| Em análise | Em contraproposta | [[@evt.contraproposta-emitida]] | [[@rn.contraproposta]] |
| Em análise | Aprovada | [[@evt.aprovada]] | [[@rn.aprovacao]] |
| Em análise | Reprovada | [[@evt.reprovada]] | [[@rn.aprovacao]] |
| Aprovada | Expirada | [[@evt.expirada]] | [[@rn.validade-proposta]] |
`);

// Eventos da originação
it('ori.eventos', 'ori', 'SECAO', 'Eventos', 'Fatos do domínio da originação.');
const evt = (chave: string, pai: string, titulo: string, texto: string) => it(`evt.${chave}`, pai, 'EVENTO', titulo, texto);
evt('iniciada', 'ori.eventos', 'PropostaIniciada', 'Uma [[@t.proposta|proposta]] foi aberta para um bem de categoria, subtipo e condição definidos. Provocado por [[@a.proponente]], [[@a.vendedor]] ou [[@a.correspondente]].');
evt('submetida', 'ori.eventos', 'PropostaSubmetida', 'A proposta completa e enquadrada foi submetida. Dispara [[DISPARA:@uc.avaliar]] e [[DISPARA:@uc.analisar]].');
evt('pendencia', 'ori.eventos', 'PropostaComPendencia', 'A proposta não pode seguir até alguém agir: cota estourada após a avaliação, avaliação vencida, documento recusado. Ver [[@rn.ltv]].');
evt('pendencia-resolvida', 'ori.eventos', 'PendenciaResolvida', 'Quem agiu resolveu a pendência (documento reenviado, entrada recomposta, bem reavaliado) e a proposta volta à análise, reenquadrada.');
evt('aprovada', 'ori.eventos', 'PropostaAprovada', 'Bem avaliado e crédito aprovado, com enquadramento válido. Começa a contar a validade ([[@rn.validade-proposta]]). Dispara [[DISPARA:@uc.emitir-contrato]].');
evt('reprovada', 'ori.eventos', 'PropostaReprovada', 'Crédito reprovado, bem recusado como garantia ou contraproposta recusada ou vencida.');
evt('cancelada', 'ori.eventos', 'PropostaCancelada', 'O [[@a.proponente]] desistiu antes do contrato.');
evt('expirada', 'ori.eventos', 'PropostaExpirada', 'A proposta aprovada não foi contratada dentro da validade.');
evt('contraproposta-emitida', 'ori.eventos', 'ContrapropostaEmitida', 'O crédito aprovou com ressalva e o credor emitiu nova versão das condições, com validade ([[@rn.contraproposta]]).');
evt('contraproposta-aceita', 'ori.eventos', 'ContrapropostaAceita', 'O [[@a.proponente]] aceitou a contraproposta; a proposta segue com as novas condições.');
evt('contraproposta-recusada', 'ori.eventos', 'ContrapropostaRecusada', 'O proponente recusou a contraproposta, ou ela venceu. A proposta é reprovada.');

// Regras da originação
it('ori.regras', 'ori', 'DOCUMENTO', 'Regras de Negócio', 'Regras da originação: comuns a todos os bens e variáveis por tipo de bem.');
it('ori.regras.comuns', 'ori.regras', 'SECAO', 'Regras comuns', 'Valem para qualquer tipo de bem.');
it('ori.regras.tipo', 'ori.regras', 'SECAO', 'Regras por tipo de bem', 'O que varia por categoria, subtipo e condição. Uma regra é definida no nível mais alto em que não varia.');
it('ori.regras.jornada', 'ori.regras', 'SECAO', 'Regras da jornada', 'Submissão, aprovação, contraproposta e validade.');

const rn = (chave: string, secao: string, titulo: string, texto: string, exemplo?: string) =>
  it(`rn.${chave}`, secao, 'REGRA', titulo, texto, exemplo ? { exemplo } : {});

rn('participantes', 'ori.regras.comuns', 'Participantes da proposta',
  'Uma proposta tem um [[@a.proponente]] e até 4 [[@a.coobrigado|coobrigados]]: de 1 a 5 [[@t.participante|participantes]], sem repetir CPF ou CNPJ ([[@rn.documento-valido]]). Pessoa física é maior e capaz. Os dados pessoais servem só ao financiamento ([[@f.lgpd]]). Decisão: [[@d2]].');
rn('pj', 'ori.regras.comuns', 'Pessoa jurídica como proponente',
  'O proponente pode ser [[@t.pj|pessoa jurídica]] só em carro novo, moto nova e imóvel comercial (pronto ou na planta). Em proposta de PJ, o coobrigado pode ser PF ou PJ. Decisão: [[@d1]].');
rn('vendedor', 'ori.regras.comuns', 'Vendedor identificado',
  'O [[@a.vendedor]] é identificado e diferente do proponente. Veículo usado vendido por particular exige [[@t.vistoria|vistoria]] aprovada. [[@t.planta|Imóvel na planta]] é vendido por [[@t.incorporadora|incorporadora]]. Decisões: [[@d4]], [[@d5]].');
rn('bem-unico', 'ori.regras.comuns', 'Bem em uma só proposta ativa',
  'A [[@t.identificacao|identificação do bem]] é única entre propostas ativas (rascunho, em análise, com pendência, em contraproposta, aprovada). Decisão: [[@d10]].');
rn('valor-financiado', 'ori.regras.comuns', 'Valor financiado',
  '[[@t.valor-financiado|Valor financiado]] = [[@t.valor-compra|valor de compra]] − [[@t.entrada|entrada]], e é maior que zero. A entrada soma recurso próprio e, quando permitido, FGTS ([[@rn.fgts]]).');
rn('ltv', 'ori.regras.comuns', 'Limite da cota de financiamento',
  'Valor financiado ≤ cota máxima do tipo de bem × [[@t.valor-referencia|valor de referência]] (o menor entre compra e avaliação). A cota máxima de cada tipo está em [[INCLUI:@rn.matriz]].',
  'Moto usada: compra R$ 20.000, tabela R$ 18.000, entrada R$ 4.000. Referência = 18.000; financiado 16.000 = 88,9% > 70% → não enquadra. A entrada mínima passa a ser R$ 7.400.');
rn('prazo', 'ori.regras.comuns', 'Prazo dentro da faixa do tipo',
  'O [[@t.prazo|prazo]] fica entre o mínimo e o máximo do tipo de bem ([[INCLUI:@rn.matriz]]).');
rn('comprometimento', 'ori.regras.comuns', 'Comprometimento de renda',
  '[[@t.comprometimento|Comprometimento de renda]]: [[@t.parcela|parcela]] ≤ 30% da [[@t.renda|renda comprovada]] somada dos participantes.');
rn('documentos', 'ori.regras.comuns', 'Documentos obrigatórios',
  'Os documentos exigidos pela categoria, pelo subtipo e pela forma de cada participante (PF ou PJ) estão presentes, sem documento repetido. Listas em [[INCLUI:@rn.documentos-tipo]] e [[INCLUI:@rn.documentos-participantes]].');

rn('matriz', 'ori.regras.tipo', 'Parâmetros por tipo de bem', `
Varia por [[VARIA_POR:@dim.tipo]]. **Valores de referência do modelo**, parametrizáveis por projeto.

| Parâmetro | Carro novo | Carro usado | Moto nova | Moto usada | Residencial pronto | Residencial na planta | Comercial pronto | Comercial na planta | Terreno urbano | Terreno rural |
|---|---|---|---|---|---|---|---|---|---|---|
| Prazo (meses) | 12–60 | 12–60 | 12–48 | 12–36 | 60–420 | 60–420 | 60–240 | 60–240 | 60–180 | 60–120 |
| Cota máxima | 90% | 80% | 90% | 70% | 80% | 80% | 70% | 70% | 60% | 50% |
| Aceita PJ | sim | não | sim | não | não | não | sim | sim | não | não |
| FGTS na entrada | não | não | não | não | sim | sim | não | não | não | não |
| Idade máxima ao fim do contrato | — | 15 anos | — | 10 anos | — | — | — | — | — | — |
| Liberação | única | única | única | única | única | por medição | única | por medição | única | única |

Cota de financiamento = [[@t.ltv|LTV]]. Regras que usam esta tabela: [[@rn.ltv]], [[@rn.prazo]], [[@rn.pj]], [[@rn.fgts]], [[@rn.idade-veiculo]].
`);
rn('idade-veiculo', 'ori.regras.tipo', 'Idade máxima do veículo usado',
  'No fim do contrato, o veículo usado tem no máximo 15 anos (carro) ou 10 anos (moto), contados do [[@t.ano-modelo|ano-modelo]].',
  'Carro usado, ano-modelo 2016, prazo de 60 meses contratado em 2026: termina em 2031 = 2016 + 15 → enquadra, no limite.');
rn('idade-proponente', 'ori.regras.tipo', 'Idade do proponente no imóvel',
  'No imóvel e no terreno, a idade do proponente pessoa física somada ao [[@t.prazo|prazo]] não passa de 80 anos e 6 meses.',
  'Proponente com 60 anos pedindo 420 meses: 60 + 35 = 95 anos → não enquadra; o prazo máximo possível é de 246 meses.');
rn('restricao-veiculo', 'ori.regras.tipo', 'Veículo usado sem restrição',
  'O veículo usado não tem restrição, bloqueio ou outro gravame no [[@a.transito]].');
rn('terreno', 'ori.regras.tipo', 'Terreno sem construção',
  'O terreno não tem construção averbada na matrícula. O [[@t.terreno-rural|terreno rural]] apresenta CCIR, ITR e CAR. Decisão: [[@d7]].');
rn('fgts', 'ori.regras.tipo', 'Uso do FGTS na entrada',
  'Só o imóvel residencial (pronto ou na planta) de proponente [[@t.pf|pessoa física]] aceita [[@t.fgts|recurso do FGTS]] na entrada. A elegibilidade e o saldo são decididos pelo [[@a.fgts]]. Base legal: [[@f.8036]]. Decisão: [[@d13]].');
rn('documentos-tipo', 'ori.regras.tipo', 'Documentos do bem por tipo', `
Varia por [[VARIA_POR:@dim.tipo]].

| Tipo | Documentos do bem |
|---|---|
| Veículo novo | nota fiscal ou pedido da concessionária |
| Veículo usado | CRLV; laudo de vistoria se vendido por particular |
| Residencial pronto / Comercial pronto | matrícula atualizada, habite-se (comercial), certidões do vendedor |
| Residencial na planta / Comercial na planta | contrato com a incorporadora, memorial de incorporação, cronograma da obra |
| Terreno urbano | matrícula, IPTU, certidões do vendedor |
| Terreno rural | matrícula, CCIR, ITR, CAR, certidões do vendedor |
`);
rn('documentos-participantes', 'ori.regras.tipo', 'Documentos dos participantes', `
Varia por [[VARIA_POR:@dim.tipo]] e por [[VARIA_POR:@dim.forma]]: cada cruzamento diz o que o participante entrega.
"—" é onde a [[@t.pj|pessoa jurídica]] não pode ser proponente ([[@rn.pj]], [[@d1]]). Documentos da PJ: [[@d17]].

| Tipo | Pessoa física | Pessoa jurídica |
|---|---|---|
| Veículo novo | identidade, comprovante de renda, comprovante de residência | contrato social, faturamento de 12 meses, documentos dos sócios e procuração |
| Veículo usado | identidade, comprovante de renda, comprovante de residência | — |
| Residencial pronto / Residencial na planta | identidade, comprovante de renda, comprovante de residência, estado civil | — |
| Comercial pronto / Comercial na planta | identidade, comprovante de renda, comprovante de residência, estado civil | contrato social, faturamento de 12 meses, documentos dos sócios e procuração |
| Terreno | identidade, comprovante de renda, comprovante de residência, estado civil | — |
`);

rn('enquadramento', 'ori.regras.jornada', 'Enquadramento para submeter',
  'Para a [[@t.submissao|submissão]], o [[@t.enquadramento|enquadramento]] exige a proposta completa e respeitando [[INCLUI:@rn.participantes]], [[INCLUI:@rn.pj]], [[INCLUI:@rn.vendedor]], [[INCLUI:@rn.bem-unico]], [[INCLUI:@rn.valor-financiado]], [[INCLUI:@rn.ltv]], [[INCLUI:@rn.prazo]], [[INCLUI:@rn.comprometimento]], [[INCLUI:@rn.documentos]] e as regras do seu tipo de bem: [[INCLUI:@rn.idade-veiculo]], [[INCLUI:@rn.idade-proponente]], [[INCLUI:@rn.restricao-veiculo]], [[INCLUI:@rn.terreno]] e [[INCLUI:@rn.fgts]].');
rn('aprovacao', 'ori.regras.jornada', 'Aprovação da proposta',
  'A proposta é aprovada quando há avaliação válida aceita como garantia, crédito aprovado **e** os documentos exigidos ([[@rn.documentos]]) conferidos e aceitos, e o enquadramento continua válido com o valor de avaliação. Crédito reprovado ou bem recusado como garantia reprova a proposta; documento recusado deixa a proposta com pendência.');
rn('contraproposta', 'ori.regras.jornada', 'Contraproposta', `
Crédito aprovado com [[@t.ressalva|ressalva]] gera uma [[@t.contraproposta|contraproposta]] com validade. Aceita, as
novas condições são reenquadradas e a proposta segue; recusada ou vencida, a proposta é reprovada.
Decisões: [[@d9]], [[@d15]]. Varia por [[VARIA_POR:@dim.tipo]].

| Tipo | Validade da contraproposta |
|---|---|
| Veículo | 5 dias corridos |
| Imóvel | 15 dias corridos |
`);
rn('validade-proposta', 'ori.regras.jornada', 'Validade da proposta aprovada',
  'Proposta aprovada vale 30 dias para virar contrato; depois expira.');
rn('validade-avaliacao', 'ori.regras.jornada', 'Avaliação válida para contratar',
  'A avaliação precisa estar dentro da [[@t.validade-avaliacao|validade]] do seu tipo de bem ([[@rn.fonte-avaliacao]]) na emissão do contrato; vencida, a proposta fica com pendência e o bem é reavaliado. Decisões: [[@d11]], [[@d16]].');

// Casos de uso da originação
it('ori.ucs', 'ori', 'SECAO', 'Casos de uso', 'Casos de uso da originação.');
const uc = (chave: string, pai: string, titulo: string, texto: string) => it(`uc.${chave}`, pai, 'CASO_DE_USO', titulo, texto);
uc('iniciar', 'ori.ucs', 'Iniciar proposta', `
**Atores:** [[@a.proponente]], [[@a.vendedor]] ou [[@a.correspondente]]. Decisão: [[@d3]].

**Fluxo principal**
1. O ator escolhe a categoria, o subtipo e a condição do bem.
2. O sistema cria a proposta em rascunho com esses dados fixos.

**Pós-condição:** proposta em rascunho. Emite [[EMITE:@evt.iniciada]].
`);
uc('participantes', 'ori.ucs', 'Informar participantes', `
**Atores:** [[@a.proponente]] ou [[@a.correspondente]].

**Fluxo principal**
1. O ator informa o proponente (PF ou PJ, conforme [[USA:@rn.pj]]) e até 4 coobrigados.
2. O sistema confere [[USA:@rn.participantes]] e anexa os documentos de cada um.

**Fluxo alternativo:** proponente PJ num tipo de bem que não aceita PJ → o sistema recusa e explica a regra.
`);
uc('bem', 'ori.ucs', 'Informar bem e vendedor', `
**Atores:** [[@a.proponente]], [[@a.vendedor]] ou [[@a.correspondente]].

**Fluxo principal**
1. O ator informa a identificação do bem, o valor de compra e o vendedor.
2. O sistema confere [[USA:@rn.bem-unico]] e [[USA:@rn.vendedor]].
3. Para veículo usado de particular, o sistema exige a vistoria.
`);
uc('condicoes', 'ori.ucs', 'Simular e escolher condições', `
**Ator:** [[@a.proponente]].

**Fluxo principal**
1. O proponente informa entrada (própria e, se permitido, FGTS — [[USA:@rn.fgts]]) e prazo.
2. O sistema mostra a parcela simulada e o enquadramento em [[USA:@rn.ltv]], [[USA:@rn.prazo]] e [[USA:@rn.comprometimento]].
`);
uc('submeter', 'ori.ucs', 'Submeter proposta', `
**Ator:** [[@a.proponente]] ou [[@a.correspondente]]. Faz a [[@t.submissao|submissão]] da proposta.

**Fluxo principal**
1. O ator submete a proposta em rascunho.
2. O sistema aplica [[USA:@rn.enquadramento]].
3. O sistema muda a proposta para em análise. Emite [[EMITE:@evt.submetida]].

**Fluxo alternativo:** incompleta ou fora do enquadramento → continua em rascunho, com a lista do que falta.
`);
uc('decidir', 'ori.ucs', 'Decidir proposta', `
**Ator:** nenhum: é automático, disparado pelos resultados da avaliação, do crédito e da conferência dos documentos.

**Fluxo principal**
1. Com bem avaliado, crédito aprovado e documentos aceitos, o sistema confere [[USA:@rn.aprovacao]] e [[USA:@rn.enquadramento]] com o valor de avaliação.
2. A proposta é aprovada. Emite [[EMITE:@evt.aprovada]].

**Fluxos alternativos**
- Crédito reprovado ou bem recusado como garantia → a proposta é reprovada. Emite [[EMITE:@evt.reprovada]].
- Cota estourada com o valor de avaliação, ou documento recusado → emite [[EMITE:@evt.pendencia]].
- Crédito aprovado com ressalva → segue para a contraproposta ([[USA:@rn.contraproposta]]).
`);
uc('expirar', 'ori.ucs', 'Expirar proposta', `
**Ator:** nenhum: acontece pela passagem do tempo.

**Fluxo principal:** a proposta aprovada passa da validade sem contrato ([[USA:@rn.validade-proposta]]) e é encerrada. Emite [[EMITE:@evt.expirada]].
`);
uc('resolver-pendencia', 'ori.ucs', 'Resolver pendência', `
**Atores:** [[@a.proponente]], [[@a.correspondente]] ou [[@a.analista]].

**Fluxo principal**
1. O ator vê a [[@t.pendencia|pendência]] e age: reenvia documento, recompõe a entrada ou pede nova avaliação.
2. O sistema reenquadra ([[USA:@rn.enquadramento]]) e a proposta volta à análise. Emite [[EMITE:@evt.pendencia-resolvida]].
`);
uc('contraproposta', 'ori.ucs', 'Responder contraproposta', `
**Ator:** [[@a.proponente]].

**Fluxo principal**
1. O proponente vê as condições da contraproposta e a validade ([[USA:@rn.contraproposta]]).
2. O proponente aceita; o sistema reenquadra. Emite [[EMITE:@evt.contraproposta-aceita]].

**Fluxo alternativo:** recusa ou vencimento → a proposta é reprovada. Emite [[EMITE:@evt.contraproposta-recusada]].
`);
uc('cancelar', 'ori.ucs', 'Cancelar proposta', `
**Ator:** [[@a.proponente]].

**Pré-condição:** proposta ainda sem contrato.

**Fluxo principal:** o proponente desiste; o sistema encerra a proposta. Emite [[EMITE:@evt.cancelada]].
`);

// ----- Crédito --------------------------------------------------------------

dominio('cre', 'Crédito', 'Suporte', 'Decide sobre os participantes. A decisão é em boa parte terceirizada (bureau, motor de crédito); o domínio orquestra e guarda o resultado.');
visoes['cre'] = `
## Propósito e fronteiras

Recebe a proposta submetida, consulta o [[@a.bureau]] e decide: aprovada, aprovada com ressalva ou reprovada.
A decisão volta para a Originação como fato.

**Depende de:** [[DEPENDE_DE:@a.bureau]].

**Atores:** [[@a.bureau]], [[@a.analista]]. **Eventos:** [[@evt.analise-solicitada]], [[@evt.credito-aprovado]], [[@evt.credito-ressalva]], [[@evt.credito-reprovado]].

## Jornada em eventos

Gerada pelo episteme (event storming): eventos em ordem, quem os provoca e o que disparam.

\`\`\`event-storming
\`\`\`
`;
it('cre.eventos', 'cre', 'SECAO', 'Eventos', 'Fatos do domínio de crédito.');
evt('analise-solicitada', 'cre.eventos', 'AnaliseCreditoSolicitada', 'A análise dos participantes foi pedida após a submissão.');
evt('credito-aprovado', 'cre.eventos', 'CreditoAprovado', 'Os participantes têm capacidade e histórico para as condições pedidas. Dispara [[DISPARA:@uc.decidir]].');
evt('credito-ressalva', 'cre.eventos', 'CreditoAprovadoComRessalva', 'Aprovação condicionada a outras condições. Dispara [[DISPARA:@uc.contraproposta]] por meio da contraproposta ([[@rn.contraproposta]]).');
evt('credito-reprovado', 'cre.eventos', 'CreditoReprovado', 'Os participantes não têm capacidade ou histórico. Dispara [[DISPARA:@uc.decidir]], que reprova a proposta.');
it('rnfd.credito', 'cre', 'RNF_DOMINIO', 'Resposta do crédito em até 1 dia útil', 'O proponente recebe a decisão de crédito (aprovada, com ressalva ou reprovada) em até 1 dia útil após a submissão. Casos em análise manual podem levar mais, e o proponente é avisado.');
it('cre.ucs', 'cre', 'SECAO', 'Casos de uso', 'Casos de uso do crédito.');
uc('analisar', 'cre.ucs', 'Analisar crédito', `
**Atores:** [[@a.bureau]], [[@a.analista]]. Faz a [[@t.analise|análise de crédito]] dos participantes, no prazo de [[USA:@rnfd.credito]].

**Fluxo principal**
1. O sistema consulta o bureau para cada participante. Emite [[EMITE:@evt.analise-solicitada]].
2. O sistema confere [[USA:@rn.comprometimento]] com a renda comprovada.
3. O sistema registra a decisão. Emite [[EMITE:@evt.credito-aprovado]], [[EMITE:@evt.credito-ressalva]] ou [[EMITE:@evt.credito-reprovado]].

**Fluxo alternativo:** fora da alçada automática → o analista decide.
`);

// ----- Avaliação ------------------------------------------------------------

dominio('ava', 'Avaliação', 'Suporte', 'Define o valor de avaliação do bem e, no imóvel na planta, mede o avanço da obra.');
visoes['ava'] = `
## Propósito e fronteiras

Define o [[@t.valor-avaliacao|valor de avaliação]] pela fonte certa para cada tipo de bem e controla a validade
da avaliação. No imóvel na planta, faz as [[@t.medicao|medições]] que liberam as parcelas.

**Depende de:** [[DEPENDE_DE:@a.avaliador]].

**Atores:** [[@a.avaliador]], [[@a.engenheiro]].

## Jornada em eventos

Gerada pelo episteme (event storming): eventos em ordem, quem os provoca e o que disparam.

\`\`\`event-storming
\`\`\`
`;
it('ava.eventos', 'ava', 'SECAO', 'Eventos', 'Fatos do domínio de avaliação.');
evt('avaliacao-solicitada', 'ava.eventos', 'AvaliacaoSolicitada', 'A avaliação do bem foi pedida após a submissão.');
evt('bem-avaliado', 'ava.eventos', 'BemAvaliado', 'O bem recebeu valor de avaliação e foi aceito como garantia. A Originação recalcula o valor de referência e o enquadramento ([[@rn.ltv]]). Dispara [[DISPARA:@uc.decidir]].');
evt('bem-recusado', 'ava.eventos', 'BemRecusadoComoGarantia', 'O avaliador não aceitou o bem como garantia. Dispara [[DISPARA:@uc.decidir]], que reprova a proposta.');
evt('avaliacao-vencida', 'ava.eventos', 'AvaliacaoVencida', 'A avaliação passou da validade antes da contratação ([[@rn.validade-avaliacao]]). Dispara [[DISPARA:@uc.avaliar]].');
evt('medicao', 'ava.eventos', 'MedicaoRealizada', 'O [[@a.engenheiro]] apurou o percentual executado da obra. Dispara [[DISPARA:@uc.liberar-parcela]].');
it('ava.regras', 'ava', 'DOCUMENTO', 'Regras de Negócio', 'Regras da avaliação.');
rn('fonte-avaliacao', 'ava.regras', 'Fonte da avaliação por tipo de bem', `
Varia por [[VARIA_POR:@dim.tipo]].

| Tipo | Fonte | Validade ([[@d16]]) |
|---|---|---|
| Veículo novo | nota fiscal ou preço de fábrica | 30 dias |
| Veículo usado | tabela de referência | mês da consulta |
| Residencial ou comercial | [[@t.laudo|laudo]] de avaliador credenciado | 180 dias |
| Terreno | laudo de avaliador credenciado | 180 dias |
`);
rn('medicao', 'ava.regras', 'Medição da obra',
  'A [[@t.medicao|medição]] apura o percentual acumulado executado; nunca diminui em relação à anterior. Decisão: [[@d6]].');
it('ava.ucs', 'ava', 'SECAO', 'Casos de uso', 'Casos de uso da avaliação.');
uc('avaliar', 'ava.ucs', 'Avaliar bem', `
**Ator:** [[@a.avaliador]].

**Fluxo principal**
1. O sistema pede a avaliação na fonte do tipo de bem ([[USA:@rn.fonte-avaliacao]]). Emite [[EMITE:@evt.avaliacao-solicitada]].
2. O avaliador devolve valor e aceitação. Emite [[EMITE:@evt.bem-avaliado]] ou [[EMITE:@evt.bem-recusado]].
`);
uc('medir', 'ava.ucs', 'Medir obra', `
**Ator:** [[@a.engenheiro]].

**Pré-condição:** contrato de imóvel na planta com garantia constituída.

**Fluxo principal:** o engenheiro vistoria a obra e informa o percentual acumulado ([[USA:@rn.medicao]]). Emite [[EMITE:@evt.medicao]].
`);

// ----- Contratação ----------------------------------------------------------

dominio('con', 'Contratação', 'Suporte', 'Emite e acompanha o contrato a partir da proposta aprovada: seguros, assinaturas e, no imóvel na planta, o prazo da obra.');
visoes['con'] = `
## Propósito e fronteiras

Transforma a proposta aprovada em [[@t.contrato|contrato]]: exige os seguros, emite o instrumento, colhe as assinaturas
e entrega o contrato assinado à Garantia.

**Depende de:** [[DEPENDE_DE:@ori|Originação]] e [[DEPENDE_DE:@a.seguradora]].

**Guarda:** o agregado [[@con.agr.contrato]] e o seu [[@con.cv.contrato]]. **Atores:** [[@a.proponente]], [[@a.coobrigado]], [[@a.vendedor]], [[@a.seguradora]].

## Jornada em eventos

Gerada pelo episteme (event storming): eventos em ordem, quem os provoca e o que disparam.

\`\`\`event-storming
\`\`\`
`;
it('con.agr.contrato', 'con', 'AGREGADO', 'Contrato', `
Fronteira de consistência da formalização e da garantia: condições finais (cópia congelada da proposta), seguros,
assinaturas, garantia, liberações e, no imóvel na planta, o cronograma da obra. Referencia a [[@ori.agr.proposta|proposta]]
pelo identificador, sem alterá-la.

## Composição

| Elemento | Natureza | Observação |
|---|---|---|
| Condições finais | objeto de valor | imutáveis após a emissão |
| Seguro exigido | entidade | conforme o tipo de bem |
| Assinatura | entidade | uma por signatário exigido |
| Garantia | entidade | órgão, protocolo, situação, exigências |
| Liberação | entidade | única ou por medição |
| Cronograma da obra | objeto de valor | só na planta: prazo previsto e prorrogações |

## Invariantes

1. Condições imutáveis após a emissão.
2. Só é emitido com os seguros exigidos contratados ([[USA:@rn.seguros]]).
3. Só vai a registro com todas as assinaturas exigidas ([[USA:@rn.assinaturas]]).
4. Retorno de registro só vale se a identificação registrada for a do contrato ([[USA:@rn.retorno-registro]]).
5. Nenhuma liberação sem garantia constituída ([[USA:@rn.sem-garantia-sem-dinheiro]]); soma liberada ≤ valor financiado ([[USA:@rn.liberacao-acumulada]]).
6. Prorrogações da obra até o limite ([[USA:@rn.prorrogacao]]).
7. Só o contrato formalizado tem quitação ou renegociação, uma de cada vez ([[USA:@rn.uma-operacao]]).
8. As condições mudam depois de formalizado só por amortização ou por aditivo vigente: cada mudança é uma nova versão, e as anteriores ficam guardadas.
9. Quitado, o contrato tem o termo de quitação emitido e a garantia baixada ([[USA:@rn.termo-quitacao]]).

**Emite:** [[EMITE:@evt.condicoes-aceitas]], [[EMITE:@evt.seguros]], [[EMITE:@evt.contrato-emitido]], [[EMITE:@evt.contrato-assinado]], [[EMITE:@evt.prorrogado]], [[EMITE:@evt.contrato-cancelado]], [[EMITE:@evt.contrato-quitado]], [[EMITE:@evt.condicoes-alteradas]], [[EMITE:@evt.termo-quitacao]].
`, EM_REVISAO);
it('con.cv.contrato', 'con', 'CICLO_DE_VIDA', 'Ciclo de vida do contrato', `
Estados do [[@con.agr.contrato|contrato]]. O estado "Em obra" só existe no imóvel na planta.

\`\`\`mermaid
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
    Formalizado --> Formalizado: CondicoesDoContratoAlteradas
    Formalizado --> Quitado: ContratoQuitado
    Quitado --> Encerrado: GarantiaBaixada
    Encerrado --> [*]
    Cancelado --> [*]
\`\`\`

| Transição | Evento | Regra |
|---|---|---|
| Emissão | [[@evt.contrato-emitido]] | [[@rn.seguros]], [[@rn.validade-avaliacao]] |
| Assinatura | [[@evt.contrato-assinado]] | [[@rn.assinaturas]] |
| Garantia constituída | [[@evt.garantia-registrada]] | [[@rn.retorno-registro]] |
| Liberação | [[@evt.recurso-liberado]], [[@evt.parcela-obra]] | [[@rn.sem-garantia-sem-dinheiro]] |
| Prorrogação | [[@evt.prorrogado]] | [[@rn.prorrogacao]] |
| Cancelamento | [[@evt.contrato-cancelado]] | — |
| Nova versão das condições (amortização ou aditivo) | [[@evt.condicoes-alteradas]] | [[@rn.amortizacao]], [[@rn.aditivo]] |
| Quitação | [[@evt.contrato-quitado]] | [[@rn.quitacao-antecipada]] |
| Encerramento | [[@evt.garantia-baixada]] | [[@rn.termo-quitacao]] |
`, EM_REVISAO);
it('con.eventos', 'con', 'SECAO', 'Eventos', 'Fatos do domínio de contratação.');
evt('condicoes-aceitas', 'con.eventos', 'CondicoesAceitas', 'O [[@a.proponente]] aceitou as condições finais da proposta aprovada.');
evt('seguros', 'con.eventos', 'SegurosContratados', 'Os seguros exigidos pelo tipo de bem foram emitidos pela [[@a.seguradora]] ([[@rn.seguros]]).');
evt('contrato-emitido', 'con.eventos', 'ContratoEmitido', 'O contrato foi emitido com as condições finais.');
evt('contrato-assinado', 'con.eventos', 'ContratoAssinado', 'Todas as assinaturas exigidas foram colhidas. Dispara [[DISPARA:@uc.registrar]].');
evt('contrato-cancelado', 'con.eventos', 'ContratoCancelado', 'O contrato foi desfeito antes de formalizado: desistência antes de assinar ou registro recusado sem solução. A garantia, se houver, é baixada.');
evt('prorrogado', 'con.eventos', 'PrazoDaObraProrrogado', 'O prazo previsto da obra foi estendido dentro do limite ([[@rn.prorrogacao]]).');
it('con.regras', 'con', 'DOCUMENTO', 'Regras de Negócio', 'Regras da contratação.');
rn('seguros', 'con.regras', 'Seguros exigidos por tipo de bem', `
O contrato só é emitido com os [[@t.seguro-exigido|seguros exigidos]] contratados. Varia por [[VARIA_POR:@dim.tipo]].
Decisão: [[@d12]].

| Tipo | Obrigatórios | Opcional |
|---|---|---|
| Residencial ou comercial | [[@t.mip|MIP]] e [[@t.dfi|DFI]] | prestamista |
| Terreno | MIP | prestamista |
| Veículo | seguro do bem | prestamista |
`);
rn('assinaturas', 'con.regras', 'Assinaturas exigidas',
  'Assinam todos os [[@t.participante|participantes]]. No imóvel e no terreno assinam também o [[@a.vendedor]] e os cônjuges, quando houver.');
rn('prorrogacao', 'con.regras', 'Prorrogação do prazo da obra',
  'No imóvel na planta, o contrato aceita [[@t.prorrogacao|prorrogações]] do prazo da obra que, somadas, não passem de 180 dias além do prazo previsto; acima disso vira pendência para o [[@a.analista]]. Decisões: [[@d6]], [[@d14]].',
  'Obra prevista para 30/06; prorrogações de 90 e de 60 dias somam 150 → aceitas. Um novo pedido de 45 dias somaria 195 → pendência para o analista.');
it('rnfd.contrato', 'con', 'RNF_DOMINIO', 'Contrato disponível em até 2 dias úteis', 'Depois que o proponente aceita as condições finais, o contrato fica disponível para assinatura em até 2 dias úteis.');
it('con.ucs', 'con', 'SECAO', 'Casos de uso', 'Casos de uso da contratação.');
uc('emitir-contrato', 'con.ucs', 'Emitir contrato', `
**Atores:** [[@a.proponente]], [[@a.seguradora]].

**Pré-condição:** proposta aprovada, dentro da validade.

**Fluxo principal**
1. O proponente aceita as condições finais. No imóvel na planta, informa a previsão de entrega da obra, do cronograma da [[@t.incorporadora|incorporadora]]: é o prazo previsto do cronograma da obra. Emite [[EMITE:@evt.condicoes-aceitas]].
2. O sistema exige os seguros ([[USA:@rn.seguros]]) e confere a avaliação ([[USA:@rn.validade-avaliacao]]). Emite [[EMITE:@evt.seguros]].
3. O sistema emite o contrato, no prazo de [[USA:@rnfd.contrato]]. Emite [[EMITE:@evt.contrato-emitido]].
`);
uc('assinar', 'con.ucs', 'Assinar contrato', `
**Atores:** [[@a.proponente]], [[@a.coobrigado]], [[@a.vendedor]].

**Fluxo principal:** cada signatário exigido ([[USA:@rn.assinaturas]]) assina; com a última assinatura, emite [[EMITE:@evt.contrato-assinado]].
`);
uc('cancelar-contrato', 'con.ucs', 'Cancelar contrato', `
**Atores:** [[@a.proponente]] (desistência antes de assinar) ou [[@a.analista]] (registro recusado sem solução).

**Fluxo principal:** o contrato ainda não formalizado é desfeito e a garantia, se registrada, é baixada. Emite [[EMITE:@evt.contrato-cancelado]].
`);
uc('prorrogar', 'con.ucs', 'Prorrogar prazo da obra', `
**Atores:** [[@a.vendedor]] (incorporadora), [[@a.analista]].

**Fluxo principal:** a incorporadora pede nova data; o sistema aplica [[USA:@rn.prorrogacao]] e registra. Emite [[EMITE:@evt.prorrogado]].

**Fluxo alternativo:** acima do limite → pendência para o analista decidir.
`);

// ----- Garantia -------------------------------------------------------------

dominio('gar', 'Garantia', 'Suporte', 'Constitui e acompanha a alienação fiduciária no cartório (imóvel e terreno) ou no órgão de trânsito (veículo).');
visoes['gar'] = `
## Propósito e fronteiras

Registra a [[@t.alienacao|alienação fiduciária]]: averbação na matrícula ou [[@t.gravame|gravame]] do veículo. Na vida
do contrato, averba o [[@t.aditivo|aditivo]] da renegociação e, quitado o contrato, faz a [[@t.baixa|baixa da garantia]].
Trata [[@t.exigencia|exigências]] e retornos assíncronos, que podem chegar repetidos ou fora de ordem.

**Depende de:** [[DEPENDE_DE:@con|Contratação]], [[DEPENDE_DE:@a.cartorio]] e [[DEPENDE_DE:@a.transito]]. **Atores:** [[@a.cartorio]], [[@a.transito]], [[@a.analista]].

## Jornada em eventos

Gerada pelo episteme (event storming): eventos em ordem, quem os provoca e o que disparam.

\`\`\`event-storming
\`\`\`
`;
it('gar.eventos', 'gar', 'SECAO', 'Eventos', 'Fatos do domínio de garantia.');
evt('registro-solicitado', 'gar.eventos', 'RegistroSolicitado', 'O contrato assinado foi enviado ao órgão de registro com um protocolo nosso.');
evt('garantia-registrada', 'gar.eventos', 'GarantiaRegistrada', 'O órgão confirmou o registro do bem do contrato ([[@rn.retorno-registro]]). Dispara [[DISPARA:@uc.liberar]].');
evt('exigencia', 'gar.eventos', 'ExigenciaEmitida', 'O [[@a.cartorio]] emitiu exigência para registrar. Dispara [[DISPARA:@uc.exigencia]].');
evt('exigencia-cumprida', 'gar.eventos', 'ExigenciaCumprida', 'O [[@a.analista]] cumpriu a exigência. Dispara [[DISPARA:@uc.registrar]] (reenvio).');
evt('registro-recusado', 'gar.eventos', 'RegistroRecusado', 'O órgão recusou o registro. Dispara [[DISPARA:@uc.registro-recusado]].');
it('gar.regras', 'gar', 'DOCUMENTO', 'Regras de Negócio', 'Regras da garantia.');
rn('orgao', 'gar.regras', 'Órgão de registro por categoria',
  'Imóvel e terreno: [[@a.cartorio]] da matrícula ([[@f.9514]]). Veículo: [[@a.transito]], pelo sistema de gravames ([[@f.cc]], [[@f.gravame]]).');
rn('retorno-registro', 'gar.regras', 'Retorno de registro é fato, não pedido',
  'O retorno do órgão é classificado, não recusado: se a identificação registrada é a do bem do contrato, a garantia está constituída; se é de outro bem, vira pendência para o [[@a.analista]]. Retorno repetido do mesmo protocolo não tem novo efeito.');
it('gar.ucs', 'gar', 'SECAO', 'Casos de uso', 'Casos de uso da garantia.');
uc('registrar', 'gar.ucs', 'Registrar garantia', `
**Atores:** [[@a.cartorio]] ou [[@a.transito]]. Faz o [[@t.registro|registro da garantia]].

**Fluxo principal**
1. O sistema envia o contrato ao órgão da categoria ([[USA:@rn.orgao]]). Emite [[EMITE:@evt.registro-solicitado]].
2. O órgão devolve o resultado; o sistema classifica ([[USA:@rn.retorno-registro]]). Emite [[EMITE:@evt.garantia-registrada]].

**Fluxos alternativos:** exigência → emite [[EMITE:@evt.exigencia]]; recusa → emite [[EMITE:@evt.registro-recusado]].
`);
uc('registro-recusado', 'gar.ucs', 'Tratar registro recusado', `
**Ator:** [[@a.analista]].

**Fluxo principal:** o analista corrige o motivo da recusa e reenvia; o sistema volta a [[INCLUI:@uc.registrar]].

**Fluxo alternativo:** sem solução → [[INCLUI:@uc.cancelar-contrato]].
`);
uc('exigencia', 'gar.ucs', 'Cumprir exigência', `
**Ator:** [[@a.analista]].

**Fluxo principal:** o analista providencia o que o cartório pediu e reenvia. Emite [[EMITE:@evt.exigencia-cumprida]].
`);

// ----- Desembolso -----------------------------------------------------------

dominio('des', 'Desembolso', 'Suporte', 'Libera o dinheiro ao vendedor — de uma vez, ou por medição no imóvel na planta — e o FGTS da entrada.');
visoes['des'] = `
## Propósito e fronteiras

Paga o [[@a.vendedor]] quando a garantia está constituída: [[@t.liberacao|liberação]] única no bem pronto, ou por
[[@t.medicao|medição]] no imóvel na planta. Pede ao [[@a.fgts]] a liberação do FGTS da entrada.

**Depende de:** [[DEPENDE_DE:@gar|Garantia]], [[DEPENDE_DE:@ava|Avaliação]] (medições) e [[DEPENDE_DE:@a.fgts]].

## Jornada em eventos

Gerada pelo episteme (event storming): eventos em ordem, quem os provoca e o que disparam.

\`\`\`event-storming
\`\`\`
`;
it('des.eventos', 'des', 'SECAO', 'Eventos', 'Fatos do domínio de desembolso.');
evt('liberacao-solicitada', 'des.eventos', 'LiberacaoSolicitada', 'A liberação do valor financiado (ou de uma parcela da obra) foi pedida.');
evt('recurso-liberado', 'des.eventos', 'RecursoLiberado', 'O vendedor recebeu o valor financiado. O contrato está formalizado.');
evt('parcela-obra', 'des.eventos', 'ParcelaDaObraLiberada', 'A incorporadora recebeu a parcela correspondente à última medição.');
evt('fgts-solicitado', 'des.eventos', 'FgtsSolicitado', 'O uso do FGTS na entrada foi pedido ao [[@a.fgts]].');
evt('fgts-liberado', 'des.eventos', 'FgtsLiberado', 'O FGTS da entrada foi liberado ao vendedor.');
evt('fgts-negado', 'des.eventos', 'FgtsNegado', 'O sistema do FGTS negou o uso. Dispara [[DISPARA:@uc.recompor-entrada]].');
it('des.regras', 'des', 'DOCUMENTO', 'Regras de Negócio', 'Regras do desembolso.');
rn('sem-garantia-sem-dinheiro', 'des.regras', 'Nenhuma liberação sem garantia confirmada',
  'Nenhum valor é liberado antes de a garantia estar registrada e confirmada pelo órgão, em todos os tipos de bem. Decisão: [[@d8]].');
rn('liberacao-acumulada', 'des.regras', 'Soma das liberações',
  'A soma das liberações nunca passa do valor financiado. Cada liberação é paga uma única vez, ao vendedor da proposta.');
rn('liberacao-medicao', 'des.regras', 'Liberação por medição',
  'No imóvel na planta, cada parcela = valor financiado × (percentual medido acumulado − percentual já liberado) ([[@rn.medicao]]). Decisão: [[@d6]].');
it('des.ucs', 'des', 'SECAO', 'Casos de uso', 'Casos de uso do desembolso.');
uc('liberar', 'des.ucs', 'Liberar recurso', `
**Atores:** [[@a.vendedor]] (favorecido) e [[@a.fgts]] (quando a entrada usa FGTS).

**Pré-condição:** garantia constituída ([[USA:@rn.sem-garantia-sem-dinheiro]]).

**Fluxo principal**
1. O sistema pede a liberação. Emite [[EMITE:@evt.liberacao-solicitada]].
2. Se a entrada usa FGTS, o sistema pede ao sistema do FGTS ([[USA:@rn.fgts]]). Emite [[EMITE:@evt.fgts-solicitado]] e, com a liberação confirmada, [[EMITE:@evt.fgts-liberado]].
3. O sistema paga o vendedor ([[USA:@rn.liberacao-acumulada]]). Emite [[EMITE:@evt.recurso-liberado]].

**Fluxo alternativo:** FGTS negado → emite [[EMITE:@evt.fgts-negado]] e a liberação espera a entrada recomposta.
`);
uc('recompor-entrada', 'des.ucs', 'Recompor entrada', `
**Ator:** [[@a.proponente]].

**Fluxo principal:** com o FGTS negado, o proponente paga com recurso próprio a parte da entrada que viria do FGTS ([[USA:@rn.valor-financiado]]); a liberação segue em [[INCLUI:@uc.liberar]].
`);
uc('liberar-parcela', 'des.ucs', 'Liberar parcela da obra', `
**Ator:** [[@a.vendedor]] (incorporadora).

**Pré-condição:** imóvel na planta com garantia constituída e nova medição.

**Fluxo principal:** o sistema calcula a parcela ([[USA:@rn.liberacao-medicao]], [[USA:@rn.liberacao-acumulada]]) e paga a incorporadora. Emite [[EMITE:@evt.parcela-obra]].
`);

// ----- Genéricos ------------------------------------------------------------

dominio('pes', 'Cadastro de pessoas', 'Genérico', 'CPF, CNPJ, endereço e renda dos participantes e do vendedor. Pode vir de um cadastro corporativo.');
visoes['pes'] = `
## Propósito e fronteiras

Mantém os dados cadastrais de [[@t.pf|pessoas físicas]] e [[@t.pj|jurídicas]] usados pela Originação. Não decide nada
sobre a proposta.
`;
dominio('doc', 'Documentos', 'Genérico', 'Recebimento, guarda e conferência dos arquivos exigidos.');
it('pes.regras', 'pes', 'DOCUMENTO', 'Regras de Negócio', 'Regras do cadastro de pessoas.');
rn('documento-valido', 'pes.regras', 'CPF ou CNPJ válido',
  'Todo participante e todo vendedor tem CPF ([[@t.pf|pessoa física]]) ou CNPJ ([[@t.pj|pessoa jurídica]]) válido e regular na Receita Federal. Os dados são tratados só para o financiamento ([[@f.lgpd]]).');
visoes['doc'] = `
## Propósito e fronteiras

Recebe e guarda os documentos dos participantes e do bem e registra a conferência (aceito, recusado, ilegível).
Quais documentos são exigidos é regra da Originação, não deste domínio.

## Jornada em eventos

\`\`\`event-storming
\`\`\`
`;
it('doc.eventos', 'doc', 'SECAO', 'Eventos', 'Fatos do domínio de documentos.');
evt('documento-recusado', 'doc.eventos', 'DocumentoRecusado', 'Um documento foi recusado na conferência (ilegível, vencido ou divergente). Dispara [[DISPARA:@evt.pendencia]] na proposta.');
evt('documento-aceito', 'doc.eventos', 'DocumentoAceito', 'Um documento foi conferido e aceito. Dispara [[DISPARA:@uc.decidir]].');
it('doc.ucs', 'doc', 'SECAO', 'Casos de uso', 'Casos de uso de documentos.');
uc('conferir-documento', 'doc.ucs', 'Conferir documento', `
**Ator:** [[@a.analista]].

**Pré-condição:** proposta submetida. A conferência acontece depois da [[@t.submissao|submissão]].

**Fluxo principal:** o analista confere o documento enviado contra o que é exigido ([[USA:@rn.documentos]]) e o aceita. Emite [[EMITE:@evt.documento-aceito]].

**Fluxo alternativo:** ilegível, vencido ou divergente → recusa. Emite [[EMITE:@evt.documento-recusado]]. O proponente reenvia o documento com a proposta com [[@t.pendencia|pendência]], que então é resolvida.
`);

// ===== Vida do contrato (entrevista de 2026-11-07, em revisão) ==============
// Tudo desta etapa entra no FIM da lista: os códigos dos artefatos saem da ordem de criação, e os já aprovados
// (citados fora do modelo) não podem mudar.

situacaoAtual = 'EM_REVISAO';

ator('financeiro', 'Sistema financeiro', 'Sistema externo que faz as contas do contrato: saldo devedor, valor de quitação válido até uma data (com o desconto proporcional dos juros), efeito de uma amortização ou de uma renegociação, dias em atraso e a liquidação no fim do prazo.');
ator('cobranca', 'Sistema de cobrança', 'Sistema externo (banco ou arrecadador) que emite a cobrança (boleto, débito ou Pix) e informa o pagamento.');

it('glo.carteira', 'glo', 'SECAO', 'Vida do contrato', 'O contrato depois de formalizado: quitação e renegociação.');
termo('saldo-devedor', 'carteira', 'Saldo devedor', 'Quanto o devedor ainda deve numa data. Calculado pelo sistema financeiro.');
termo('quitacao', 'carteira', 'Quitação', 'Pagamento de toda a dívida: no fim do prazo, com a última parcela, ou antecipada, pelo valor de quitação. Quitado, o contrato tem o termo de quitação e a garantia baixada.');
termo('valor-quitacao', 'carteira', 'Valor de quitação', 'Quanto o devedor paga para quitar antes do fim do prazo: o saldo devedor com o desconto proporcional dos juros. Vale até uma data.');
termo('amortizacao', 'carteira', 'Amortização', 'Pagamento antecipado de parte da dívida. O devedor escolhe reduzir o prazo ou reduzir a parcela; o contrato ganha nova versão das condições.');
termo('termo-quitacao', 'carteira', 'Termo de quitação', 'Documento do credor que declara a dívida paga. É o que permite baixar a garantia.');
termo('baixa', 'carteira', 'Baixa da garantia', 'Cancelamento da alienação fiduciária: averbação do cancelamento na matrícula (imóvel e terreno) ou baixa do gravame (veículo). O bem passa a ser só do devedor.');
termo('renegociacao', 'carteira', 'Renegociação', 'Mudança das condições de um contrato formalizado a pedido do devedor, em dia ou em atraso: novo prazo e nova parcela, reenquadrados e analisados como na proposta.');
termo('aditivo', 'carteira', 'Aditivo', 'Instrumento que altera o contrato com as condições renegociadas. Assinado pelos mesmos signatários do contrato.');
termo('averbacao', 'carteira', 'Averbação do aditivo', 'Anotação do aditivo na matrícula (imóvel e terreno) ou atualização do gravame (veículo), para a garantia refletir as novas condições.');
termo('inadimplencia', 'carteira', 'Inadimplência', 'Atraso de pagamento acima do limite (referência: 90 dias). Nesta etapa só é informado pelo sistema financeiro; não impede a renegociação.');

fonte('cdc', 'Código de Defesa do Consumidor, art. 52, § 2º', 'Garante ao consumidor a liquidação antecipada do débito, total ou parcial, com redução proporcional dos juros e demais acréscimos.');

q(18, 'O que entra na vida do contrato nesta etapa?', 'Cobrança das parcelas, atraso, quitação, renegociação, retomada da garantia.');
q(19, 'Quem calcula saldo devedor, valor de quitação e efeito de amortização e renegociação?', 'O cálculo financeiro está fora do escopo do modelo.');
q(20, 'Quem emite a cobrança e informa os pagamentos?', 'Boleto, débito ou Pix, e a confirmação do pagamento.');
q(21, 'Quando o atraso vira inadimplência?', 'Número de dias e o que muda para o contrato.');
q(22, 'Na amortização, o que muda no contrato?', 'Reduz o prazo, reduz a parcela, ou o devedor escolhe.');
q(23, 'O FGTS pode amortizar ou quitar o imóvel?', 'A lei do FGTS permite no imóvel residencial, com intervalo mínimo entre usos.');
q(24, 'Renegociação: quem pede, quando, e passa por nova análise?', 'Só em atraso ou a qualquer momento; reenquadra; consulta o bureau; quem decide.');
q(25, 'Como a renegociação é formalizada?', 'Aditivo assinado; averbação no cartório ou atualização do gravame.');

d(18, [18], 'Nesta etapa: quitação com baixa da garantia, e renegociação', 'Cobrança das parcelas, atraso e retomada da garantia ficam para outra etapa. A quitação vale antecipada (total ou parcial) e no fim do prazo.');
d(19, [19], 'Um sistema financeiro externo faz as contas', 'O [[@a.financeiro]] informa [[@t.saldo-devedor|saldo devedor]], [[@t.valor-quitacao|valor de quitação]] e o efeito de amortização e renegociação. O financiamento decide e conduz; o cálculo continua fora do escopo.');
d(20, [20], 'Um sistema de cobrança externo cobra e informa o pagamento', 'Nesta etapa, o [[@a.cobranca]] cobra só a quitação e a amortização.');
d(21, [21], 'Inadimplente com 90 dias de atraso', 'Valor de referência, parametrizável. Os dias em atraso vêm do sistema financeiro. Nesta etapa a inadimplência só informa: o contrato inadimplente também pode ser renegociado.');
d(22, [22], 'Na amortização, o devedor escolhe reduzir o prazo ou a parcela', 'O sistema financeiro calcula o efeito, e o contrato ganha nova versão das condições.');
d(23, [23], 'FGTS amortiza ou quita o imóvel residencial de pessoa física', 'Pedido ao [[@a.fgts]], como na entrada, com intervalo mínimo de 2 anos entre usos ([[@f.8036]]).');
d(24, [24], 'O devedor pede a renegociação; reenquadra, passa no bureau e o analista decide', 'Em dia ou em atraso, inadimplente incluído. As novas condições respeitam as regras do tipo de bem (prazo, idade do proponente, idade do veículo, comprometimento de renda), a análise de crédito é refeita, e o [[@a.analista]] decide.');
d(25, [25], 'Renegociação por aditivo assinado e averbado', 'Todos os signatários do contrato assinam o [[@t.aditivo|aditivo]]. No imóvel e no terreno ele é averbado no cartório; no veículo, o gravame é atualizado.');

// ----- Carteira de contratos ------------------------------------------------

dominio('car', 'Carteira de contratos', 'Suporte', 'Acompanha o contrato formalizado até a quitação: quitação antecipada ou no fim do prazo, amortização e renegociação.');
visoes['car'] = `
## Propósito e fronteiras

Cuida do [[@t.contrato|contrato]] depois de formalizado: [[@t.quitacao|quitação]] (total ou parcial, antecipada ou no
fim do prazo) e [[@t.renegociacao|renegociação]]. Não faz contas: o [[@a.financeiro]] calcula, o [[@a.cobranca]] cobra.
Quitado, pede à Garantia a baixa; renegociado, a averbação do aditivo.

**Depende de:** [[DEPENDE_DE:@con|Contratação]], [[DEPENDE_DE:@gar|Garantia]], [[DEPENDE_DE:@cre|Crédito]] (renegociação),
[[DEPENDE_DE:@ori|Originação]] (regras de enquadramento), [[DEPENDE_DE:@a.financeiro]], [[DEPENDE_DE:@a.cobranca]] e [[DEPENDE_DE:@a.fgts]].

**Guarda:** os agregados [[@car.agr.quitacao]] e [[@car.agr.renegociacao]], com o [[@car.cv.quitacao]] e o [[@car.cv.renegociacao]].

**Atores:** [[@a.proponente]], [[@a.coobrigado]], [[@a.analista]], [[@a.financeiro]], [[@a.cobranca]], [[@a.fgts]].

## Jornada em eventos

Gerada pelo episteme (event storming): eventos em ordem, quem os provoca e o que disparam.

\`\`\`event-storming
\`\`\`
`;
it('car.agr.quitacao', 'car', 'AGREGADO', 'Quitação', `
Fronteira de consistência de um pedido de quitação ou amortização de um [[@con.agr.contrato|contrato]]: o que foi pedido,
o valor calculado e a sua validade, a cobrança e o pagamento. Referencia o contrato pelo identificador.

## Composição

| Elemento | Natureza | Observação |
|---|---|---|
| Tipo | objeto de valor | total, ou parcial com efeito em prazo ou em parcela |
| Recurso | objeto de valor | próprio, ou FGTS no imóvel residencial de pessoa física |
| Valor calculado | objeto de valor | valor e data de validade, do sistema financeiro |
| Cobrança | entidade | uma por pedido, do sistema de cobrança |

## Invariantes

1. Só para contrato formalizado, sem outra quitação ou renegociação em andamento ([[USA:@rn.uma-operacao]]).
2. O valor vale até a data informada; vencido, o pedido acaba sem efeito ([[USA:@rn.validade-quitacao]]).
3. O FGTS só no imóvel residencial de pessoa física, respeitado o intervalo entre usos ([[USA:@rn.fgts-amortizacao]]).
4. Pago, aplica-se uma única vez: quita o contrato (total) ou gera a nova versão das condições (parcial, [[USA:@rn.amortizacao]]).

**Emite:** [[EMITE:@evt.quitacao-solicitada]], [[EMITE:@evt.quitacao-calculada]], [[EMITE:@evt.cobranca-emitida]], [[EMITE:@evt.fgts-quitacao-solicitado]], [[EMITE:@evt.fgts-quitacao-negado]], [[EMITE:@evt.quitacao-paga]], [[EMITE:@evt.quitacao-vencida]], [[EMITE:@evt.quitacao-cancelada]], [[EMITE:@evt.quitacao-concluida]].
`);
it('car.cv.quitacao', 'car', 'CICLO_DE_VIDA', 'Ciclo de vida da quitação', `
Estados de um pedido de [[@car.agr.quitacao|quitação]] (total ou parcial).

\`\`\`mermaid
stateDiagram-v2
    [*] --> Solicitada: QuitacaoSolicitada
    Solicitada --> Calculada: QuitacaoCalculada
    Solicitada --> Cancelada: QuitacaoCancelada
    Calculada --> Cobrada: CobrancaEmitida (recurso próprio)
    Calculada --> AguardandoFgts: FgtsDaQuitacaoSolicitado (FGTS)
    Calculada --> Vencida: QuitacaoVencida
    Calculada --> Cancelada: QuitacaoCancelada
    Cobrada --> Paga: QuitacaoPaga
    Cobrada --> Vencida: QuitacaoVencida
    AguardandoFgts --> Paga: QuitacaoPaga
    AguardandoFgts --> Cancelada: FgtsDaQuitacaoNegado
    Paga --> Concluida: QuitacaoConcluida
    Concluida --> [*]
    Vencida --> [*]
    Cancelada --> [*]
\`\`\`

| Transição | Evento | Regra |
|---|---|---|
| Pedido | [[@evt.quitacao-solicitada]] | [[@rn.uma-operacao]], [[@rn.quitacao-antecipada]] |
| Cálculo | [[@evt.quitacao-calculada]] | [[@rn.validade-quitacao]] |
| FGTS | [[@evt.fgts-quitacao-solicitado]], [[@evt.fgts-quitacao-negado]] | [[@rn.fgts-amortizacao]] |
| Pagamento | [[@evt.quitacao-paga]] | — |
| Conclusão | [[@evt.quitacao-concluida]] | [[@rn.amortizacao]] |
| Vencimento | [[@evt.quitacao-vencida]] | [[@rn.validade-quitacao]] |
`);
it('car.agr.renegociacao', 'car', 'AGREGADO', 'Renegociação', `
Fronteira de consistência de uma [[@t.renegociacao|renegociação]]: as condições pedidas, as calculadas pelo sistema
financeiro, o reenquadramento, a decisão e o [[@t.aditivo|aditivo]] com as suas assinaturas. Referencia o contrato pelo identificador.

## Composição

| Elemento | Natureza | Observação |
|---|---|---|
| Condições pedidas | objeto de valor | novo prazo |
| Condições calculadas | objeto de valor | novo saldo e nova parcela, com as parcelas em atraso incorporadas |
| Decisão | objeto de valor | aprovada ou reprovada, com o motivo |
| Aditivo | entidade | as novas condições e uma assinatura por signatário |

## Invariantes

1. Só para contrato formalizado, sem outra quitação ou renegociação em andamento ([[USA:@rn.uma-operacao]]).
2. As condições calculadas respeitam as regras do tipo de bem ([[USA:@rn.reenquadramento]]).
3. O aditivo só vale depois de assinado por todos e averbado ([[USA:@rn.aditivo]]).

**Emite:** [[EMITE:@evt.renegociacao-solicitada]], [[EMITE:@evt.renegociacao-simulada]], [[EMITE:@evt.renegociacao-aprovada]], [[EMITE:@evt.renegociacao-reprovada]], [[EMITE:@evt.aditivo-emitido]], [[EMITE:@evt.aditivo-assinado]], [[EMITE:@evt.renegociacao-cancelada]].
`);
it('car.cv.renegociacao', 'car', 'CICLO_DE_VIDA', 'Ciclo de vida da renegociação', `
Estados de uma [[@car.agr.renegociacao|renegociação]].

\`\`\`mermaid
stateDiagram-v2
    [*] --> Solicitada: RenegociacaoSolicitada
    Solicitada --> Simulada: RenegociacaoSimulada
    Solicitada --> Cancelada: RenegociacaoCancelada
    Simulada --> EmAnalise: AnaliseCreditoSolicitada
    Simulada --> Reprovada: RenegociacaoReprovada (fora do enquadramento)
    Simulada --> Cancelada: RenegociacaoCancelada
    EmAnalise --> Aprovada: RenegociacaoAprovada
    EmAnalise --> Reprovada: RenegociacaoReprovada
    Aprovada --> AditivoEmitido: AditivoEmitido
    AditivoEmitido --> AditivoAssinado: AditivoAssinado
    AditivoEmitido --> Cancelada: RenegociacaoCancelada
    AditivoAssinado --> EmAverbacao: AverbacaoSolicitada
    EmAverbacao --> Vigente: AditivoAverbado
    Vigente --> [*]
    Reprovada --> [*]
    Cancelada --> [*]
\`\`\`

| Transição | Evento | Regra |
|---|---|---|
| Pedido | [[@evt.renegociacao-solicitada]] | [[@rn.uma-operacao]] |
| Cálculo e reenquadramento | [[@evt.renegociacao-simulada]] | [[@rn.reenquadramento]] |
| Decisão | [[@evt.renegociacao-aprovada]], [[@evt.renegociacao-reprovada]] | [[@rn.reenquadramento]] |
| Aditivo | [[@evt.aditivo-emitido]], [[@evt.aditivo-assinado]] | [[@rn.aditivo]] |
| Averbação | [[@evt.averbacao-solicitada]], [[@evt.aditivo-averbado]] | [[@rn.aditivo]], [[@rn.orgao]] |
`);

it('car.eventos', 'car', 'SECAO', 'Eventos', 'Fatos do domínio da carteira de contratos.');
evt('quitacao-solicitada', 'car.eventos', 'QuitacaoSolicitada', 'O [[@a.proponente]] pediu a quitação ou a amortização do contrato: tipo, efeito e recurso.');
evt('quitacao-calculada', 'car.eventos', 'QuitacaoCalculada', 'O [[@a.financeiro]] informou o valor e até quando ele vale ([[@rn.validade-quitacao]]).');
evt('cobranca-emitida', 'car.eventos', 'CobrancaEmitida', 'O [[@a.cobranca]] emitiu a cobrança do valor calculado.');
evt('fgts-quitacao-solicitado', 'car.eventos', 'FgtsDaQuitacaoSolicitado', 'O uso do FGTS na quitação ou amortização foi pedido ao [[@a.fgts]] ([[@rn.fgts-amortizacao]]).');
evt('fgts-quitacao-negado', 'car.eventos', 'FgtsDaQuitacaoNegado', 'O [[@a.fgts]] negou o uso. O pedido acaba; o devedor pode pedir de novo com recurso próprio.');
evt('quitacao-paga', 'car.eventos', 'QuitacaoPaga', 'O valor foi pago, pela cobrança ou pelo FGTS.');
evt('quitacao-vencida', 'car.eventos', 'QuitacaoVencida', 'Passou a validade do valor sem pagamento. O pedido acaba sem efeito.');
evt('quitacao-cancelada', 'car.eventos', 'QuitacaoCancelada', 'O devedor desistiu do pedido antes de pagar.');
evt('quitacao-concluida', 'car.eventos', 'QuitacaoConcluida', 'O pagamento foi aplicado ao contrato. Total: dispara [[DISPARA:@evt.contrato-quitado]]. Parcial: dispara [[DISPARA:@evt.condicoes-alteradas]].');
evt('renegociacao-solicitada', 'car.eventos', 'RenegociacaoSolicitada', 'O [[@a.proponente]] pediu a renegociação do contrato, com o novo prazo.');
evt('renegociacao-simulada', 'car.eventos', 'RenegociacaoSimulada', 'O [[@a.financeiro]] calculou o novo saldo e a nova parcela, com as parcelas em atraso incorporadas. Enquadrada, dispara [[DISPARA:@uc.analisar]].');
evt('renegociacao-aprovada', 'car.eventos', 'RenegociacaoAprovada', 'O [[@a.analista]] aprovou a renegociação, com o crédito analisado.');
evt('renegociacao-reprovada', 'car.eventos', 'RenegociacaoReprovada', 'A renegociação foi reprovada: fora do enquadramento ou pelo analista.');
evt('aditivo-emitido', 'car.eventos', 'AditivoEmitido', 'O aditivo com as condições renegociadas foi emitido para assinatura.');
evt('aditivo-assinado', 'car.eventos', 'AditivoAssinado', 'Todos os signatários assinaram o aditivo. Dispara [[DISPARA:@uc.averbar]].');
evt('renegociacao-cancelada', 'car.eventos', 'RenegociacaoCancelada', 'O devedor desistiu da renegociação antes de assinar.');

evt('contrato-quitado', 'con.eventos', 'ContratoQuitado', 'A dívida foi toda paga: quitação antecipada concluída ou liquidação no fim do prazo. Dispara [[DISPARA:@uc.baixar]].');
evt('condicoes-alteradas', 'con.eventos', 'CondicoesDoContratoAlteradas', 'O contrato formalizado ganhou nova versão das condições: amortização aplicada ou aditivo vigente.');
evt('termo-quitacao', 'con.eventos', 'TermoDeQuitacaoEmitido', 'O credor emitiu o termo de quitação do contrato ([[@rn.termo-quitacao]]).');

evt('baixa-solicitada', 'gar.eventos', 'BaixaSolicitada', 'O pedido de baixa da garantia do contrato quitado foi enviado ao órgão de registro, com um protocolo nosso.');
evt('garantia-baixada', 'gar.eventos', 'GarantiaBaixada', 'O órgão confirmou a baixa. O contrato está encerrado.');
evt('averbacao-solicitada', 'gar.eventos', 'AverbacaoSolicitada', 'O aditivo assinado foi enviado ao órgão de registro, com um protocolo nosso.');
evt('aditivo-averbado', 'gar.eventos', 'AditivoAverbado', 'O órgão averbou o aditivo (ou atualizou o gravame). O aditivo está vigente: dispara [[DISPARA:@evt.condicoes-alteradas]].');

it('car.regras', 'car', 'DOCUMENTO', 'Regras de Negócio', 'Regras da carteira de contratos.');
rn('uma-operacao', 'car.regras', 'Uma quitação ou renegociação por vez, só no contrato formalizado',
  'Quitação, amortização e renegociação só existem para o contrato formalizado (bem pronto liberado ou obra concluída). Enquanto uma está em andamento, outra não começa. Decisão: [[@d18]].');
rn('quitacao-antecipada', 'car.regras', 'Quitação antecipada com desconto dos juros',
  'O devedor pode quitar ou amortizar antes do prazo, a qualquer tempo, com a redução proporcional dos juros e demais acréscimos ([[@f.cdc]]). O valor é calculado pelo [[@a.financeiro]]. Decisões: [[@d19]], [[@d22]].');
rn('validade-quitacao', 'car.regras', 'Validade do valor de quitação',
  'O [[@t.valor-quitacao|valor de quitação]] (ou de amortização) vale até a data informada pelo sistema financeiro. Sem pagamento até lá, o pedido vence sem efeito, e um novo pedido recalcula.',
  'Valor de R$ 12.300,00 válido até 15/03. Pago em 16/03 → o pedido já venceu; o devedor pede de novo e recebe um novo valor.');
rn('amortizacao', 'car.regras', 'Efeito da amortização',
  'Na [[@t.amortizacao|amortização]], o devedor escolhe reduzir o prazo (a parcela fica igual) ou reduzir a parcela (o prazo fica igual). O contrato ganha nova versão das condições, calculada pelo sistema financeiro. Decisão: [[@d22]].');
rn('fgts-amortizacao', 'car.regras', 'FGTS na quitação e na amortização',
  'Só no imóvel residencial de [[@t.pf|pessoa física]], com o FGTS de um participante do contrato e no mínimo 2 anos desde o último uso do FGTS no mesmo contrato (inclusive na entrada). Decide o [[@a.fgts]] ([[@f.8036]]). Decisão: [[@d23]].',
  'Entrada com FGTS em 2026; amortização com FGTS pedida em 2027 → recusada (menos de 2 anos). Em 2028 → pedida ao sistema do FGTS.');
rn('reenquadramento', 'car.regras', 'Renegociação reenquadrada',
  'As condições renegociadas respeitam as regras do tipo de bem como na proposta: [[INCLUI:@rn.prazo]], [[INCLUI:@rn.idade-proponente]], [[INCLUI:@rn.idade-veiculo]] e [[INCLUI:@rn.comprometimento]]. Fora delas, a renegociação é reprovada sem ir ao bureau. Decisão: [[@d24]].');
rn('aditivo', 'car.regras', 'Aditivo assinado e averbado',
  'O [[@t.aditivo|aditivo]] é assinado pelos mesmos signatários do contrato ([[@rn.assinaturas]]) e só vale depois de averbado no órgão da categoria ([[@rn.orgao]]): averbação na matrícula (imóvel e terreno) ou atualização do gravame (veículo). Decisão: [[@d25]].');
rn('termo-quitacao', 'car.regras', 'Termo de quitação e baixa da garantia',
  'Quitado o contrato, o credor emite o [[@t.termo-quitacao|termo de quitação]] em até 30 dias ([[@f.9514]], art. 25) e pede a [[@t.baixa|baixa da garantia]] ao órgão da categoria ([[@rn.orgao]]). O retorno do órgão é fato, como no registro ([[@rn.retorno-registro]]).');
rn('inadimplencia', 'car.regras', 'Inadimplência',
  'O contrato com 90 dias ou mais de atraso, informados pelo [[@a.financeiro]], está [[@t.inadimplencia|inadimplente]] (valor de referência). Nesta etapa isso só informa: o inadimplente também pode renegociar. Decisão: [[@d21]].');

it('car.ucs', 'car', 'SECAO', 'Casos de uso', 'Casos de uso da carteira de contratos.');
uc('pedir-quitacao', 'car.ucs', 'Pedir quitação ou amortização', `
**Atores:** [[@a.proponente]], [[@a.financeiro]].

**Pré-condição:** contrato formalizado, sem outra operação em andamento ([[USA:@rn.uma-operacao]]).

**Fluxo principal**
1. O proponente escolhe: quitação total, ou amortização de um valor com efeito em prazo ou em parcela ([[USA:@rn.amortizacao]]); e o recurso, próprio ou FGTS ([[USA:@rn.fgts-amortizacao]]). Emite [[EMITE:@evt.quitacao-solicitada]].
2. O sistema financeiro calcula o valor, com o desconto dos juros, e a validade ([[USA:@rn.quitacao-antecipada]], [[USA:@rn.validade-quitacao]]). Emite [[EMITE:@evt.quitacao-calculada]].
3. Segue em [[INCLUI:@uc.pagar-quitacao]].

**Fluxo alternativo:** o proponente desiste antes de pagar. Emite [[EMITE:@evt.quitacao-cancelada]].
`);
uc('pagar-quitacao', 'car.ucs', 'Pagar quitação ou amortização', `
**Atores:** [[@a.cobranca]] (recurso próprio) ou [[@a.fgts]].

**Fluxo principal**
1. Recurso próprio: o sistema de cobrança emite a cobrança ([[EMITE:@evt.cobranca-emitida]]) e informa o pagamento. Emite [[EMITE:@evt.quitacao-paga]].
2. O sistema aplica o pagamento. Emite [[EMITE:@evt.quitacao-concluida]]: o contrato é quitado (total) ou ganha nova versão das condições (parcial).

**Fluxos alternativos**
- FGTS: o pedido vai ao sistema do FGTS ([[EMITE:@evt.fgts-quitacao-solicitado]]); liberado, segue como pago; negado, o pedido acaba ([[EMITE:@evt.fgts-quitacao-negado]]).
- Sem pagamento até a validade: o pedido vence ([[EMITE:@evt.quitacao-vencida]]).
`);
uc('quitar-fim-prazo', 'car.ucs', 'Quitar no fim do prazo', `
**Ator:** [[@a.financeiro]].

**Fluxo principal:** o sistema financeiro informa que a última parcela foi paga e a dívida está liquidada. O contrato é quitado: emite [[EMITE:@evt.contrato-quitado]], e a garantia é baixada em [[INCLUI:@uc.baixar]].
`);
uc('pedir-renegociacao', 'car.ucs', 'Pedir renegociação', `
**Atores:** [[@a.proponente]], [[@a.financeiro]]. Decisão: [[@d24]].

**Pré-condição:** contrato formalizado, em dia ou em atraso ([[USA:@rn.inadimplencia]]), sem outra operação em andamento ([[USA:@rn.uma-operacao]]).

**Fluxo principal**
1. O proponente pede um novo prazo. Emite [[EMITE:@evt.renegociacao-solicitada]].
2. O sistema financeiro calcula o novo saldo e a nova parcela. Emite [[EMITE:@evt.renegociacao-simulada]].
3. O sistema reenquadra as novas condições ([[USA:@rn.reenquadramento]]) e pede a análise de crédito ([[INCLUI:@uc.analisar]]).

**Fluxo alternativo:** fora do enquadramento → reprovada, sem ir ao bureau. Emite [[EMITE:@evt.renegociacao-reprovada]].
`);
uc('decidir-renegociacao', 'car.ucs', 'Decidir renegociação', `
**Ator:** [[@a.analista]].

**Pré-condição:** renegociação enquadrada, com a análise de crédito concluída.

**Fluxo principal:** o analista aprova, com o resultado do crédito à vista. Emite [[EMITE:@evt.renegociacao-aprovada]] e o aditivo é emitido para assinatura ([[EMITE:@evt.aditivo-emitido]]).

**Fluxo alternativo:** o analista reprova, com o motivo. Emite [[EMITE:@evt.renegociacao-reprovada]].
`);
uc('assinar-aditivo', 'car.ucs', 'Assinar aditivo', `
**Atores:** [[@a.proponente]], [[@a.coobrigado]], [[@a.vendedor]] (imóvel e terreno).

**Fluxo principal:** cada signatário do contrato assina o aditivo ([[USA:@rn.aditivo]]); com a última assinatura, emite [[EMITE:@evt.aditivo-assinado]].

**Fluxo alternativo:** o proponente desiste antes de assinar. Emite [[EMITE:@evt.renegociacao-cancelada]].
`);

uc('baixar', 'gar.ucs', 'Baixar garantia', `
**Atores:** [[@a.cartorio]] ou [[@a.transito]].

**Pré-condição:** contrato quitado.

**Fluxo principal**
1. O credor emite o termo de quitação ([[USA:@rn.termo-quitacao]]). Emite [[EMITE:@evt.termo-quitacao]].
2. O sistema pede a baixa ao órgão da categoria ([[USA:@rn.orgao]]). Emite [[EMITE:@evt.baixa-solicitada]].
3. O órgão confirma; o sistema classifica o retorno ([[USA:@rn.retorno-registro]]). Emite [[EMITE:@evt.garantia-baixada]]: o contrato está encerrado.

**Fluxo alternativo:** exigência → [[INCLUI:@uc.exigencia]].
`);
uc('averbar', 'gar.ucs', 'Averbar aditivo', `
**Atores:** [[@a.cartorio]] ou [[@a.transito]].

**Pré-condição:** aditivo assinado por todos.

**Fluxo principal**
1. O sistema envia o aditivo ao órgão da categoria ([[USA:@rn.aditivo]], [[USA:@rn.orgao]]). Emite [[EMITE:@evt.averbacao-solicitada]].
2. O órgão averba (ou atualiza o gravame). Emite [[EMITE:@evt.aditivo-averbado]]: o aditivo está vigente e o contrato ganha nova versão das condições.

**Fluxo alternativo:** exigência → [[INCLUI:@uc.exigencia]].
`);

situacaoAtual = SITUACAO;

// ---------------------------------------------------------------------------
// Carga
// ---------------------------------------------------------------------------

async function main() {
  const pkg = JSON.parse(fs.readFileSync(path.join(EPISTEME, 'package.json'), 'utf-8'));
  if (pkg.name !== 'episteme') {
    throw new Error(`Rode de dentro de um checkout do episteme (cwd atual: ${EPISTEME}).`);
  }
  const requireDoEpisteme = createRequire(path.join(EPISTEME, 'package.json'));
  const importar = (rel: string) => import(pathToFileURL(path.join(EPISTEME, rel)).href);

  const { createClient } = requireDoEpisteme('@libsql/client');
  const { migrate } = await importar('server/db/migrate.ts');
  const { createNode, updateNode } = await importar('server/services/nodes.ts');
  const { compileNode } = await importar('server/services/compiler.ts');
  const { verificarInvariantes } = await importar('server/verify/invariants.ts');
  const { citacoesParaLinks } = await importar('src/lib/documento.ts');

  const arquivoDb = path.join(AQUI, 'financiamento.db');
  fs.rmSync(arquivoDb, { force: true });
  const db = createClient({ url: 'file:' + arquivoDb });
  await migrate(db);

  const refs = new Map<string, Ref>();
  const resolver = (texto: string) =>
    texto.replace(/\[\[([^\]]*?)@([a-z0-9_.-]+)/g, (_m, antes: string, chave: string) => {
      const ref = refs.get(chave);
      if (!ref) throw new Error(`Chave citada não existe: @${chave}`);
      return `[[${antes}${ref.codigo}`;
    });

  // 1ª passada: cria tudo com uma descrição provisória (as citações ainda não resolvem).
  for (const item of itens) {
    const paiId = item.pai === null ? null : refs.get(item.pai)?.id;
    if (item.pai !== null && paiId === undefined) throw new Error(`Pai ${item.pai} não criado antes de ${item.chave}`);
    const r = await createNode(db, {
      pai_id: paiId ?? null, tipo: item.tipo, titulo: item.titulo, texto: 'Em elaboração.',
      classificacao: item.classificacao
    });
    if (r.error) throw new Error(`${item.chave}: ${r.error}`);
    refs.set(item.chave, { id: r.id, codigo: r.codigo });
    if (item.tipo === 'DOMINIO') {
      const { rows } = await db.execute({ sql: "SELECT id, codigo FROM no WHERE pai_id = ? AND tipo = 'VISAO'", args: [r.id] });
      refs.set(`${item.chave}.vis`, { id: rows[0].id as number, codigo: rows[0].codigo as string });
    }
  }

  // 2ª passada: textos com as citações resolvidas, e a situação final.
  const avisos: string[] = [];
  const escrever = async (chave: string, texto: string, exemplo: string | undefined, situacao: Situacao | undefined) => {
    const ref = refs.get(chave)!;
    const r = await updateNode(db, ref.id, 1, {
      texto: resolver(texto.trim()),
      ...(exemplo ? { exemplo: resolver(exemplo) } : {}),
      ...(situacao ? { status: situacao } : {})
    });
    if (r.error) throw new Error(`${chave}: ${r.error}`);
    for (const w of r.warnings || []) avisos.push(`${ref.codigo} (${chave}): ${w}`);
  };
  for (const item of itens) await escrever(item.chave, item.texto, item.exemplo, item.situacao);
  for (const [dom, texto] of Object.entries(visoes)) {
    await escrever(`${dom}.vis`, texto, undefined, visoesEmRevisao.has(dom) ? 'EM_REVISAO' : SITUACAO);
  }

  if (avisos.length > 0) throw new Error('Avisos na carga:\n' + avisos.join('\n'));
  const { violacoes } = await verificarInvariantes(db);
  if (violacoes.length > 0) throw new Error('Violações de invariantes:\n' + JSON.stringify(violacoes, null, 2));

  const { markdown } = await compileNode(db, refs.get('raiz')!.id);
  const saida = path.join(AQUI, '..', 'especificacao.md');
  const semData = markdown.replace(/^\*\*Data de Exportação:\*\* .*\n\n/m, '');
  fs.writeFileSync(saida, citacoesParaLinks(semData));

  const { rows } = await db.execute("SELECT tipo, COUNT(*) AS n FROM no GROUP BY tipo ORDER BY tipo");
  const { rows: lig } = await db.execute('SELECT COUNT(*) AS n FROM referencia');
  console.log(rows.map((r: any) => `${r.tipo}: ${r.n}`).join(' · '));
  console.log(`Ligações: ${lig[0].n}. Base: ${arquivoDb}. Documento: ${saida}`);
  db.close();
}

main().catch(e => { console.error(e.message || e); process.exit(1); });
