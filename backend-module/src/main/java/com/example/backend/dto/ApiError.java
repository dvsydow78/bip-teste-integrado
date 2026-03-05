package com.example.backend.dto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO que representa o formato padrão de erro da API.
 * <p>
 * Utilizado para retornar informações estruturadas sobre exceções ocorridas
 * durante a execução das requisições.
 * </p>
 *
 * Campos:
 * <ul>
 *     <li>timestamp - data e hora em que o erro ocorreu</li>
 *     <li>status - código HTTP do erro</li>
 *     <li>erro - nome ou tipo do erro</li>
 *     <li>mensagem - mensagem descritiva do erro</li>
 *     <li>path - endpoint onde o erro ocorreu</li>
 *     <li>detalhes - informações adicionais sobre o erro, quando disponíveis</li>
 * </ul>
 */
public record ApiError(
        LocalDateTime timestamp,
        int status,
        String erro,
        String mensagem,
        String path,
        Map<String, Object> detalhes
) {}