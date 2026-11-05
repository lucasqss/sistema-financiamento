package br.com.financiamento.garantia.aplicacao;

import br.com.financiamento.bem.BemFacade;
import br.com.financiamento.contratacao.ContratacaoFacade;
import br.com.financiamento.contratacao.ContratoAssinado;
import br.com.financiamento.garantia.dominio.Registro;
import br.com.financiamento.garantia.dominio.RegistroRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * UC-0018, passo 1: o contrato assinado vai ao órgão da categoria (RN-0028)
 * com um protocolo nosso; o envio sai pelo outbox. Emite RegistroSolicitado.
 */
@ApplicationScoped
public class SolicitarRegistroService {

    @Inject
    RegistroRepository repositorio;

    @Inject
    Registros registros;

    @Inject
    BemFacade bens;

    @Inject
    ContratacaoFacade contratacao;

    @Transactional
    public void executar(ContratoAssinado contrato) {
        if (repositorio.doContrato(contrato.contratoId()).isPresent()) return;
        var orgao = bens.categoria(contrato.tipoDeBem()).orgaoDeRegistro(contrato.tipoDeBem());
        var registro = Registro.solicitar(contrato.contratoId(), contrato.propostaId(), contrato.tipoDeBem(),
                contrato.identificacaoDoBem(), orgao);
        registros.salvar(registro);
        contratacao.registroSolicitado(contrato.contratoId(), orgao, registro.protocolo());
    }
}
