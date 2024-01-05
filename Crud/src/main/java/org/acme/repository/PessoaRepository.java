package org.acme.repository;

import java.util.List;

import org.acme.model.Pessoas;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class PessoaRepository implements PanacheRepositoryBase<Pessoas, Long> {

    @Inject
    EntityManager em;

    @Transactional
    public List<Pessoas> buscarPorNome(String nome) {
        // Usando a linguagem JPQL para realizar a consulta com case-insensitivity
        return em.createQuery("SELECT p FROM Pessoas p WHERE LOWER(p.nome) LIKE LOWER(:nome)", Pessoas.class)
                .setParameter("nome", "%" + nome + "%")
                .getResultList();
    }
}
