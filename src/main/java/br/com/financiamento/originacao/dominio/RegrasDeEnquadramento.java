package br.com.financiamento.originacao.dominio;

import br.com.financiamento.bem.CategoriaDeBem;
import br.com.financiamento.bem.DadosDoEnquadramento;
import br.com.financiamento.bem.ParametrosDoTipo;
import br.com.financiamento.originacao.dominio.documento.DocumentoEntregue;
import br.com.financiamento.originacao.dominio.participante.Participante;
import br.com.financiamento.shared.dominio.DocumentoFiscal;
import br.com.financiamento.shared.dominio.Violacao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * As regras de enquadramento da originação (RN-0018): as comuns a todo bem
 * (RN-0001 a RN-0009, RN-0015) e as do plug-in do tipo de bem. Nenhuma regra
 * aqui pergunta a categoria: o que varia vem dos parâmetros e do plug-in.
 */
final class RegrasDeEnquadramento {

    static final int MAXIMO_DE_PARTICIPANTES = 5;
    static final BigDecimal COMPROMETIMENTO_MAXIMO = new BigDecimal("0.30");
    static final int MAIORIDADE = 18;

    private final Proposta proposta;
    private final CategoriaDeBem categoria;
    private final ParametrosDoTipo parametros;
    private final LocalDate hoje;
    private final List<Violacao> violacoes = new ArrayList<>();

    private RegrasDeEnquadramento(Proposta proposta, CategoriaDeBem categoria, LocalDate hoje) {
        this.proposta = proposta;
        this.categoria = categoria;
        this.parametros = categoria.parametros(proposta.tipoDeBem());
        this.hoje = hoje;
    }

    static Enquadramento avaliar(Proposta proposta, CategoriaDeBem categoria, LocalDate hoje,
                                 boolean bemEmOutraPropostaAtiva) {
        var r = new RegrasDeEnquadramento(proposta, categoria, hoje);
        r.completude();
        r.participantes();
        r.pessoaJuridica();
        r.vendedor();
        if (bemEmOutraPropostaAtiva) r.violar("RN-0004", "O bem já está em outra proposta ativa.");
        r.condicoes();
        r.documentos();
        r.violacoes.addAll(categoria.verificarRegrasProprias(r.dadosParaOPlugin()));
        return new Enquadramento(r.violacoes);
    }

    private void violar(String regra, String mensagem) {
        violacoes.add(new Violacao(regra, mensagem));
    }

    /** RN-0018: a proposta completa. */
    private void completude() {
        if (proposta.proponente() == null) violar("RN-0018", "Informe o proponente.");
        var bem = proposta.bem();
        if (bem == null || bem.identificacao() == null || bem.identificacao().isBlank()) {
            violar("RN-0018", "Informe a identificação do bem.");
        }
        if (bem == null || bem.valorDeCompra() == null || bem.valorDeCompra().signum() <= 0) {
            violar("RN-0018", "Informe o valor de compra do bem.");
        }
        if (bem == null || bem.documentoDoVendedor() == null || bem.documentoDoVendedor().isBlank()) {
            violar("RN-0018", "Informe o vendedor.");
        }
        if (proposta.condicoes() == null) violar("RN-0018", "Informe entrada, prazo e parcela.");
    }

    /** RN-0001 e RN-0033. */
    private void participantes() {
        var lista = proposta.participantes();
        if (lista.isEmpty()) return;
        if (lista.size() > MAXIMO_DE_PARTICIPANTES) {
            violar("RN-0001", "A proposta tem " + lista.size() + " participantes; o máximo é 5 (1 proponente e até 4 coobrigados).");
        }
        long proponentes = lista.stream().filter(Participante::proponente).count();
        if (proponentes != 1) violar("RN-0001", "A proposta tem " + proponentes + " proponentes; deve ter exatamente 1.");
        var documentos = new HashSet<String>();
        for (var p : lista) {
            if (!documentos.add(p.documento())) violar("RN-0001", "Participante repetido: " + p.documento() + ".");
            if (!p.documentoValido()) {
                violar("RN-0033", (p.pessoaFisica() ? "CPF" : "CNPJ") + " inválido: " + p.documento() + ".");
            }
            if (p.pessoaFisica()) {
                if (p.nascimento() == null) violar("RN-0001", "Informe a data de nascimento de " + p.nome() + ".");
                else if (Period.between(p.nascimento(), hoje).getYears() < MAIORIDADE) {
                    violar("RN-0001", p.nome() + " não é maior de idade.");
                }
            }
        }
    }

    /** RN-0002: PJ como proponente só onde o tipo de bem aceita; coobrigado PJ só em proposta de PJ. */
    private void pessoaJuridica() {
        var proponente = proposta.proponente();
        if (proponente == null) return;
        if (!proponente.pessoaFisica() && !parametros.aceitaPessoaJuridica()) {
            violar("RN-0002", "Este tipo de bem não aceita pessoa jurídica como proponente.");
        }
        if (proponente.pessoaFisica() && proposta.participantes().stream().anyMatch(p -> !p.pessoaFisica())) {
            violar("RN-0002", "Coobrigado pessoa jurídica só em proposta de proponente pessoa jurídica.");
        }
    }

