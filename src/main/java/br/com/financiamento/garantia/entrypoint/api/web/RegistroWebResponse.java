package br.com.financiamento.garantia.entrypoint.api.web;

import br.com.financiamento.garantia.dominio.Registro;

import java.time.Instant;
import java.util.List;

public record RegistroWebResponse(Long id, Long contratoId, String orgao, String identificacaoDoBem, int tentativa,
                                  String protocolo, String situacao, String observacao, List<Retorno> retornos) {

    public record Retorno(String protocolo, String resultado, String identificacaoRegistrada, String texto,
                          Instant recebidoEm, String efeito) {
    }

    public static RegistroWebResponse de(Registro r) {
        return new RegistroWebResponse(r.id(), r.contratoId(), r.orgao(), r.identificacaoDoBem(), r.tentativa(),
                r.protocolo(), r.situacao().name(), r.observacao(),
                r.retornos().stream().map(x -> new Retorno(x.protocolo(), x.resultado().name(), x.identificacaoRegistrada(),
                        x.texto(), x.recebidoEm(), x.efeito())).toList());
    }
}
