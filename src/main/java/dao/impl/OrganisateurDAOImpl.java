/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.impl;

import dao.OrganisateurDAO;
import entities.Organisateur;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

/**
 *
 * @author Chari
 */
@Stateless
public class OrganisateurDAOImpl implements OrganisateurDAO {

    @PersistenceContext(unitName = "EventGestPU")
    private EntityManager em;

    @Override
    public void ajouter(Organisateur o) {
        if (o.getPersonne().getId() == null) {
            em.persist(o.getPersonne());
            em.flush();
            o.setId(o.getPersonne().getId());
        }
        em.persist(o);
    }

    @Override
    public void modifier(Organisateur o) {
        if (o.getPersonne() != null) {
            em.merge(o.getPersonne());
        }
        em.merge(o);
        em.flush();
    }

    @Override
    public void supprimer(Integer id) {
        Organisateur o = trouver(id);
        if (o != null) {
            em.remove(o);
        }
    }

    @Override
    public Organisateur trouver(Integer id) {
        return em.find(Organisateur.class, id);
    }

    @Override
    public List<Organisateur> listerTout() {
        return em.createNamedQuery("Organisateur.findAll", Organisateur.class).getResultList();
    }
    
    @Override
    public List<Organisateur> rechercher(String motCle) {
        if (motCle == null || motCle.trim().isEmpty()) {
            return listerTout(); 
        }
        
        String query = "SELECT o FROM Organisateur o WHERE LOWER(o.personne.nom) LIKE :motCle "
                     + "OR LOWER(o.personne.prenom) LIKE :motCle "
                     + "OR LOWER(o.personne.email) LIKE :motCle "
                     + "OR LOWER(o.organisation) LIKE :motCle";
                     
        return em.createQuery(query, Organisateur.class)
                 .setParameter("motCle", "%" + motCle.toLowerCase() + "%")
                 .getResultList();
    }
}
