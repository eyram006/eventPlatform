package dao.impl;

import dao.BilletDAO;
import entities.Billet;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class BilletDAOImpl implements BilletDAO {

    @PersistenceContext(unitName = "EventGestPU")
    private EntityManager em;

    @Override
    public void ajouter(Billet b) {
        em.persist(b);
        em.flush();
    }

    @Override
    public void modifier(Billet b) {
        em.merge(b);
        em.flush();
    }

    @Override
    public void supprimer(Integer id) {
        // Charger le billet avec ses reservations pour que la cascade fonctionne
        List<Billet> results = em.createQuery(
            "SELECT b FROM Billet b LEFT JOIN FETCH b.reservations WHERE b.id = :id", Billet.class)
            .setParameter("id", id)
            .getResultList();
        if (!results.isEmpty()) {
            Billet b = results.get(0);
            // Detacher le billet de son evenement pour eviter la contrainte
            if (b.getEvenement() != null) {
                b.getEvenement().getBillets().remove(b);
            }
            em.remove(em.contains(b) ? b : em.merge(b));
            em.flush();
        }
    }

    @Override
    public Billet trouver(Integer id) {
        return em.find(Billet.class, id);
    }

    @Override
    public List<Billet> listerParEvenement(Integer evenementId) {
        return em.createNamedQuery("Billet.findByEvenement", Billet.class)
                 .setParameter("evenementId", evenementId)
                 .getResultList();
    }

    @Override
    public List<Billet> listerDisponibles(Integer evenementId) {
        return em.createNamedQuery("Billet.findDisponibles", Billet.class)
                 .setParameter("evenementId", evenementId)
                 .getResultList();
    }
}
