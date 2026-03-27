package bean;

import entities.Billet;
import entities.Client;
import entities.Evenement;
import entities.Notification;
import entities.Personne;
import entities.Reservation;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import services.BilletService;
import services.ClientService;
import services.EvenementService;
import services.NotificationService;
import services.ReservationService;

@Named("clientBean")
@ViewScoped
public class ClientBean implements Serializable {
    
    @Inject
    private ClientService service;

    @Inject
    private EvenementService evenementService;

    @Inject
    private BilletService billetService;

    @Inject
    private ReservationService reservationService;

    @Inject
    private NotificationService notificationService;

    private Client client;
    private List<Client> listeClients;
    private String motCle;

    // Espace client
    private Client clientSelectionne;
    private List<Evenement> evenementsPublies;
    private List<Billet> billetsDisponibles;
    private List<Reservation> reservationsClient;
    
    private Integer evenementId;
    private Integer billetId;
    private Integer quantite = 1;

    private Reservation derniereReservation;
    private boolean afficherRecu = false;

    // Notifications
    private List<Notification> notificationsClient;
    private long nbNotificationsNonLues = 0;

    @PostConstruct
    public void init() {
        preparerNouveau();
        chargerClients();
    }

    public void preparerNouveau() {
        client = new Client();
        client.setPersonne(new Personne()); 
    }

    public void chargerClients() {
        listeClients = service.lister();
    }
    
    public void rechercherClients() {
        if (motCle == null || motCle.trim().isEmpty()) {
            chargerClients();
        } else {
            listeClients = service.rechercher(motCle);
        }
    }
    
    public void ajouter() {
        try {
            if (client.getId() == null) {
                service.ajouter(client);
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Succes", "Client enregistre avec succes."));
            } else {
                service.modifier(client);
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Succes", "Client mis a jour."));
            }
            preparerNouveau();
            this.motCle = null; 
            chargerClients();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Erreur : " + e.getMessage()));
        }
    }

    public void supprimer(int id) {
        try {
            service.supprimer(id);
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Succes", "Client supprime."));
            if (clientSelectionne != null && clientSelectionne.getId() != null && clientSelectionne.getId().equals(id)) {
                fermerEspaceClient();
            }
            rechercherClients(); 
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Impossible de supprimer."));
        }
    }
    
    public void modifier(Client c) {
       this.client = c;
       if (this.client.getPersonne() == null) {
           this.client.setPersonne(new Personne());
       }
    }

    
    // ESPACE CLIENT
  

    public void ouvrirEspaceClient(Client c) {
        this.clientSelectionne = c;
        this.evenementsPublies = evenementService.listerPublies();
        this.reservationsClient = reservationService.listerParClient(c.getId());
        this.billetsDisponibles = null;
        this.evenementId = null;
        this.billetId = null;
        this.quantite = 1;
        this.afficherRecu = false;
        this.derniereReservation = null;
        // Charger les notifications
        this.notificationsClient = notificationService.listerParClient(c.getId());
        this.nbNotificationsNonLues = notificationService.compterNonLues(c.getId());
    }

    public void fermerEspaceClient() {
        this.clientSelectionne = null;
        this.evenementsPublies = null;
        this.reservationsClient = null;
        this.billetsDisponibles = null;
        this.afficherRecu = false;
        this.derniereReservation = null;
        this.notificationsClient = null;
        this.nbNotificationsNonLues = 0;
    }

    public void chargerBillets() {
        if (evenementId != null) {
            billetsDisponibles = billetService.listerDisponibles(evenementId);
        } else {
            billetsDisponibles = null;
        }
        billetId = null;
    }

    public void acheterBillet() {
        try {
            if (clientSelectionne == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Aucun client selectionne."));
                return;
            }
            if (evenementId == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Veuillez selectionner un evenement."));
                return;
            }
            if (billetId == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Veuillez selectionner un type de billet."));
                return;
            }
            if (quantite == null || quantite < 1) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "La quantite doit etre au moins 1."));
                return;
            }

            derniereReservation = reservationService.acheterBillet(clientSelectionne.getId(), billetId, quantite);
            afficherRecu = true;

            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Succes",
                    "Paiement confirme ! Recu : " + derniereReservation.getNumeroRecu()));

            // Rafraichir
            evenementsPublies = evenementService.listerPublies();
            reservationsClient = reservationService.listerParClient(clientSelectionne.getId());
            if (evenementId != null) {
                billetsDisponibles = billetService.listerDisponibles(evenementId);
            }

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", e.getMessage()));
            afficherRecu = false;
        }
    }

    public void voirRecu(Reservation r) {
        this.derniereReservation = r;
        this.afficherRecu = true;
    }

    public void fermerRecu() {
        this.afficherRecu = false;
        this.derniereReservation = null;
    }

    // =============================================
    // SELECTITEMS pour les menus deroulants JSF
    // =============================================

    public List<SelectItem> getEvenementItems() {
        List<SelectItem> items = new ArrayList<>();
        if (evenementsPublies != null) {
            for (Evenement ev : evenementsPublies) {
                items.add(new SelectItem(ev.getId(), ev.getTitre() + " (" + ev.getPlacesRestantes() + " places)"));
            }
        }
        return items;
    }

    public List<SelectItem> getBilletItems() {
        List<SelectItem> items = new ArrayList<>();
        if (billetsDisponibles != null) {
            for (Billet b : billetsDisponibles) {
                items.add(new SelectItem(b.getId(), b.getTypeBillet() + " - " + b.getPrix() + " FCFA (" + b.getNbPlacesRestantes() + " dispo)"));
            }
        }
        return items;
    }

    // =============================================
    // GETTERS & SETTERS
    // =============================================

    public List<Client> getListe() { return listeClients; }
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    public String getMotCle() { return motCle; }
    public void setMotCle(String motCle) { this.motCle = motCle; }

    public Client getClientSelectionne() { return clientSelectionne; }
    public List<Evenement> getEvenementsPublies() { return evenementsPublies; }
    public List<Billet> getBilletsDisponibles() { return billetsDisponibles; }
    public List<Reservation> getReservationsClient() { return reservationsClient; }

    public Integer getEvenementId() { return evenementId; }
    public void setEvenementId(Integer id) { this.evenementId = id; }
    public Integer getBilletId() { return billetId; }
    public void setBilletId(Integer id) { this.billetId = id; }
    public Integer getQuantite() { return quantite; }
    public void setQuantite(Integer q) { this.quantite = q; }

    public Reservation getDerniereReservation() { return derniereReservation; }
    public boolean isAfficherRecu() { return afficherRecu; }

    // --- Notifications ---

    public void marquerNotificationsLues() {
        if (clientSelectionne != null) {
            notificationService.marquerToutesCommeLues(clientSelectionne.getId());
            this.notificationsClient = notificationService.listerParClient(clientSelectionne.getId());
            this.nbNotificationsNonLues = 0;
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Toutes les notifications ont ete marquees comme lues."));
        }
    }

    public List<Notification> getNotificationsClient() { return notificationsClient; }
    public long getNbNotificationsNonLues() { return nbNotificationsNonLues; }
}