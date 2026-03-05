package com.example.backend.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO de requisição para transferência de valor entre benefícios.
 * <p>
 * Representa os dados necessários para executar uma transferência,
 * contendo o identificador do benefício de origem, destino e o valor a ser transferido.
 * </p>
 *
 * Regras de validação:
 * <ul>
 *     <li>fromId é obrigatório</li>
 *     <li>toId é obrigatório</li>
 *     <li>amount é obrigatório, positivo e com no máximo 15 dígitos inteiros e 2 decimais</li>
 * </ul>
 */
public record TransferenciaRequest(

    /**
     * Identificador do benefício de origem da transferência.
     */
    @NotNull(message = "ID de origem é obrigatório")
    Long fromId,

    /**
     * Identificador do benefício de destino da transferência.
     */
    @NotNull(message = "ID de destino é obrigatório")
    Long toId,

    /**
     * Valor monetário a ser transferido.
     * Deve ser positivo e respeitar a precisão decimal definida.
     */
    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser positivo")
    @Digits(integer = 15, fraction = 2)
    BigDecimal amount
) {}