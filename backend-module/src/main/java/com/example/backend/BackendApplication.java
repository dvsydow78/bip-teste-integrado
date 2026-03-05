package com.example.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 * Classe principal da aplicação Backend.
 * <p>
 * Responsável por inicializar o contexto do Spring Boot e configurar o
 * scan de entidades JPA utilizadas pela aplicação, incluindo os modelos
 * definidos no módulo EJB.
 * </p>
 *
 * Configurações importantes:
 * <ul>
 *     <li>Spring Boot auto configuration habilitado</li>
 *     <li>Escaneamento de entidades JPA nos pacotes backend e ejb-module</li>
 * </ul>
 */
@SpringBootApplication
// Escaneia entidades JPA do módulo EJB e do próprio backend
@EntityScan(basePackages = {"com.example.ejb.model", "com.example.backend"})
public class BackendApplication {

    /**
     * Método de inicialização da aplicação.
     *
     * @param args argumentos da linha de comando
     */
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}
