package com.example.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração da documentação OpenAPI (Swagger) da aplicação.
 *
 * Esta classe personaliza as informações exibidas na interface do Swagger UI,
 * acessível em: http://localhost:8080/swagger-ui/index.html
 *
 * A documentação é gerada automaticamente pelo springdoc-openapi a partir das
 * anotações presentes nos controllers (@Operation, @ApiResponse, @Tag, etc.),
 * combinadas com os metadados definidos aqui.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Define as informações gerais da API exibidas no cabeçalho do Swagger UI.
     *
     * @return instância de OpenAPI com título, versão e descrição configurados
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BIP Teste Integrado API")
                        .version("1.0")
                        .description("API do módulo Backend"));
    }
}