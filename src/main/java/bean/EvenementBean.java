package bean;

import entities.Billet;
import entities.Evenement;
import entities.Organisateur;
import entities.StatutEvenement;
import entities.TypeBillet;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import services.BilletService;
import services.EvenementService;
import services.OrganisateurService;

@Named("evenementBean")
@ViewScoped
public class EvenementBean implements Serializable {

    @Inject
    private EvenementService evenementService;

    @Inject
    private BilletService billetService;

    @Inject
    private OrganisateurService organisateurService;

    private Evenement evenement;
    private List<Evenement> listeEvenements;
    private List<Organisateur> listeOrganisateurs;
    private Integer organisateurId;
    private String motCle;

    // Pour l'ajout de billets
    private Billet nouveauBillet;
    private Evenement evenementSelectionne;
    private List<Billet> billetsEvenement;

    @PostConstruct
    public void init() {
        preparerNouveau();
        chargerEvenements();
        listeOrganisateurs = organisateurService.toutAfficher();
    }

    public void preparerNouveau() {
        evenement = new Evenement();
        evenement.setStatut(StatutEvenement.BROUILLON);
        nouveauBillet = new Billet();
    }

    public void chargerEvenements() {
        listeEvenements = evenementService.listerTout();
    }

    public void rechercher() {
        listeEvenements = evenementService.rechercher(motCle);
    }

    public void enregistrer() {
        try {
            Organisateur org = null;
            // Chercher l'organisateur via le service
            for (Organisateur o : listeOrganisateurs) {
                if (o.getId().equals(organisateurId)) {
                    org = o;
                    break;
                }
            }
            if (org == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Veuillez sélectionner un organisateur."));
                return;
            }

            evenement.setOrganisateur(org);

            if (evenement.getId() == null) {
                evenementService.creer(evenement);
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Succès", "Événement créé avec succès."));
            } else {
                evenementService.modifier(evenement);
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Succès", "Événement modifié avec succès."));
            }
            preparerNouveau();
            this.motCle = null;
            this.organisateurId = null;
            chargerEvenements();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Erreur lors de l'enregistrement : " + e.getMessage()));
        }
    }

    public void publier(Integer id) {
        try {
            evenementService.publier(id);
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Succès", "L'événement a été publié !"));
            chargerEvenements();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Impossible de publier l'événement."));
        }
    }

    public void annulerEvenement(Integer id) {
        try {
            evenementService.annuler(id);
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Événement annulé."));
            chargerEvenements();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Impossible d'annuler."));
        }
    }

    public void supprimer(Integer id) {
        try {
            evenementService.supprimer(id);
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Succès", "Événement supprimé."));
            chargerEvenements();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Impossible de supprimer cet événement."));
        }
    }

    public void selectionner(Evenement e) {
        this.evenement = e;
        this.organisateurId = e.getOrganisateur() != null ? e.getOrganisateur().getId() : null;
    }

    // --- Gestion des billets ---
    
    public void selectionnerPourBillets(Evenement e) {
        this.evenementSelectionne = e;
        this.billetsEvenement = billetService.listerParEvenement(e.getId());
        this.nouveauBillet = new Billet();
    }

    public void ajouterBillet() {
        try {
            if (evenementSelectionne == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Aucun événement sélectionné."));
                return;
            }
            nouveauBillet.setEvenement(evenementSelectionne);
            billetService.ajouter(nouveauBillet);
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Succès", 
                    "Billet " + nouveauBillet.getTypeBillet() + " ajouté avec " + nouveauBillet.getNbPlaces() + " places."));
            billetsEvenement = billetService.listerParEvenement(evenementSelectionne.getId());
            nouveauBillet = new Billet();
            chargerEvenements();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Erreur lors de l'ajout du billet."));
        }
    }

    public void supprimerBillet(Integer id) {
        try {
            billetService.supprimer(id);
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Succès", "Billet supprimé."));
            if (evenementSelectionne != null) {
                billetsEvenement = billetService.listerParEvenement(evenementSelectionne.getId());
            }
            chargerEvenements();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Impossible de supprimer ce billet."));
        }
    }

    // --- Getters & Setters ---
    
    public Evenement getEvenement() { return evenement; }
    public void setEvenement(Evenement evenement) { this.evenement = evenement; }

    public List<Evenement> getListeEvenements() { return listeEvenements; }

    public List<Organisateur> getListeOrganisateurs() { return listeOrganisateurs; }

    public Integer getOrganisateurId() { return organisateurId; }
    public void setOrganisateurId(Integer organisateurId) { this.organisateurId = organisateurId; }

    public String getMotCle() { return motCle; }
    public void setMotCle(String motCle) { this.motCle = motCle; }

    public Billet getNouveauBillet() { return nouveauBillet; }
    public void setNouveauBillet(Billet nouveauBillet) { this.nouveauBillet = nouveauBillet; }

    public Evenement getEvenementSelectionne() { return evenementSelectionne; }
    public void setEvenementSelectionne(Evenement e) { this.evenementSelectionne = e; }

    public List<Billet> getBilletsEvenement() { return billetsEvenement; }

    public TypeBillet[] getTypesBillet() { return TypeBillet.values(); }

    public StatutEvenement[] getStatuts() { return StatutEvenement.values(); }
}
