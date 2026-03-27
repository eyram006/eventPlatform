package bean;

import entities.Billet;
import entities.Evenement;
import entities.Organisateur;
import entities.Personne;
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

@Named
@ViewScoped
public class OrganisateurBean implements Serializable {
    
    @Inject
    private OrganisateurService service;

    @Inject
    private EvenementService evenementService;

    @Inject
    private BilletService billetService;

    private Organisateur organisateur;
    private List<Organisateur> liste;
    private String motCle;

    // Gestion des evenements
    private Organisateur organisateurSelectionne;
    private Evenement evenement;
    private List<Evenement> evenementsOrganisateur;

    // Gestion des billets
    private Evenement evenementPourBillets;
    private Billet nouveauBillet;
    private Billet billetEnEdition;
    private List<Billet> billetsEvenement;
    private boolean modeEditionBillet = false;

    @PostConstruct
    public void init() {
        preparerNouveau();
        chargerListe();
    }

    public void preparerNouveau() {
        organisateur = new Organisateur();
        organisateur.setPersonne(new Personne());
    }

    public void chargerListe() {
        liste = service.toutAfficher();
    }
    
    public void rechercherOrganisateurs() {
        if (motCle == null || motCle.trim().isEmpty()) {
            chargerListe();
        } else {
            liste = service.rechercher(motCle);
        }
    }

    public void enregistrer() {
        try {
            if (organisateur.getId() == null) {
                service.enregistrer(organisateur);
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Succes", "Organisateur enregistre avec succes."));
            } else {
                service.actualiser(organisateur);
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Succes", "Informations mises a jour."));
            }
            preparerNouveau();
            this.motCle = null; 
            chargerListe();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Erreur : " + e.getMessage()));
        }
    }

    public void selectionner(Organisateur o) {
        this.organisateur = o;
    }

    public void supprimer(Integer id) {
        try {
            service.effacer(id);
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Succes", "Profil supprime."));
            if (organisateurSelectionne != null && organisateurSelectionne.getId().equals(id)) {
                fermerEvenements();
            }
            rechercherOrganisateurs(); 
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Impossible de supprimer."));
        }
    }

    // =============================================
    // GESTION DES EVENEMENTS
    // =============================================

    public void ouvrirEvenements(Organisateur o) {
        this.organisateurSelectionne = o;
        this.evenementsOrganisateur = evenementService.listerParOrganisateur(o.getId());
        preparerNouvelEvenement();
        this.evenementPourBillets = null;
        this.billetsEvenement = null;
        this.modeEditionBillet = false;
    }

    public void fermerEvenements() {
        this.organisateurSelectionne = null;
        this.evenementsOrganisateur = null;
        this.evenement = null;
        this.evenementPourBillets = null;
        this.billetsEvenement = null;
        this.modeEditionBillet = false;
    }

    public void preparerNouvelEvenement() {
        this.evenement = new Evenement();
        this.evenement.setStatut(StatutEvenement.BROUILLON);
    }

