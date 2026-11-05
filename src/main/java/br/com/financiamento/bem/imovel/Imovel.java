package br.com.financiamento.bem.imovel;

import br.com.financiamento.bem.CategoriaDeBem;
import br.com.financiamento.bem.DadosDoEnquadramento;
import br.com.financiamento.bem.FormaDoParticipante;
import br.com.financiamento.bem.ParametrosDoTipo;
import br.com.financiamento.bem.TipoDeBem;
import br.com.financiamento.shared.dominio.Violacao;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static br.com.financiamento.bem.FormaDeLiberacao.POR_MEDICAO;
import static br.com.financiamento.bem.FormaDeLiberacao.UNICA;

/**
 * Plug-in da categoria Imóvel: residencial e comercial, pronto e na planta, e
 * terreno urbano e rural.
 */
@ApplicationScoped
class Imovel implements CategoriaDeBem {

    static final TipoDeBem RESIDENCIAL_PRONTO = TipoDeBem.de("RESIDENCIAL_PRONTO");
    static final TipoDeBem RESIDENCIAL_NA_PLANTA = TipoDeBem.de("RESIDENCIAL_NA_PLANTA");
    static final TipoDeBem COMERCIAL_PRONTO = TipoDeBem.de("COMERCIAL_PRONTO");
    static final TipoDeBem COMERCIAL_NA_PLANTA = TipoDeBem.de("COMERCIAL_NA_PLANTA");
    static final TipoDeBem TERRENO_URBANO = TipoDeBem.de("TERRENO_URBANO");
    static final TipoDeBem TERRENO_RURAL = TipoDeBem.de("TERRENO_RURAL");

    /** RN-0010, colunas de imóvel. */
    private static final Map<TipoDeBem, ParametrosDoTipo> PARAMETROS = Map.of(
            RESIDENCIAL_PRONTO, ParametrosDoTipo.de(60, 420, "0.80", false, true, null, UNICA),
            RESIDENCIAL_NA_PLANTA, ParametrosDoTipo.de(60, 420, "0.80", false, true, null, POR_MEDICAO),
            COMERCIAL_PRONTO, ParametrosDoTipo.de(60, 240, "0.70", true, false, null, UNICA),
            COMERCIAL_NA_PLANTA, ParametrosDoTipo.de(60, 240, "0.70", true, false, null, POR_MEDICAO),
            TERRENO_URBANO, ParametrosDoTipo.de(60, 180, "0.60", false, false, null, UNICA),
            TERRENO_RURAL, ParametrosDoTipo.de(60, 120, "0.50", false, false, null, UNICA));

    /** RN-0016. */
    private static final Map<TipoDeBem, List<String>> DOCUMENTOS_DO_BEM = Map.of(
            RESIDENCIAL_PRONTO, List.of("matrícula atualizada", "certidões do vendedor"),
            COMERCIAL_PRONTO, List.of("matrícula atualizada", "habite-se", "certidões do vendedor"),
            RESIDENCIAL_NA_PLANTA, List.of("contrato com a incorporadora", "memorial de incorporação", "cronograma da obra"),
            COMERCIAL_NA_PLANTA, List.of("contrato com a incorporadora", "memorial de incorporação", "cronograma da obra"),
            TERRENO_URBANO, List.of("matrícula", "IPTU", "certidões do vendedor"),
            TERRENO_RURAL, List.of("matrícula", "CCIR", "ITR", "CAR", "certidões do vendedor"));

    private static final Set<TipoDeBem> TERRENOS = Set.of(TERRENO_URBANO, TERRENO_RURAL);

    private static final List<String> DOCUMENTOS_PF =
            List.of("identidade", "comprovante de renda", "comprovante de residência", "estado civil");
    private static final List<String> DOCUMENTOS_PJ =
            List.of("contrato social", "faturamento de 12 meses", "documentos dos sócios", "procuração");

    /** RN-0012: idade do proponente somada ao prazo, no máximo 80 anos e 6 meses. */
    static final int IDADE_MAXIMA_FINAL_MESES = 80 * 12 + 6;

    @Override
    public String nome() {
        return "Imóvel";
    }

    @Override
    public Set<TipoDeBem> tipos() {
        return PARAMETROS.keySet();
    }

    @Override
    public ParametrosDoTipo parametros(TipoDeBem tipo) {
        return PARAMETROS.get(tipo);
    }

    @Override
    public List<String> documentosDoBem(TipoDeBem tipo) {
        return DOCUMENTOS_DO_BEM.get(tipo);
    }

    /** RN-0017: PJ só no comercial. */
    @Override
    public List<String> documentosDoParticipante(TipoDeBem tipo, FormaDoParticipante forma) {
        if (forma == FormaDoParticipante.PESSOA_FISICA) return DOCUMENTOS_PF;
        return parametros(tipo).aceitaPessoaJuridica() ? DOCUMENTOS_PJ : List.of();
    }

    @Override
    public List<Violacao> verificarRegrasProprias(DadosDoEnquadramento dados) {
        var violacoes = new ArrayList<Violacao>();
        verificarIdadeDoProponente(dados, violacoes);
        if (TERRENOS.contains(dados.tipo())) verificarTerrenoSemConstrucao(dados, violacoes);
        return violacoes;
    }

    /** RN-0012. */
    private void verificarIdadeDoProponente(DadosDoEnquadramento dados, List<Violacao> violacoes) {
        if (dados.formaDoProponente() != FormaDoParticipante.PESSOA_FISICA
                || dados.nascimentoDoProponente() == null || dados.prazoMeses() == null) {
            return;
        }
        var idade = Period.between(dados.nascimentoDoProponente(), dados.dataDeReferencia());
        int idadeEmMeses = (int) idade.toTotalMonths();
        if (idadeEmMeses + dados.prazoMeses() > IDADE_MAXIMA_FINAL_MESES) {
            int prazoMaximo = Math.max(0, IDADE_MAXIMA_FINAL_MESES - idadeEmMeses);
            violacoes.add(new Violacao("RN-0012", "Idade do proponente somada ao prazo passa de 80 anos e 6 meses; "
                    + "o prazo máximo possível é de " + prazoMaximo + " meses."));
        }
    }

    /** RN-0014. Os documentos do terreno rural (CCIR, ITR, CAR) estão em RN-0016. */
    private void verificarTerrenoSemConstrucao(DadosDoEnquadramento dados, List<Violacao> violacoes) {
        if (dados.construcaoAverbada() == null) {
            violacoes.add(new Violacao("RN-0014", "Informe se há construção averbada na matrícula do terreno."));
        } else if (dados.construcaoAverbada()) {
            violacoes.add(new Violacao("RN-0014", "Terreno com construção averbada não é financiado como terreno."));
        }
    }

    @Override
    public Period validadeDaContraproposta() {
        return Period.ofDays(15);
    }
}
