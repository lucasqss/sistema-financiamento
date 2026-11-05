package br.com.financiamento.garantia.aplicacao;

import br.com.financiamento.contratacao.ContratacaoFacade;
import br.com.financiamento.garantia.dominio.Registro;
import br.com.financiamento.garantia.dominio.retorno.ResultadoDoOrgao;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Clock;

/**
 * UC-0018, passo 2 e RN-0029: classifica o retorno do órgão como fato. O
 * registro constituído, a exigência e a recusa movem o contrato; o retorno
 * repetido ou de tentativa antiga é só guardado.
 */
@ApplicationScoped
public class ClassificarRetornoService {

    @Inject
    Registros registros;

    @Inject
    ContratacaoFacade contratacao;

    @Inject
    Clock relogio;

    @Transactional
    public Registro.Efeito executar(String protocolo, ResultadoDoOrgao resultado, String identificacaoRegistrada, String texto) {
        var registro = registros.doProtocolo(protocolo);
        var efeito = registro.classificar(protocolo, resultado, identificacaoRegistrada, texto, relogio.instant());
        if (efeito == Registro.Efeito.REPETIDO) return efeito;
        registros.salvar(registro);
        switch (efeito) {
            case CONSTITUIDA -> contratacao.garantiaRegistrada(registro.contratoId(), protocolo);
            case EXIGENCIA -> contratacao.exigenciaEmitida(registro.contratoId(), registro.observacao());
            case RECUSADO -> contratacao.registroRecusado(registro.contratoId(), registro.observacao());
            default -> { } // pendência de identificação fica com o analista; o contrato segue em registro
        }
        return efeito;
    }
}