    public void enregistrerEvenement() {
        try {
            if (organisateurSelectionne == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Aucun organisateur selectionne."));
                return;
            }
            if (evenement.getTitre() == null || evenement.getTitre().trim().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Le titre est obligatoire."));
                return;
            }
            if (evenement.getCapacite() == null || evenement.getCapacite() < 1) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Le nombre de places est obligatoire (min 1)."));
                return;
            }

            evenement.setOrganisateur(organisateurSelectionne);

            if (evenement.getId() == null) {
                evenementService.creer(evenement);
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Succes", "Evenement cree avec " + evenement.getCapacite() + " places."));
            } else {
                evenementService.modifier(evenement);
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Succes", "Evenement modifie."));
            }
            preparerNouvelEvenement();
            rafraichirEvenements();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Erreur : " + e.getMessage()));
        }
    }

    public void selectionnerEvenement(Evenement e) {
        this.evenement = e;
    }

    public void publierEvenement(Integer id) {
        try {
            evenementService.publier(id);
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Succes", "Evenement publie. Les clients peuvent maintenant le voir."));
            rafraichirEvenements();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", e.getMessage()));
        }
    }

    public void annulerEvenement(Integer id) {
        try {
            evenementService.annuler(id);
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Evenement annule."));
            rafraichirEvenements();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", e.getMessage()));
        }
    }

    public void supprimerEvenement(Integer id) {
        try {
            // Fermer la section billets si elle concerne cet evenement
            if (evenementPourBillets != null && evenementPourBillets.getId().equals(id)) {
                evenementPourBillets = null;
                billetsEvenement = null;
                modeEditionBillet = false;
            }
            evenementService.supprimer(id);
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Succes", "Evenement supprime definitivement."));
            rafraichirEvenements();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Impossible de supprimer : " + e.getMessage()));
        }
    }

    public void retirerEvenement(Integer id) {
        try {
            // Fermer la section billets si elle concerne cet evenement
            if (evenementPourBillets != null && evenementPourBillets.getId().equals(id)) {
                evenementPourBillets = null;
                billetsEvenement = null;
                modeEditionBillet = false;
            }
            evenementService.retirerEvenement(id);
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Succes", 
                    "Evenement retire avec succes. Les clients concernes ont ete notifies et rembourses."));
            rafraichirEvenements();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Impossible de retirer : " + e.getMessage()));
        }
    }



    public void ouvrirBillets(Evenement ev) {
        this.evenementPourBillets = evenementService.trouver(ev.getId());
        this.billetsEvenement = billetService.listerParEvenement(ev.getId());
        this.nouveauBillet = new Billet();
        this.billetEnEdition = null;
        this.modeEditionBillet = false;
    }

    public void fermerBillets() {
        this.evenementPourBillets = null;
        this.billetsEvenement = null;
        this.nouveauBillet = null;
        this.billetEnEdition = null;
        this.modeEditionBillet = false;
    }

    public void ajouterBillet() {
        try {
            if (evenementPourBillets == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Aucun evenement selectionne pour les billets."));
                return;
            }
            if (nouveauBillet.getTypeBillet() == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Le type de billet est obligatoire."));
                return;
            }
            if (nouveauBillet.getPrix() == null || nouveauBillet.getPrix() < 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Le prix est obligatoire."));
                return;
            }
            if (nouveauBillet.getNbPlaces() == null || nouveauBillet.getNbPlaces() < 1) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Le nombre de places est obligatoire (min 1)."));
                return;
            }

            int placesDejaAttribuees = evenementPourBillets.getPlacesAttribuees();
            int capacite = evenementPourBillets.getCapacite() != null ? evenementPourBillets.getCapacite() : 0;
            int restantAAttribuer = capacite - placesDejaAttribuees;

            if (nouveauBillet.getNbPlaces() > restantAAttribuer) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur",
                        "Impossible : " + nouveauBillet.getNbPlaces()
                        + " places demandees mais seulement " + restantAAttribuer
                        + " places disponibles a attribuer."));
                return;
            }

            nouveauBillet.setEvenement(evenementPourBillets);
            billetService.ajouter(nouveauBillet);
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Succes",
                    "Billet " + nouveauBillet.getTypeBillet() + " ajoute (" + nouveauBillet.getNbPlaces() + " places)."));
            
            rafraichirBillets();
            nouveauBillet = new Billet();
            rafraichirEvenements();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Erreur : " + e.getMessage()));
        }
    }

    // --- MODIFICATION BILLET ---

    public void editerBillet(Billet b) {
        this.billetEnEdition = b;
        this.modeEditionBillet = true;
    }

    public void annulerEditionBillet() {
        this.billetEnEdition = null;
        this.modeEditionBillet = false;
        rafraichirBillets();
    }

    public void sauvegarderBillet() {
        try {
            if (billetEnEdition == null) return;

            if (billetEnEdition.getTypeBillet() == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Le type est obligatoire."));
                return;
            }
            if (billetEnEdition.getPrix() == null || billetEnEdition.getPrix() < 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Le prix est obligatoire."));
                return;
            }
            if (billetEnEdition.getNbPlaces() == null || billetEnEdition.getNbPlaces() < 1) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Le nombre de places doit etre au minimum 1."));
                return;
            }

            // Verifier la capacite en tenant compte de la modification
            int capacite = evenementPourBillets.getCapacite() != null ? evenementPourBillets.getCapacite() : 0;
            int autresBillets = 0;
            for (Billet bl : billetsEvenement) {
                if (!bl.getId().equals(billetEnEdition.getId())) {
                    autresBillets += bl.getNbPlaces();
                }
            }
            int restant = capacite - autresBillets;
            if (billetEnEdition.getNbPlaces() > restant) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur",
                        "Le total des places depasserait la capacite. Maximum pour ce billet : " + restant));
                return;
            }

            // Ajuster les places restantes proportionnellement
            int ancienNbPlaces = billetService.trouver(billetEnEdition.getId()).getNbPlaces();
            int diff = billetEnEdition.getNbPlaces() - ancienNbPlaces;
            int nouvellesRestantes = billetEnEdition.getNbPlacesRestantes() + diff;
            if (nouvellesRestantes < 0) nouvellesRestantes = 0;
            billetEnEdition.setNbPlacesRestantes(nouvellesRestantes);

            billetService.modifier(billetEnEdition);
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Succes", "Billet modifie avec succes."));
            
            this.billetEnEdition = null;
            this.modeEditionBillet = false;
            rafraichirBillets();
            rafraichirEvenements();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", e.getMessage()));
        }
    }

    public void supprimerBillet(Integer id) {
        try {
            billetService.supprimer(id);
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Succes", "Billet supprime."));
            if (billetEnEdition != null && billetEnEdition.getId().equals(id)) {
                billetEnEdition = null;
                modeEditionBillet = false;
            }
            rafraichirBillets();
            rafraichirEvenements();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", e.getMessage()));
        }
    }

    private void rafraichirBillets() {
        if (evenementPourBillets != null) {
            evenementPourBillets = evenementService.trouver(evenementPourBillets.getId());
            billetsEvenement = billetService.listerParEvenement(evenementPourBillets.getId());
        }
    }

    private void rafraichirEvenements() {
        if (organisateurSelectionne != null) {
            evenementsOrganisateur = evenementService.listerParOrganisateur(organisateurSelectionne.getId());
        }
    }

   
    // GETTERS & SETTERS
    

    public Organisateur getOrganisateur() { return organisateur; }
    public void setOrganisateur(Organisateur o) { this.organisateur = o; }
    public List<Organisateur> getListe() { return liste; }
    public String getMotCle() { return motCle; }
    public void setMotCle(String motCle) { this.motCle = motCle; }

    public Organisateur getOrganisateurSelectionne() { return organisateurSelectionne; }
    public Evenement getEvenement() { return evenement; }
    public void setEvenement(Evenement evenement) { this.evenement = evenement; }
    public List<Evenement> getEvenementsOrganisateur() { return evenementsOrganisateur; }

    public Evenement getEvenementPourBillets() { return evenementPourBillets; }
    public Billet getNouveauBillet() { return nouveauBillet; }
    public void setNouveauBillet(Billet b) { this.nouveauBillet = b; }
    public List<Billet> getBilletsEvenement() { return billetsEvenement; }

    public Billet getBilletEnEdition() { return billetEnEdition; }
    public void setBilletEnEdition(Billet b) { this.billetEnEdition = b; }
    public boolean isModeEditionBillet() { return modeEditionBillet; }

    public TypeBillet[] getTypesBillet() { return TypeBillet.values(); }
}