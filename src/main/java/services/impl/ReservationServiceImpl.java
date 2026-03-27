package services.impl;

import dao.BilletDAO;
import dao.ReservationDAO;
import entities.Billet;
import entities.Client;
import entities.Reservation;
import entities.StatutReservation;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;
import services.ReservationService;

@Stateless
public class ReservationServiceImpl implements ReservationService {

    @Inject
    private ReservationDAO reservationDAO;

    @Inject
    private BilletDAO billetDAO;

    @PersistenceContext(unitName = "EventGestPU")
    private EntityManager em;

    @Override
    public Reservation acheterBillet(Integer clientId, Integer billetId, Integer quantite) {
        Billet billet = billetDAO.trouver(billetId);
        if (billet == null) {
            throw new RuntimeException("Billet introuvable.");
        }
        if (billet.getNbPlacesRestantes() < quantite) {
            throw new RuntimeException("Pas assez de places disponibles. Restantes : " + billet.getNbPlacesRestantes());
        }

        Client client = em.find(Client.class, clientId);
        if (client == null) {
            throw new RuntimeException("Client introuvable.");
        }

        // Décrémenter les places
        billet.setNbPlacesRestantes(billet.getNbPlacesRestantes() - quantite);
        billetDAO.modifier(billet);

        // Créer la réservation
        Reservation reservation = new Reservation();
        reservation.setClient(client);
        reservation.setBillet(billet);
        reservation.setQuantite(quantite);
        reservation.setMontantTotal(billet.getPrix() * quantite);
        reservation.setDateReservation(new Date());
        reservation.setStatut(StatutReservation.CONFIRMEE);

        reservationDAO.ajouter(reservation);

        return reservation;
    }

    @Override
    public Reservation trouver(Integer id) {
        return reservationDAO.trouver(id);
    }

    @Override
    public Reservation trouverParNumeroRecu(String numeroRecu) {
        return reservationDAO.trouverParNumeroRecu(numeroRecu);
    }

    @Override
    public List<Reservation> listerParClient(Integer clientId) {
        return reservationDAO.listerParClient(clientId);
    }

    @Override
    public List<Reservation> listerTout() {
        return reservationDAO.listerTout();
    }
}