    /** RN-0003: vendedor identificado e diferente do proponente. */
    private void vendedor() {
        var bem = proposta.bem();
        if (bem == null || bem.documentoDoVendedor() == null || bem.documentoDoVendedor().isBlank()) return;
        var doc = bem.documentoDoVendedor();
        if (!DocumentoFiscal.cpfValido(doc) && !DocumentoFiscal.cnpjValido(doc)) {
            violar("RN-0003", "CPF ou CNPJ do vendedor inválido: " + doc + ".");
        }
        var proponente = proposta.proponente();
        if (proponente != null && proponente.documento().equals(doc)) {
            violar("RN-0003", "O vendedor não pode ser o proponente.");
        }
    }

    /** RN-0005 a RN-0008 e RN-0015. */
    private void condicoes() {
        var c = proposta.condicoes();
        var bem = proposta.bem();
        if (c == null) return;

        if (c.prazoMeses() < parametros.prazoMinimoMeses() || c.prazoMeses() > parametros.prazoMaximoMeses()) {
            violar("RN-0007", "Prazo de " + c.prazoMeses() + " meses fora da faixa do tipo: "
                    + parametros.prazoMinimoMeses() + " a " + parametros.prazoMaximoMeses() + ".");
        }

        if (c.entradaFgts().signum() > 0) {
            var proponente = proposta.proponente();
            if (!parametros.aceitaFgtsNaEntrada() || proponente == null || !proponente.pessoaFisica()) {
                violar("RN-0015", "Só imóvel residencial de proponente pessoa física aceita FGTS na entrada.");
            }
        }

        var renda = proposta.participantes().stream().map(Participante::rendaComprovada)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (c.parcela() == null || c.parcela().signum() <= 0) {
            violar("RN-0018", "Informe a parcela simulada.");
        } else if (c.parcela().compareTo(renda.multiply(COMPROMETIMENTO_MAXIMO)) > 0) {
            violar("RN-0008", "Parcela de " + c.parcela() + " passa de 30% da renda comprovada somada ("
                    + renda.multiply(COMPROMETIMENTO_MAXIMO).setScale(2, RoundingMode.HALF_UP) + ").");
        }

        if (bem == null || bem.valorDeCompra() == null) return;
        var financiado = c.valorFinanciado(bem.valorDeCompra());
        if (financiado.signum() <= 0) {
            violar("RN-0005", "O valor financiado precisa ser maior que zero.");
            return;
        }
        var limite = parametros.cotaMaxima().multiply(bem.valorDeReferencia());
        if (financiado.compareTo(limite) > 0) {
            var entradaMinima = bem.valorDeCompra().subtract(limite).setScale(2, RoundingMode.HALF_UP);
            violar("RN-0006", "Valor financiado de " + financiado + " passa da cota máxima de "
                    + parametros.cotaMaxima().movePointRight(2).stripTrailingZeros().toPlainString()
                    + "% do valor de referência (" + bem.valorDeReferencia() + "). A entrada mínima é " + entradaMinima + ".");
        }
    }

    /** RN-0009: os documentos exigidos pelo tipo de bem (RN-0016) e pela forma de cada participante (RN-0017). */
    private void documentos() {
        var tipo = proposta.tipoDeBem();
        for (var exigido : categoria.documentosDoBem(tipo)) {
            exigirDocumento(DocumentoEntregue.DO_BEM, exigido, "do bem");
        }
        for (var p : proposta.participantes()) {
            for (var exigido : categoria.documentosDoParticipante(tipo, p.forma())) {
                exigirDocumento(p.documento(), exigido, "de " + p.nome());
            }
        }
    }

    private void exigirDocumento(String titular, String tipo, String de) {
        // Documento recusado na conferência conta como não entregue: precisa ser reenviado.
        if (proposta.documentos().stream().noneMatch(d -> d.mesmo(titular, tipo)
                && d.situacao() != DocumentoEntregue.Situacao.RECUSADO)) {
            violar("RN-0009", "Falta o documento " + de + ": " + tipo + ".");
        }
    }

    private DadosDoEnquadramento dadosParaOPlugin() {
        var bem = proposta.bem();
        var proponente = proposta.proponente();
        return new DadosDoEnquadramento(
                proposta.tipoDeBem(),
                hoje,
                proposta.condicoes() == null ? null : proposta.condicoes().prazoMeses(),
                bem == null ? null : bem.anoModelo(),
                bem == null ? null : bem.construcaoAverbada(),
                proponente == null ? null : proponente.forma(),
                proponente == null ? null : proponente.nascimento());
    }

}
