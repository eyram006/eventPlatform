/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.impl;

import dao.GerantDAO;
import entities.Gerant;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

/**
 *
 * @author Chari
 */

@Stateless
public class GerantDAOImpl implements GerantDAO {

    @PersistenceContext(unitName = "EventGestPU")
    private EntityManager em;

    @Override
    public void ajouter(Gerant gerant) {
        em.persist(gerant.getPersonne());
        em.flush();
        gerant.setId(gerant.getPersonne().getId()); 
        em.persist(gerant);
    }

    @Override
    public void modifier(Gerant gerant) {
        if (gerant.getPersonne() != null) {
        em.merge(gerant);
        em.flush(); 
    }
    }

    @Override
    public void supprimer(Integer id) {
        Gerant g = trouver(id);
        if (g != null) {
            em.remove(g);
        }
    }

    @Override
    public Gerant trouver(Integer id) {
        return em.find(Gerant.class, id);
    }

    @Override
    public List<Gerant> lister() {
        return em.createNamedQuery("Gerant.findAll", Gerant.class).getResultList();
    }
    @Override
    public List<Gerant> rechercher(String motCle) {
        if (motCle == null || motCle.trim().isEmpty()) {
            return lister();
        }
        
        String query = "SELECT g FROM Gerant g WHERE LOWER(g.personne.nom) LIKE :motCle "
                     + "OR LOWER(g.personne.prenom) LIKE :motCle "
                     + "OR LOWER(g.personne.email) LIKE :motCle "
                     + "OR LOWER(g.role) LIKE :motCle";
                     
        return em.createQuery(query, Gerant.class)
                 .setParameter("motCle", "%" + motCle.toLowerCase() + "%")
                 .getResultList();
    }
}