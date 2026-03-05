package com.example.ejb.exception;

/**
 * Exceção lançada quando ocorre conflito de concorrência durante a transferência.
 * <p>
 * Geralmente ocorre em cenários de controle de concorrência otimista, quando
 * múltiplas transações tentam modificar o mesmo registro simultaneamente.
 * </p>
 */
public class TransferConflictException extends TransferException {

    /**
     * Constrói a exceção com uma mensagem descritiva.
     *
     * @param message mensagem de erro
     */
    public TransferConflictException(String message) {
        super(message);
    }

    /**
     * Constrói a exceção com mensagem e causa raiz da exceção.
     *
     * @param message mensagem de erro
     * @param cause exceção original que provocou o erro
     */
    public TransferConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
