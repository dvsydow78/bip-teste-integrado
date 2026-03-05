package com.example.backend.config;

import com.example.ejb.repository.BeneficioRepository;
import com.example.ejb.service.BeneficioEjbService;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.support.SharedEntityManagerBean;

/**
 * Registra os beans do ejb-module no contexto Spring.
 *
 * Por que usar SharedEntityManagerBean?
 * O EntityManager não é thread-safe — injetá-lo diretamente via @PersistenceContext
 * em uma classe @Configuration pode causar problemas em cenários concorrentes.
 * O SharedEntityManagerBean cria um proxy thread-safe gerenciado pelo Spring,
 * equivalente ao comportamento do @PersistenceContext em beans de escopo correto.
 *
 * Por que usar "new" + setter para criar os beans?
 * O ejb-module não utiliza anotações do Spring (@Component, @Service, etc.),
 * portanto suas classes não podem ser detectadas pelo component scan automático.
 * A criação manual via @Bean é a forma correta de integrá-las ao contexto Spring.
 * Os beans resultantes SÃO gerenciados pelo Spring e recebem proxies AOP normalmente,
 * incluindo suporte a @Transactional.
 */
@Configuration
public class EjbServiceConfig {

    /**
     * Cria um proxy thread-safe do EntityManager a partir da EntityManagerFactory.
     * Utilizado pelos repositórios que necessitam de acesso ao contexto de persistência.
     *
     * @param emf fábrica de EntityManager fornecida pelo Spring
     * @return bean do EntityManager compartilhado e thread-safe
     */
    @Bean
    public SharedEntityManagerBean entityManagerBean(EntityManagerFactory emf) {
        SharedEntityManagerBean bean = new SharedEntityManagerBean();
        bean.setEntityManagerFactory(emf);
        return bean;
    }

    /**
     * Cria e configura o repositório de benefícios do ejb-module,
     * injetando o EntityManager compartilhado para acesso ao banco de dados.
     *
     * @param emBean bean do EntityManager compartilhado
     * @return instância configurada de BeneficioRepository
     * @throws Exception se a inicialização do EntityManager falhar
     */
    @Bean
    public BeneficioRepository beneficioRepository(SharedEntityManagerBean emBean) throws Exception {
        emBean.afterPropertiesSet();
        BeneficioRepository repo = new BeneficioRepository();
        repo.setEm(emBean.getObject());
        return repo;
    }

    /**
     * Cria e configura o serviço de benefícios do ejb-module,
     * injetando o repositório necessário para as operações de negócio.
     *
     * @param beneficioRepository repositório de benefícios já configurado
     * @return instância configurada de BeneficioEjbService
     */
    @Bean
    public BeneficioEjbService beneficioEjbService(BeneficioRepository beneficioRepository) {
        BeneficioEjbService svc = new BeneficioEjbService();
        svc.setRepository(beneficioRepository);
        return svc;
    }
}