package com.example.backend.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO de requisição para criação ou atualização de benefícios.
 * <p>
 * Contém os dados necessários para o cadastro e alteração de um benefício,
 * incluindo validações de entrada para garantir a integridade dos dados.
 * </p>
 *
 * Regras de validação:
 * <ul>
 *     <li>Nome é obrigatório e possui limite de 100 caracteres</li>
 *     <li>Descrição é opcional, com limite de 255 caracteres</li>
 *     <li>Valor é obrigatório e deve ser maior ou igual a 0.01</li>
 * </ul>
 */
public record BeneficioRequest(

    /**
     * Nome do benefício.
     * Campo obrigatório e limitado a 100 caracteres.
     */
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    String nome,

    /**
     * Descrição opcional do benefício.
     * Limite máximo de 255 caracteres.
     */
    @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres")
    String descricao,

    /**
     * Valor monetário do benefício.
     * Campo obrigatório e deve ser maior ou igual a 0.01.
     */
    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser positivo")
    BigDecimal valor
) {}