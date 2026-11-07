package br.com.financiamento.shared.web;

import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.persistence.OptimisticLockException;
import org.hibernate.StaleStateException;
import org.jboss.logging.Logger;

/**
 * Repete a chamada que perdeu no controle de concorrência otimista (versão do agregado), até 5 vezes, com uma
 * pequena espera crescente. A transação fica no caso de uso, dentro da chamada: cada repetição é uma transação nova.
 */
@RepeteEmConflito
@Interceptor
@Priority(Interceptor.Priority.PLATFORM_BEFORE)
class RepeticaoEmConflito {

    private static final Logger LOG = Logger.getLogger(RepeticaoEmConflito.class);
    static final int TENTATIVAS = 5;

    @AroundInvoke
    Object repetir(InvocationContext chamada) throws Exception {
        for (int tentativa = 1; ; tentativa++) {
            try {
                return chamada.proceed();
            } catch (RuntimeException e) {
                if (!conflito(e) || tentativa == TENTATIVAS) throw e;
                LOG.debugf("Conflito de concorrência em %s; tentativa %d", chamada.getMethod().getName(), tentativa + 1);
                Thread.sleep(20L * tentativa);
            }
        }
    }

    static boolean conflito(Throwable e) {
        for (var c = e; c != null; c = c.getCause()) {
            if (c instanceof OptimisticLockException || c instanceof StaleStateException) return true;
        }
        return false;
    }
}
