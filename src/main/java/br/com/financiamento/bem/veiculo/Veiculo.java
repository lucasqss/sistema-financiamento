package br.com.financiamento.bem.veiculo;

import br.com.financiamento.bem.CategoriaDeBem;
import br.com.financiamento.bem.DadosDoEnquadramento;
import br.com.financiamento.bem.FormaDoParticipante;
import br.com.financiamento.bem.ParametrosDoTipo;
import br.com.financiamento.bem.TipoDeBem;
import br.com.financiamento.shared.dominio.Violacao;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static br.com.financiamento.bem.FormaDeLiberacao.UNICA;

/** Plug-in da categoria Veículo: carro e moto, novos e usados. */
@ApplicationScoped
class Veiculo implements CategoriaDeBem {

    static final TipoDeBem CARRO_NOVO = TipoDeBem.de("CARRO_NOVO");
    static final TipoDeBem CARRO_USADO = TipoDeBem.de("CARRO_USADO");
    static final TipoDeBem MOTO_NOVA = TipoDeBem.de("MOTO_NOVA");
    static final TipoDeBem MOTO_USADA = TipoDeBem.de("MOTO_USADA");

    /** RN-0010, colunas de veículo. */
    private static final Map<TipoDeBem, ParametrosDoTipo> PARAMETROS = Map.of(
            CARRO_NOVO, ParametrosDoTipo.de(12, 60, "0.90", true, false, null, UNICA),
            CARRO_USADO, ParametrosDoTipo.de(12, 60, "0.80", false, false, 15, UNICA),
            MOTO_NOVA, ParametrosDoTipo.de(12, 48, "0.90", true, false, null, UNICA),
            MOTO_USADA, ParametrosDoTipo.de(12, 36, "0.70", false, false, 10, UNICA));

    private static final Set<TipoDeBem> NOVOS = Set.of(CARRO_NOVO, MOTO_NOVA);

    private static final List<String> DOCUMENTOS_PF =
            List.of("identidade", "comprovante de renda", "comprovante de residência");
    private static final List<String> DOCUMENTOS_PJ =
            List.of("contrato social", "faturamento de 12 meses", "documentos dos sócios", "procuração");

    @Override
    public String nome() {
        return "Veículo";
    }

    @Override
    public Set<TipoDeBem> tipos() {
        return PARAMETROS.keySet();
    }

    @Override
    public ParametrosDoTipo parametros(TipoDeBem tipo) {
        return PARAMETROS.get(tipo);
    }

    /** RN-0016. O laudo de vistoria do veículo de particular vem da Avaliação. */
    @Override
    public List<String> documentosDoBem(TipoDeBem tipo) {
        return NOVOS.contains(tipo) ? List.of("nota fiscal ou pedido da concessionária") : List.of("CRLV");
    }

    /** RN-0017: PJ só no veículo novo. */
    @Override
    public List<String> documentosDoParticipante(TipoDeBem tipo, FormaDoParticipante forma) {
        if (forma == FormaDoParticipante.PESSOA_FISICA) return DOCUMENTOS_PF;
        return NOVOS.contains(tipo) ? DOCUMENTOS_PJ : List.of();
    }

    /** RN-0011: idade máxima do veículo usado ao fim do contrato, contada do ano-modelo. */
    @Override
    public List<Violacao> verificarRegrasProprias(DadosDoEnquadramento dados) {
        Integer idadeMaxima = parametros(dados.tipo()).idadeMaximaDoBem();
        if (idadeMaxima == null) return List.of();
        if (dados.anoModelo() == null) {
            return List.of(new Violacao("RN-0011", "Informe o ano-modelo do veículo usado."));
        }
        if (dados.prazoMeses() == null) return List.of();
        int anoFinal = dados.dataDeReferencia().plusMonths(dados.prazoMeses()).getYear();
        int idadeFinal = anoFinal - dados.anoModelo();
        if (idadeFinal > idadeMaxima) {
            return List.of(new Violacao("RN-0011", "O veículo terá " + idadeFinal + " anos ao fim do contrato ("
                    + anoFinal + "); o máximo é " + idadeMaxima + "."));
        }
        return List.of();
    }

    /** RN-0023: veículo novo pela nota fiscal ou preço de fábrica; usado pela tabela de referência. */
    @Override
    public String fonteDaAvaliacao(TipoDeBem tipo) {
        return NOVOS.contains(tipo) ? "nota fiscal ou preço de fábrica" : "tabela de referência";
    }

    /** RN-0023: novo vale 30 dias; usado vale até o fim do mês da consulta. */
    @Override
    public LocalDate validadeDaAvaliacao(TipoDeBem tipo, LocalDate avaliadaEm) {
        return NOVOS.contains(tipo) ? avaliadaEm.plusDays(30) : avaliadaEm.with(TemporalAdjusters.lastDayOfMonth());
    }

    /** RN-0025. */
    @Override
    public List<String> segurosExigidos(TipoDeBem tipo) {
        return List.of("seguro do bem");
    }

    @Override
    public List<String> segurosOpcionais(TipoDeBem tipo) {
        return List.of("prestamista");
    }

    /** RN-0026: no veículo assinam só os participantes. */
    @Override
    public boolean vendedorEConjugesAssinam(TipoDeBem tipo) {
        return false;
    }

    /** RN-0028: gravame no órgão de trânsito, pelo sistema de gravames (F-0002, F-0003). */
    @Override
    public String orgaoDeRegistro(TipoDeBem tipo) {
        return "órgão de trânsito";
    }

    @Override
    public Period validadeDaContraproposta() {
        return Period.ofDays(5);
    }
}
