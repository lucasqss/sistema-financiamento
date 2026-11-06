package br.com.financiamento.shared.seguranca;

/**
 * Os papéis que vêm no token (ADR 0012). Os humanos são os atores do modelo de negócio; os de sistema,
 * um por sistema externo que devolve resultados.
 */
public final class Papeis {

    private Papeis() {
    }

    public static final String PROPONENTE = "proponente";          // ATR-0001
    public static final String COOBRIGADO = "coobrigado";          // ATR-0002
    public static final String VENDEDOR = "vendedor";              // ATR-0003, inclui a incorporadora
    public static final String CORRESPONDENTE = "correspondente";  // ATR-0004
    public static final String ANALISTA = "analista";              // ATR-0005
    public static final String ENGENHEIRO = "engenheiro";          // ATR-0006
    public static final String CONJUGE = "conjuge";                // signatário no imóvel (RN-0026)

    public static final String SISTEMA_BUREAU = "sistema-bureau";            // ATR-0008
    public static final String SISTEMA_AVALIADOR = "sistema-avaliador";      // ATR-0007
    public static final String SISTEMA_SEGURADORA = "sistema-seguradora";    // ATR-0011
    public static final String SISTEMA_REGISTRO = "sistema-registro";        // ATR-0009, ATR-0010
    public static final String SISTEMA_FGTS = "sistema-fgts";                // ATR-0012
    public static final String SISTEMA_PAGAMENTO = "sistema-pagamento";

    static final String PREFIXO_DE_SISTEMA = "sistema-";
}
