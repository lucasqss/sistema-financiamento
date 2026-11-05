package br.com.financiamento.garantia.aplicacao;

import br.com.financiamento.contratacao.ContratacaoFacade;
import br.com.financiamento.garantia.dominio.Registro;
import br.com.financiamento.garantia.dominio.SituacaoDoRegistro;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/** O analista (ATR-0005) trata o registro: cumpre exigência, reenvia ou desfaz. */
@ApplicationScoped
public class TratarRegistroService {

    @Inject
    Registros registros;

    @Inject
    ContratacaoFacade contratacao;

    /** UC-0020: cumprida a exigência, o registro é reenviado com novo protocolo. */
    @Transactional
    public Registro cumprirExigencia(Long registroId) {
        var registro = registros.buscar(registroId);
        registro.cumprirExigencia();
        registros.salvar(registro);
        contratacao.exigenciaCumprida(registro.contratoId(), registro.protocolo());
        return registro;
    }

    /** UC-0019: corrigido o motivo da recusa (ou a divergência de identificação), reenvia. */
    @Transactional
    public Registro reenviar(Long registroId) {
        var registro = registros.buscar(registroId);
        var antes = registro.reenviar();
        registros.salvar(registro);
        if (antes == SituacaoDoRegistro.RECUSADO) {
            contratacao.registroSolicitado(registro.contratoId(), registro.orgao(), registro.protocolo());
        }
        return registro;
    }

    /** UC-0019, fluxo alternativo: sem solução, o contrato é desfeito (UC-0016). */
    @Transactional
    public Registro desfazer(Long registroId, String motivo) {
        var registro = registros.buscar(registroId);
        registro.desfazer();
        registros.salvar(registro);
        contratacao.cancelarPorRegistroRecusado(registro.contratoId(),
                motivo == null || motivo.isBlank() ? registro.observacao() : motivo);
        return registro;
    }
}
