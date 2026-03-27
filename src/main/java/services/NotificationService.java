package services;

import entities.Notification;
import java.util.List;

public interface NotificationService {
    void creer(Notification n);
    void marquerCommeLue(Integer notificationId);
    void marquerToutesCommeLues(Integer clientId);
    List<Notification> listerParClient(Integer clientId);
    List<Notification> listerNonLues(Integer clientId);
    long compterNonLues(Integer clientId);
}
