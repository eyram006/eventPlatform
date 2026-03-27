package dao.impl;

import dao.NotificationDAO;
import entities.Notification;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class NotificationDAOImpl implements NotificationDAO {

    @PersistenceContext(unitName = "EventGestPU")
    private EntityManager em;

    @Override
    public void ajouter(Notification n) {
        em.persist(n);
        em.flush();
    }

    @Override
    public void modifier(Notification n) {
        em.merge(n);
        em.flush();
    }

    @Override
    public List<Notification> listerParClient(Integer clientId) {
        return em.createNamedQuery("Notification.findByClient", Notification.class)
                 .setParameter("clientId", clientId)
                 .getResultList();
    }

    @Override
    public List<Notification> listerNonLuesParClient(Integer clientId) {
        return em.createNamedQuery("Notification.findNonLuesByClient", Notification.class)
                 .setParameter("clientId", clientId)
                 .getResultList();
    }

    @Override
    public long compterNonLues(Integer clientId) {
        return em.createNamedQuery("Notification.countNonLuesByClient", Long.class)
                 .setParameter("clientId", clientId)
                 .getSingleResult();
    }
}
