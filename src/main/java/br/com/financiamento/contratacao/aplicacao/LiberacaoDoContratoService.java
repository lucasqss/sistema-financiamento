package br.com.financiamento.contratacao.aplicacao;

import br.com.financiamento.bem.BemFacade;
import br.com.financiamento.contratacao.ContratoParaDesembolso;
import br.com.financiamento.contratacao.dominio.EstadoContrato;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.Set;

/** Os fatos da liberação e da obra que movem o contrato (CV-0002), vindos do Desembolso. */
@ApplicationScoped
public class LiberacaoDoContratoService {

    private static final Set<EstadoContrato> COM_GARANTIA =
            Set.of(EstadoContrato.GARANTIA_CONSTITUIDA, EstadoContrato.EM_OBRA, EstadoContrato.FORMALIZADO);

    @Inject
    Contratacoes contratacoes;

    @Inject
    BemFacade bens;

    @Transactional
    public ContratoParaDesembolso paraDesembolso(Long contratoId) {
        var c = contratacoes.contrato(contratoId);
        var tipo = c.tipoDeBem();
        return new ContratoParaDesembolso(c.id(), c.propostaId(), tipo, c.condicoes().valorFinanciado(),
                c.condicoes().entradaFgts(), c.documentoDoVendedor(), bens.categoria(tipo).parametros(tipo).liberacao(),
                COM_GARANTIA.contains(c.estado()));
    }

    @Transactional
    public void recursoLiberado(Long contratoId) {
        var c = contratacoes.contrato(contratoId, Contratacoes.aceita("RecursoLiberado"));
        c.recursoLiberado();
        contratacoes.salvar(c);
    }

    @Transactional
    public void parcelaDaObraLiberada(Long contratoId, boolean ultima) {
        var c = contratacoes.contrato(contratoId, Contratacoes.aceita("ParcelaDaObraLiberada"));
        c.parcelaDaObraLiberada(ultima);
        contratacoes.salvar(c);
    }

    @Transactional
    public void medicaoRealizada(Long contratoId) {
        var c = contratacoes.contrato(contratoId);
        c.medicaoRealizada();
        contratacoes.salvar(c);
    }
}
