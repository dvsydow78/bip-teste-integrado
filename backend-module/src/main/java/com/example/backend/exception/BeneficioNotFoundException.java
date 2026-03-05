package com.example.backend.exception;

/**
 * Exceção lançada quando um benefício não é encontrado.
 * <p>
 * Utilizada para indicar que a operação solicitada não pôde ser concluída
 * porque o benefício com o identificador informado não existe no sistema.
 * </p>
 */
public class BeneficioNotFoundException extends RuntimeException {

    /**
     * Constrói a exceção com uma mensagem contendo o ID do benefício não encontrado.
     *
     * @param id identificador do benefício que não foi localizado
     */
    public BeneficioNotFoundException(Long id) {
        super("Benefício não encontrado: id=" + id);
    }
}