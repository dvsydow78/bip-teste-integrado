package com.example.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

/**
 * Configuração centralizada de CORS (Cross-Origin Resource Sharing).
 *
 * CORS é um mecanismo de segurança dos navegadores que bloqueia requisições
 * feitas por um frontend hospedado em uma origem diferente da API. Esta classe
 * define quais origens, métodos e cabeçalhos são permitidos para todos os
 * endpoints sob o prefixo "/api/**".
 *
 * A origem permitida é externalizada no application.properties via a property
 * "cors.allowed-origins", possibilitando valores distintos por ambiente
 * sem necessidade de alteração no código:
 *
 *   application.properties (dev):
 *     cors.allowed-origins=http://localhost:4200
 *
 *   application-prod.properties (produção):
 *     cors.allowed-origins=https://bip-teste-integrado.com.br
 *
 * Caso a property não esteja definida, o valor padrão "http://localhost:4200"
 * é utilizado (ambiente de desenvolvimento Angular).
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * Origem permitida para requisições cross-origin.
     * Lida da property "cors.allowed-origins" no application.properties.
     * Valor padrão: http://localhost:4200 (dev Angular).
     */
    @Value("${cors.allowed-origins:http://localhost:4200}")
    private String allowedOrigins;

    /**
     * Registra as regras de CORS para todos os endpoints da API.
     *
     * Configurações aplicadas:
     * - Padrão de URL: /api/** (todos os endpoints da API)
     * - Origens permitidas: definidas via property "cors.allowed-origins"
     * - Métodos permitidos: GET, POST, PUT, DELETE, OPTIONS
     * - Cabeçalhos permitidos: todos (*)
     * - Credenciais: habilitadas (permite envio de cookies e tokens de autenticação)
     * - Cache do preflight: 3600 segundos (1 hora), reduz requisições OPTIONS repetidas
     *
     * @param registry registro de mapeamentos CORS do Spring MVC
     */
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}