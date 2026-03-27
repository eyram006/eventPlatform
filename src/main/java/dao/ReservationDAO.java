package dao;

import entities.Reservation;
import java.util.List;

public interface ReservationDAO {
    void ajouter(Reservation r);
    void modifier(Reservation r);
    Reservation trouver(Integer id);
    Reservation trouverParNumeroRecu(String numeroRecu);
    List<Reservation> listerParClient(Integer clientId);
    List<Reservation> listerTout();
}
