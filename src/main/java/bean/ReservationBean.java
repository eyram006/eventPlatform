package bean;

import entities.Billet;
import entities.Client;
import entities.Evenement;
import entities.Reservation;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import services.BilletService;
import services.ClientService;
import services.EvenementService;
import services.ReservationService;

@Named("reservationBean")
@ViewScoped
public class ReservationBean implements Serializable {

    @Inject
    private EvenementService evenementService;

    @Inject
    private BilletService billetService;

    @Inject
    private ReservationService reservationService;

    @Inject
    private ClientService clientService;

    private List<Evenement> evenementsPublies;
    private List<Client> listeClients;
    private List<Billet> billetsDisponibles;
    private List<Reservation> mesReservations;

    private Integer evenementId;
    private Integer billetId;
    private Integer clientId;
    private Integer quantite = 1;

    private Reservation derniereReservation;
    private boolean afficherRecu = false;

    // Pour recherche de reçu
    private String numeroRecuRecherche;

    @PostConstruct
    public void init() {
        evenementsPublies = evenementService.listerPublies();
        listeClients = clientService.lister();
    }

    public void chargerBillets() {
        if (evenementId != null) {
            billetsDisponibles = billetService.listerDisponibles(evenementId);
        } else {
            billetsDisponibles = null;
        }
        billetId = null;
    }

    public void acheter() {
        try {
            if (clientId == null || billetId == null || quantite == null || quantite < 1) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Veuillez remplir tous les champs."));
                return;
            }

            derniereReservation = reservationService.acheterBillet(clientId, billetId, quantite);
            afficherRecu = true;

            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Succès",
                    "Paiement confirmé ! Votre numéro de reçu : " + derniereReservation.getNumeroRecu()));

            // Rafraîchir les données
            evenementsPublies = evenementService.listerPublies();
            if (evenementId != null) {
                billetsDisponibles = billetService.listerDisponibles(evenementId);
            }

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", e.getMessage()));
            afficherRecu = false;
        }
    }

    public void rechercherRecu() {
        if (numeroRecuRecherche != null && !numeroRecuRecherche.trim().isEmpty()) {
            derniereReservation = reservationService.trouverParNumeroRecu(numeroRecuRecherche.trim());
            if (derniereReservation != null) {
                afficherRecu = true;
            } else {
                afficherRecu = false;
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Introuvable", "Aucun reçu trouvé avec ce numéro."));
            }
        }
    }

    public void chargerMesReservations() {
        if (clientId != null) {
            mesReservations = reservationService.listerParClient(clientId);
        }
    }

    public void fermerRecu() {
        afficherRecu = false;
        derniereReservation = null;
    }

    // --- Getters & Setters ---

    public List<Evenement> getEvenementsPublies() { return evenementsPublies; }

    public List<Client> getListeClients() { return listeClients; }

    public List<Billet> getBilletsDisponibles() { return billetsDisponibles; }

    public List<Reservation> getMesReservations() { return mesReservations; }

    public Integer getEvenementId() { return evenementId; }
    public void setEvenementId(Integer evenementId) { this.evenementId = evenementId; }

    public Integer getBilletId() { return billetId; }
    public void setBilletId(Integer billetId) { this.billetId = billetId; }

    public Integer getClientId() { return clientId; }
    public void setClientId(Integer clientId) { this.clientId = clientId; }

    public Integer getQuantite() { return quantite; }
    public void setQuantite(Integer quantite) { this.quantite = quantite; }

    public Reservation getDerniereReservation() { return derniereReservation; }

    public boolean isAfficherRecu() { return afficherRecu; }

    public String getNumeroRecuRecherche() { return numeroRecuRecherche; }
    public void setNumeroRecuRecherche(String n) { this.numeroRecuRecherche = n; }
}
