package br.com.financiamento.bem;

import br.com.financiamento.shared.validacao.Falha;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Set;

/**
 * Porta do microkernel (ADR 0003): o que varia por categoria de bem.
 *
 * Cada plug-in (veículo, imóvel, ...) atende um conjunto de tipos de bem da
 * dimensão DIM-0001. O núcleo nunca pergunta "é veículo?": pede ao plug-in do
 * tipo. Um tipo de bem novo é um plug-in novo, sem mudar o núcleo.
 */
public interface CategoriaDeBem {

    /** Nome da categoria no modelo de negócio ("Veículo", "Imóvel"). */
    String nome();

    /** Os tipos de bem finais da dimensão DIM-0001 que este plug-in atende. */
    Set<TipoDeBem> tipos();

    /** RN-0010. */
    ParametrosDoTipo parametros(TipoDeBem tipo);

    /** RN-0016: documentos do bem. */
    List<String> documentosDoBem(TipoDeBem tipo);

    /** RN-0017: documentos de cada participante; vazio onde a forma não pode ser proponente. */
    List<String> documentosDoParticipante(TipoDeBem tipo, FormaDoParticipante forma);

    /** Regras próprias da categoria (ex.: RN-0011, RN-0012, RN-0014). Vazio quando enquadra. */
    List<Falha> verificarRegrasProprias(DadosDoEnquadramento dados);

    /** RN-0023: de onde vem o valor de avaliação. */
    String fonteDaAvaliacao(TipoDeBem tipo);

    /** RN-0023 e DEC-0016: até quando vale uma avaliação feita na data informada. */
    LocalDate validadeDaAvaliacao(TipoDeBem tipo, LocalDate avaliadaEm);

    /** RN-0025: seguros obrigatórios para emitir o contrato. */
    List<String> segurosExigidos(TipoDeBem tipo);

    /** RN-0025: seguros que o proponente pode escolher contratar. */
    List<String> segurosOpcionais(TipoDeBem tipo);

    /** RN-0026: além dos participantes, o vendedor e os cônjuges assinam o contrato? */
    boolean vendedorEConjugesAssinam(TipoDeBem tipo);

    /** RN-0028: onde a alienação fiduciária é registrada. */
    String orgaoDeRegistro(TipoDeBem tipo);

    /** RN-0020: validade da contraproposta. */
    Period validadeDaContraproposta();
}
