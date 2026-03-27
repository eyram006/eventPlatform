package dao.impl;

import dao.EvenementDAO;
import entities.Evenement;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class EvenementDAOImpl implements EvenementDAO {

    @PersistenceContext(unitName = "EventGestPU")
    private EntityManager em;

    @Override
    public void ajouter(Evenement e) {
        em.persist(e);
        em.flush();
    }

    @Override
    public void modifier(Evenement e) {
        em.merge(e);
        em.flush();
    }

    @Override
    public void supprimer(Integer id) {
        // Charger l'evenement avec ses billets et reservations pour que la cascade fonctionne
        List<Evenement> results = em.createQuery(
            "SELECT DISTINCT e FROM Evenement e " +
            "LEFT JOIN FETCH e.billets b " +
            "LEFT JOIN FETCH b.reservations " +
            "WHERE e.id = :id", Evenement.class)
            .setParameter("id", id)
            .getResultList();
        if (!results.isEmpty()) {
            Evenement e = results.get(0);
            em.remove(em.contains(e) ? e : em.merge(e));
            em.flush();
        }
    }

    @Override
    public Evenement trouver(Integer id) {
        return em.find(Evenement.class, id);
    }

    @Override
    public List<Evenement> listerTout() {
        return em.createNamedQuery("Evenement.findAll", Evenement.class).getResultList();
    }

    @Override
    public List<Evenement> listerPublies() {
        return em.createNamedQuery("Evenement.findPublies", Evenement.class).getResultList();
    }

    @Override
    public List<Evenement> listerParOrganisateur(Integer organisateurId) {
        return em.createNamedQuery("Evenement.findByOrganisateur", Evenement.class)
                 .setParameter("organisateurId", organisateurId)
                 .getResultList();
    }

    @Override
    public List<Evenement> rechercher(String motCle) {
        if (motCle == null || motCle.trim().isEmpty()) {
            return listerTout();
        }
        String query = "SELECT e FROM Evenement e WHERE LOWER(e.titre) LIKE :motCle "
                     + "OR LOWER(e.lieu) LIKE :motCle "
                     + "OR LOWER(e.description) LIKE :motCle";
        return em.createQuery(query, Evenement.class)
                 .setParameter("motCle", "%" + motCle.toLowerCase() + "%")
                 .getResultList();
    }
}
