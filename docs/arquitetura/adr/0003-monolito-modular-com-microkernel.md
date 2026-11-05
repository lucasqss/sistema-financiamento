# 0003. Monólito modular com microkernel por categoria de bem

**Status:** Aceita · **Data:** 2026-11-05

## Contexto
Comparando os estilos de Richards com as três características que dirigem ([0001](0001-caracteristicas-de-arquitetura.md)):
- o **monólito modular** dá evolutividade e confiabilidade a baixo custo, mas pouca extensibilidade;
- o **microkernel** dá a maior extensibilidade, com um núcleo e plug-ins;
- os estilos **distribuídos** (baseado em serviços, eventos, microsserviços) elevam a evolutividade, mas custam operação e consistência distribuída, o que não se paga com 1 time e volume baixo.

No modelo de negócio, a variação está concentrada no **tipo de bem**. Prazo, cota, idade, documentos, avaliação, seguros, registro e liberação variam pela dimensão "Tipo de bem" (DIM-0001). A jornada é a mesma para todos.

## Decisão
**Monólito modular**:
- um módulo por domínio do modelo: `originacao`, `credito`, `avaliacao`, `contratacao`, `garantia`, `desembolso`, `pessoas`, `documentos`, mais `shared`;
- a organização interna segue a skill `arquitetura-modular`: `dominio/aplicacao/entrypoint/integracao`, uma `Facade` por módulo e referência por ID entre módulos.

**Microkernel** no ponto de variação:
- o núcleo da originação define uma porta de **categoria de bem**;
- os plug-ins são `veiculo` (carro e moto, novo e usado) e `imovel` (residencial e comercial, pronto e na planta, e terreno urbano e rural);
- cada plug-in responde enquadramento, documentos, avaliação, seguros, órgão de registro e forma de liberação da sua categoria;
- os outros módulos consultam a categoria pelo mesmo contrato.

## Consequências
- Um tipo de bem novo (ex.: máquina agrícola) é um plug-in novo e uma entrada na dimensão "Tipo de bem", sem mudar o núcleo.
- O núcleo não pode ter `if` por categoria. Isso é garantido por fitness function ([0008](0008-fitness-functions.md)).
- Os módulos podem virar serviços no futuro, porque já têm fachada e esquema próprios.
- Disciplina obrigatória: fronteiras verificadas nos testes, ou o monólito degrada em "bola de lama".
