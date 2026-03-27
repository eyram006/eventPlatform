package services.impl;

import dao.NotificationDAO;
import entities.Notification;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.List;
import services.NotificationService;

@Stateless
public class NotificationServiceImpl implements NotificationService {

    @Inject
    private NotificationDAO dao;

    @Override
    public void creer(Notification n) {
        dao.ajouter(n);
    }

    @Override
    public void marquerCommeLue(Integer notificationId) {
        // Simple approach: fetch all notifications and find the one
        // In a real app you'd add a trouver method to DAO
    }

    @Override
    public void marquerToutesCommeLues(Integer clientId) {
        List<Notification> nonLues = dao.listerNonLuesParClient(clientId);
        for (Notification n : nonLues) {
            n.setLue(true);
            dao.modifier(n);
        }
    }

    @Override
    public List<Notification> listerParClient(Integer clientId) {
        return dao.listerParClient(clientId);
    }

    @Override
    public List<Notification> listerNonLues(Integer clientId) {
        return dao.listerNonLuesParClient(clientId);
    }

    @Override
    public long compterNonLues(Integer clientId) {
        return dao.compterNonLues(clientId);
    }
}
