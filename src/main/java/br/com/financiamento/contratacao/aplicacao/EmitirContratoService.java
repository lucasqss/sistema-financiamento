package br.com.financiamento.contratacao.aplicacao;

import br.com.financiamento.bem.BemFacade;
import br.com.financiamento.contratacao.dominio.Contrato;
import br.com.financiamento.contratacao.dominio.PreparoDoContrato;
import br.com.financiamento.contratacao.dominio.assinatura.Signatario;
import br.com.financiamento.contratacao.dominio.condicoes.CondicoesFinais;
import br.com.financiamento.originacao.OriginacaoFacade;
import br.com.financiamento.originacao.PropostaParaContratar;
import br.com.financiamento.shared.dominio.RegraVioladaException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

/**
 * UC-0014, passo 3: com os seguros contratados, confere de novo a proposta
 * (validade e avaliação, RN-0021 e RN-0022) e emite o contrato com as condições
 * congeladas e os signatários exigidos (RN-0026). A proposta fica contratada.
 */
@ApplicationScoped
public class EmitirContratoService {

    @Inject
    OriginacaoFacade originacao;

    @Inject
    BemFacade bens;

    @Inject
    Contratacoes contratacoes;

    @Inject
    Clock relogio;

    /** Pedido explícito (nova tentativa depois de um impedimento). */
    @Transactional
    public DetalheDaContratacao executar(Long preparoId) {
        var preparo = contratacoes.preparo(preparoId);
        if (preparo.emitido()) return DetalheDaContratacao.de(preparo);
        if (!preparo.segurosContratados()) {
            throw new RegraVioladaException("RN-0025", "O contrato só é emitido com os seguros contratados.");
        }
        return emitir(preparo);
    }

    /** Usado quando o último seguro é contratado. */
    DetalheDaContratacao emitir(PreparoDoContrato preparo) {
        var contratabilidade = originacao.contratabilidade(preparo.propostaId());
        if (!contratabilidade.contratavel()) {
            preparo.impedir(contratabilidade.impedimento());
            contratacoes.salvar(preparo);
            return DetalheDaContratacao.de(preparo);
        }
        var p = contratabilidade.proposta();
        var contrato = Contrato.emitir(p.propostaId(), p.tipoDeBem(), p.documentoDoVendedor(), condicoesFinais(p),
                preparo.seguros(), signatarios(p, preparo), relogio.instant());
        contratacoes.salvar(contrato);
        originacao.contratoEmitido(p.propostaId(), contrato.id());
        preparo.contratoEmitido(contrato.id());
        contratacoes.salvar(preparo);
        return DetalheDaContratacao.de(preparo);
    }

    private static CondicoesFinais condicoesFinais(PropostaParaContratar p) {
        return new CondicoesFinais(p.condicoesVersao(), p.identificacaoDoBem(), p.valorDeCompra(), p.valorDeAvaliacao(),
                p.entradaPropria(), p.entradaFgts(), p.valorFinanciado(), p.prazoMeses(), p.parcela());
    }

    /** RN-0026: todos os participantes; no imóvel e no terreno, também o vendedor e os cônjuges. */
    private List<Signatario> signatarios(PropostaParaContratar p, PreparoDoContrato preparo) {
        var lista = new ArrayList<Signatario>();
        p.participantes().forEach(x -> lista.add(new Signatario(x.papel(), x.documento(), x.nome())));
        if (bens.categoria(p.tipoDeBem()).vendedorEConjugesAssinam(p.tipoDeBem())) {
            lista.add(new Signatario("VENDEDOR", p.documentoDoVendedor(), "Vendedor"));
            lista.addAll(preparo.conjuges());
        }
        return lista;
    }
}
