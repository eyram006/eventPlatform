package dao.impl;

import dao.ReservationDAO;
import entities.Reservation;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class ReservationDAOImpl implements ReservationDAO {

    @PersistenceContext(unitName = "EventGestPU")
    private EntityManager em;

    @Override
    public void ajouter(Reservation r) {
        em.persist(r);
        em.flush();
    }

    @Override
    public void modifier(Reservation r) {
        em.merge(r);
        em.flush();
    }

    @Override
    public Reservation trouver(Integer id) {
        return em.find(Reservation.class, id);
    }

    @Override
    public Reservation trouverParNumeroRecu(String numeroRecu) {
        try {
            return em.createNamedQuery("Reservation.findByNumeroRecu", Reservation.class)
                     .setParameter("numeroRecu", numeroRecu)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Reservation> listerParClient(Integer clientId) {
        return em.createNamedQuery("Reservation.findByClient", Reservation.class)
                 .setParameter("clientId", clientId)
                 .getResultList();
    }

    @Override
    public List<Reservation> listerTout() {
        return em.createNamedQuery("Reservation.findAll", Reservation.class).getResultList();
    }
}
