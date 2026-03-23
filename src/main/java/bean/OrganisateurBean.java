package bean;

import entities.Organisateur;
import entities.Personne;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import services.OrganisateurService;

@Named
@ViewScoped
public class OrganisateurBean implements Serializable {
    
    @Inject
    private OrganisateurService service;

    private Organisateur organisateur;
    private List<Organisateur> liste;
    private String motCle;

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
        liste = service.rechercher(motCle);
    }

    public void enregistrer() {
        try {
            if (organisateur.getId() == null) {
                service.enregistrer(organisateur);
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Succès", "Organisateur enregistré avec succès."));
            } else {
                service.actualiser(organisateur);
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Succès", "Informations de l'organisateur mises à jour avec succès."));
            }
            preparerNouveau();
            this.motCle = null; 
            chargerListe();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Une erreur est survenue lors de l'enregistrement."));
        }
    }

    public void selectionner(Organisateur o) {
        this.organisateur = o;
    }

    public void supprimer(Integer id) {
        try {
            service.effacer(id);
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Succès", "Le profil de l'organisateur a été supprimé."));
            rechercherOrganisateurs(); 
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Impossible de supprimer cet organisateur."));
        }
    }

    // Getters & Setters
    public Organisateur getOrganisateur() { return organisateur; }
    public void setOrganisateur(Organisateur o) { this.organisateur = o; }
    public List<Organisateur> getListe() { return liste; }
    public String getMotCle() { return motCle; }
    public void setMotCle(String motCle) { this.motCle = motCle; }
}