package dao;

import entities.Notification;
import java.util.List;

public interface NotificationDAO {
    void ajouter(Notification n);
    void modifier(Notification n);
    List<Notification> listerParClient(Integer clientId);
    List<Notification> listerNonLuesParClient(Integer clientId);
    long compterNonLues(Integer clientId);
}
