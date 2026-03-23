package dao.impl;

import dao.ClientDAO;
import entities.Client;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

public class ClientDAOImpl implements ClientDAO {

    @PersistenceContext(unitName = "EventGestPU")
    private EntityManager em;

    @Override
    public void ajouter(Client c) {
        em.persist(c.getPersonne());
        em.flush();
        c.setId(c.getPersonne().getId());
        em.persist(c);
    }

    @Override
    public void modifier(Client c) {
        if (c.getPersonne() != null) {
            em.merge(c);
            em.flush();
        }
    }

    @Override
    public void supprimer(int id) {
        Client c = trouver(id);
        if (c != null) {
            em.remove(c);
        }
    }

    @Override
    public Client trouver(int id) {
        return em.find(Client.class, id);
    }

    @Override
    public List<Client> lister() {
        return em.createNamedQuery("Client.findAll", Client.class).getResultList();
    }
    @Override
    public List<Client> rechercher(String motCle) {
        if (motCle == null || motCle.trim().isEmpty()) {
            return lister(); // Si la barre est vide, on renvoie tout
        }
        
        // On cherche le mot clé dans le nom, prénom, email ou téléphone (insensible à la casse)
        String query = "SELECT c FROM Client c WHERE LOWER(c.personne.nom) LIKE :motCle "
                     + "OR LOWER(c.personne.prenom) LIKE :motCle "
                     + "OR LOWER(c.personne.email) LIKE :motCle "
                     + "OR c.telephone LIKE :motCle";
                     
        return em.createQuery(query, Client.class)
                 .setParameter("motCle", "%" + motCle.toLowerCase() + "%")
                 .getResultList();
    }
}
