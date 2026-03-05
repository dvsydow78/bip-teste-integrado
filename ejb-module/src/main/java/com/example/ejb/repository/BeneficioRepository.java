package com.example.ejb.repository;

import com.example.ejb.model.Beneficio;
import jakarta.ejb.Stateless;
import jakarta.persistence.*;

import java.util.List;
import java.util.Optional;

/**
 * Repositório EJB — acesso a dados via EntityManager puro.
 * Usado pelo EjbService e, via Spring Bridge, pelo backend-module.
 */
@Stateless
public class BeneficioRepository {

    @PersistenceContext
    private EntityManager em;

    public void setEm(EntityManager em) {
        this.em = em;
    }

    // Busca simples
    public Optional<Beneficio> findById(Long id) {
        return Optional.ofNullable(em.find(Beneficio.class, id));
    }

    // Busca com lock pessimista (para transferência)
    public Optional<Beneficio> findByIdForUpdate(Long id) {
        Beneficio b = em.find(Beneficio.class, id, LockModeType.PESSIMISTIC_WRITE);
        return Optional.ofNullable(b);
    }

    // Lista todos
    public List<Beneficio> findAll() {
        return em.createQuery("SELECT b FROM Beneficio b", Beneficio.class)
                 .getResultList();
    }

    // Lista apenas ativos
    public List<Beneficio> findAtivos() {
        return em.createQuery(
                "SELECT b FROM Beneficio b WHERE b.ativo = true", Beneficio.class)
                 .getResultList();
    }

    // Persistir novo
    public Beneficio save(Beneficio b) {
        if (b.getId() == null) {
            em.persist(b);
            return b;
        }
        return em.merge(b);
    }

    
}