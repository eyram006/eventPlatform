package services.impl;

import dao.BilletDAO;
import dao.EvenementDAO;
import dao.NotificationDAO;
import dao.ReservationDAO;
import entities.Billet;
import entities.Client;
import entities.Evenement;
import entities.Notification;
import entities.Reservation;
import entities.StatutEvenement;
import entities.StatutReservation;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import services.EvenementService;

@Stateless
public class EvenementServiceImpl implements EvenementService {

    @Inject
    private EvenementDAO dao;

    @Inject
    private BilletDAO billetDAO;

    @Inject
    private ReservationDAO reservationDAO;

    @Inject
    private NotificationDAO notificationDAO;

    @PersistenceContext(unitName = "EventGestPU")
    private EntityManager em;

    @Override
    public void creer(Evenement e) {
        e.setStatut(StatutEvenement.BROUILLON);
        dao.ajouter(e);
    }

    @Override
    public void modifier(Evenement e) {
        dao.modifier(e);
    }

    @Override
    public void supprimer(Integer id) {
        // Charger l'evenement avec ses billets et reservations
        List<Evenement> results = em.createQuery(
            "SELECT DISTINCT e FROM Evenement e " +
            "LEFT JOIN FETCH e.billets b " +
            "LEFT JOIN FETCH b.reservations r " +
            "LEFT JOIN FETCH r.client c " +
            "LEFT JOIN FETCH c.personne " +
            "WHERE e.id = :id", Evenement.class)
            .setParameter("id", id)
            .getResultList();

        if (!results.isEmpty()) {
            Evenement evenement = results.get(0);
            String titreEvenement = evenement.getTitre();
            String organisateurNom = evenement.getOrganisateur() != null 
                ? evenement.getOrganisateur().getPersonne().getNom() + " " + evenement.getOrganisateur().getPersonne().getPrenom()
                : "L'organisateur";

            Set<Integer> clientsNotifies = new HashSet<>();

            // 1. Rembourser et notifier tous les clients qui ont des reservations
            if (evenement.getBillets() != null) {
                for (Billet billet : evenement.getBillets()) {
                    if (billet.getReservations() != null) {
                        for (Reservation reservation : billet.getReservations()) {
                            if (reservation.getStatut() == StatutReservation.CONFIRMEE 
                                || reservation.getStatut() == StatutReservation.EN_ATTENTE) {
                                
                                Client client = reservation.getClient();
                                double montantRembourse = reservation.getMontantTotal();

                                // Notifier le client
                                if (client != null) {
                                    if (!clientsNotifies.contains(client.getId())) {
                                        Notification notification = new Notification();
                                        notification.setClient(client);
                                        notification.setTitre("Evenement supprime : " + titreEvenement);
                                        notification.setMessage(
                                            organisateurNom + " a annule et supprime l'evenement \"" + titreEvenement + "\". "
                                            + "Votre reservation a ete annulee et un remboursement de " 
                                            + montantRembourse + " FCFA a ete effectue. "
                                            + "Nous nous excusons pour le desagrement."
                                        );
                                        notification.setMontantRembourse(montantRembourse);
                                        notification.setDateCreation(new Date());
                                        notification.setLue(false);
                                        notificationDAO.ajouter(notification);

                                        clientsNotifies.add(client.getId());
                                    } else {
                                        // Le client avait plusieurs reservations pour cet evenement
                                        Notification notification = new Notification();
                                        notification.setClient(client);
                                        notification.setTitre("Remboursement supplementaire : " + titreEvenement);
                                        notification.setMessage(
                                            "Un remboursement supplementaire de " + montantRembourse 
                                            + " FCFA a ete effectue suite a la suppression de l'evenement \"" 
                                            + titreEvenement + "\"."
                                        );
                                        notification.setMontantRembourse(montantRembourse);
                                        notification.setDateCreation(new Date());
                                        notification.setLue(false);
                                        notificationDAO.ajouter(notification);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 2. Supprimer l'evenement de la base de donnees via le DAO
            dao.supprimer(id);
        }
    }

    @Override
    public void publier(Integer id) {
        Evenement e = dao.trouver(id);
        if (e != null) {
            e.setStatut(StatutEvenement.PUBLIE);
            dao.modifier(e);
        }
    }

    @Override
    public void annuler(Integer id) {
        Evenement e = dao.trouver(id);
        if (e != null) {
            e.setStatut(StatutEvenement.ANNULE);
            dao.modifier(e);
        }
    }

    @Override
    public void retirerEvenement(Integer id) {
        this.supprimer(id); // La suppression s'occupe de notifier et rembourser
    }

    @Override
    public Evenement trouver(Integer id) {
        return dao.trouver(id);
    }

    @Override
    public List<Evenement> listerTout() {
        return dao.listerTout();
    }

    @Override
    public List<Evenement> listerPublies() {
        return dao.listerPublies();
    }

    @Override
    public List<Evenement> listerParOrganisateur(Integer organisateurId) {
        return dao.listerParOrganisateur(organisateurId);
    }

    @Override
    public List<Evenement> rechercher(String motCle) {
        return dao.rechercher(motCle);
    }
}
