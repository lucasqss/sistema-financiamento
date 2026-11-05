package br.com.financiamento.garantia.aplicacao;

import br.com.financiamento.contratacao.ContratacaoFacade;
import br.com.financiamento.garantia.GarantiaConstituida;
import br.com.financiamento.garantia.OuvinteDaGarantia;
import br.com.financiamento.garantia.dominio.Registro;
import br.com.financiamento.garantia.dominio.retorno.ResultadoDoOrgao;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Clock;

/**
 * UC-0018, passo 2 e RN-0029: classifica o retorno do órgão como fato. O
 * registro constituído, a exigência e a recusa movem o contrato; o retorno
 * repetido ou de tentativa antiga é só guardado. Constituída, avisa quem ouve
 * a garantia (o Desembolso), na mesma transação.
 */
@ApplicationScoped
public class ClassificarRetornoService {

    @Inject
    Registros registros;

    @Inject
    ContratacaoFacade contratacao;

    @Inject
    Clock relogio;

    @Inject
    @Any
    Instance<OuvinteDaGarantia> ouvintes;

    @Transactional
    public Registro.Efeito executar(String protocolo, ResultadoDoOrgao resultado, String identificacaoRegistrada, String texto) {
        var registro = registros.doProtocolo(protocolo);
        var efeito = registro.classificar(protocolo, resultado, identificacaoRegistrada, texto, relogio.instant());
        if (efeito == Registro.Efeito.REPETIDO) return efeito;
        registros.salvar(registro);
        switch (efeito) {
            case CONSTITUIDA -> {
                contratacao.garantiaRegistrada(registro.contratoId(), protocolo);
                var constituida = new GarantiaConstituida(registro.contratoId(), registro.propostaId(), protocolo);
                ouvintes.forEach(o -> o.garantiaConstituida(constituida));
            }
            case EXIGENCIA -> contratacao.exigenciaEmitida(registro.contratoId(), registro.observacao());
            case RECUSADO -> contratacao.registroRecusado(registro.contratoId(), registro.observacao());
            default -> { } // pendência de identificação fica com o analista; o contrato segue em registro
        }
        return efeito;
    }
}
