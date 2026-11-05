package br.com.financiamento.bem;

import br.com.financiamento.shared.dominio.RegraVioladaException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Núcleo do microkernel: acha o plug-in que atende um tipo de bem. Os plug-ins
 * são descobertos pelo CDI; nenhuma classe fora do próprio plug-in o conhece.
 */
@ApplicationScoped
public class BemFacade {

    @Inject
    @Any
    Instance<CategoriaDeBem> categorias;

    /** O plug-in do tipo; recusa um tipo que nenhum plug-in atende. */
    public CategoriaDeBem categoria(TipoDeBem tipo) {
        return categorias.stream()
                .filter(c -> c.tipos().contains(tipo))
                .findFirst()
                .orElseThrow(() -> new RegraVioladaException("DIM-0001",
                        "Tipo de bem não financiado: " + tipo + ". Tipos aceitos: " + tiposAtendidos()));
    }

    public Set<TipoDeBem> tiposAtendidos() {
        return categorias.stream()
                .flatMap(c -> c.tipos().stream())
                .collect(Collectors.toCollection(() -> new TreeSet<>((a, b) -> a.codigo().compareTo(b.codigo()))));
    }
}
