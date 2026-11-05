package br.com.financiamento.garantia.aplicacao;

import br.com.financiamento.garantia.dominio.Registro;
import br.com.financiamento.garantia.dominio.RegistroRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.Optional;

@ApplicationScoped
public class ConsultarRegistrosService {

    @Inject
    RegistroRepository repositorio;

    @Transactional
    public Optional<Registro> doContrato(Long contratoId) {
        return repositorio.doContrato(contratoId);
    }
}
