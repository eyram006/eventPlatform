package bean;

import entities.Gerant;
import entities.Personne;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import services.GerantService;

@Named
@ViewScoped
public class GerantBean implements Serializable {

    @Inject
    private GerantService gerantService;

    private Gerant gerant;
    private List<Gerant> listeGerants;
    
    private String motCle;

    @PostConstruct
    public void init() {
        preparerNouveau();
        chargerGerants();
    }

    public void preparerNouveau() {
        gerant = new Gerant();
        gerant.setPersonne(new Personne()); 
    }

    public void chargerGerants() {
        listeGerants = gerantService.toutAfficher();
    }
    
    public void rechercherGerants() {
        listeGerants = gerantService.rechercher(motCle);
    }

    public void enregistrer() {
        try {
            if (gerant.getId() == null) {
                gerantService.creer(gerant);
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Succès", "Gérant enregistré avec succès."));
            } else {
                gerantService.actualiser(gerant);
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Succès", "Informations du gérant mises à jour avec succès."));
            }
            preparerNouveau();
            this.motCle = null; 
            chargerGerants();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Une erreur est survenue lors de l'enregistrement."));
        }
    }

    public void supprimer(Integer id) {
        try {
            gerantService.effacer(id);
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Succès", "Le profil du gérant a été supprimé."));
            rechercherGerants(); 
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Impossible de supprimer ce gérant."));
        }
    }

    public void selectionner(Gerant g) {
        this.gerant = g;
    }

    // Getters et Setters
    public Gerant getGerant() { return gerant; }
    public void setGerant(Gerant gerant) { this.gerant = gerant; }
    public List<Gerant> getListeGerants() { return listeGerants; }
    public String getMotCle() { return motCle; }
    public void setMotCle(String motCle) { this.motCle = motCle; }
}