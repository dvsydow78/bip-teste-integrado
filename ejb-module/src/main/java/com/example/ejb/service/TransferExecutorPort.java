package com.example.ejb.service;

import java.math.BigDecimal;

/**
 * Porta de execução da operação de transferência.
 * <p>
 * Define o contrato para execução de transferências de valor entre duas
 * entidades, permitindo a aplicação de arquitetura hexagonal ou DDD,
 * desacoplando a regra de negócio da implementação concreta.
 * </p>
 */
public interface TransferExecutorPort {

    /**
     * Executa a transferência de valor entre origem e destino.
     *
     * @param fromId identificador da origem
     * @param toId identificador do destino
     * @param amount valor a ser transferido
     */
    void execute(Long fromId, Long toId, BigDecimal amount);
}