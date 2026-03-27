package services;

import entities.Reservation;
import java.util.List;

public interface ReservationService {
    Reservation acheterBillet(Integer clientId, Integer billetId, Integer quantite);
    Reservation trouver(Integer id);
    Reservation trouverParNumeroRecu(String numeroRecu);
    List<Reservation> listerParClient(Integer clientId);
    List<Reservation> listerTout();
}
