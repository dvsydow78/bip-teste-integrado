package com.example.ejb.exception;

/**
 * Exceção base para erros relacionados às regras de negócio de transferência.
 * <p>
 * Utilizada para representar violações de regras aplicáveis ao processo de
 * transferência, permitindo tratamento específico das exceções de domínio.
 * </p>
 */
public class TransferException extends RuntimeException {

    /**
     * Constrói a exceção com uma mensagem descritiva.
     *
     * @param message mensagem de erro
     */
    public TransferException(String message) {
        super(message);
    }

    /**
     * Constrói a exceção com mensagem e causa raiz da exceção.
     *
     * @param message mensagem de erro
     * @param cause exceção original que provocou a falha
     */
    public TransferException(String message, Throwable cause) {
        super(message, cause);
    }
}
