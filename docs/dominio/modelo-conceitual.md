# Modelo conceitual

Diagrama de **conceitos de negócio**, sem tecnologia. Ele indica relações e responsabilidades; tipos de dado,
tabelas e classes concretas virão no espaço da solução.

```mermaid
classDiagram
    direction LR

    class Proposta {
        <<agregado>>
        número
        situação
        data de submissão
        validade
        submeter()
        aplicarAvaliação()
        aplicarAnáliseDeCrédito()
        cancelar()
    }
    class Participante {
        papel: proponente | coobrigado
        CPF
        nome
        data de nascimento
        renda comprovada
    }
    class Bem {
        <<abstrato>>
        categoria
        subtipo
        valor de compra
        valor de avaliação
        valorDeReferência()
    }
    class Veiculo {
        condição: novo | usado
        chassi
        placa
        RENAVAM
        ano-modelo
    }
    class Imovel {
        matrícula
        cartório
        endereço
        área
    }
    class Terreno {
        urbano | rural
        sem construção
    }
    class Condicoes {
        <<valor>>
        entrada
        valor financiado
        prazo
        parcela
        ltv()
        comprometimentoDeRenda()
    }
    class Vendedor {
        CPF ou CNPJ
        nome
        conta para pagamento
    }
    class Documento {
        tipo
        situação
    }
    class Avaliacao {
        fonte: laudo | tabela | nota fiscal
        valor
        data
        aceito como garantia
    }
    class AnaliseCredito {
        resultado: aprovada | ressalva | reprovada
        ressalvas
        data
    }
    class RegrasDoTipo {
        <<política>>
        prazo mín/máx
        LTV máximo
        idade máxima
        documentos exigidos
        enquadrar(proposta)
    }
    class Contrato {
        <<agregado>>
        número
        situação
        condições finais
        assinar()
        registrarGarantia()
        liberarRecurso()
    }
    class Assinatura {
        signatário
        data
    }
    class Garantia {
        tipo: alienação fiduciária
        órgão: cartório | trânsito
        protocolo
        situação
        exigências
    }
    class Liberacao {
        favorecido
        valor
        data
    }

    Proposta "1" *-- "1..*" Participante
    Proposta "1" *-- "1" Bem
    Proposta "1" *-- "0..1" Condicoes
    Proposta "1" *-- "*" Documento
    Proposta "1" --> "1" Vendedor
    Proposta ..> RegrasDoTipo : enquadrada por
    Bem <|-- Veiculo
    Bem <|-- Imovel
    Imovel <|-- Terreno
    Proposta "1" o-- "0..1" Avaliacao
    Proposta "1" o-- "0..1" AnaliseCredito
    Contrato "1" --> "1" Proposta : nasce de (por referência)
    Contrato "1" *-- "1..*" Assinatura
    Contrato "1" *-- "1" Garantia
    Contrato "1" *-- "0..1" Liberacao
```

> Terreno aparece como especialização de Imóvel porque compartilha identificação (matrícula) e registro (cartório);
> o que muda são as regras e os documentos. Em [questões em aberto](questoes-em-aberto.md) fica a decisão de
> mantê-lo como subtipo ou especialização.

## Agregados candidatos

### Proposta
Fronteira de consistência da **originação**: participantes, bem, condições, documentos e os resultados de avaliação e crédito
**como chegaram à proposta**.

Invariantes:
1. Categoria, subtipo e condição são fixados ao iniciar e nunca mudam.
2. Só o **rascunho** e a proposta **com pendência** aceitam alteração de dados.
3. Para submeter: tudo completo e enquadrado nas regras do tipo (C1–C8).
4. Qualquer mudança em valor de compra, entrada, prazo ou avaliação dispara novo enquadramento.
5. Só é aprovada com avaliação aceita **e** crédito aprovado, ambos válidos e posteriores à última alteração das condições.
6. Uma proposta gera **no máximo um** contrato.

### Contrato
Fronteira de consistência da **formalização e garantia**: condições finais (cópia congelada da proposta), assinaturas,
garantia e liberação. Referencia a proposta pelo identificador, não a altera.

Invariantes:
1. Condições do contrato são imutáveis após a emissão.
2. Só vai a registro com todas as assinaturas exigidas pela categoria.
3. Um retorno de registro só é aceito se a identificação do bem registrada for a do contrato; retorno repetido não tem efeito.
4. O recurso só é liberado com a garantia constituída, uma única vez, ao vendedor da proposta.

### Fora dos agregados
- **Regras do tipo** são uma **política** consultada pela proposta, não parte dela; variam por categoria/subtipo/condição.
- **Avaliação** e **Análise de crédito** são produzidas por outros contextos; a proposta guarda o **resultado** (valor, decisão, data).
- **Vendedor** e **Participante** podem vir de um cadastro de pessoas; a proposta guarda o que precisa para a decisão.

## Por que dois agregados, e não um

- A proposta muda muito e é editada por pessoas; o contrato é estável e reage a sistemas externos.
- As invariantes não se cruzam depois da emissão: nada no contrato precisa da proposta mudando junto.
- A proposta pode terminar sem contrato (reprovada, expirada, cancelada), o que é a maioria dos casos.
