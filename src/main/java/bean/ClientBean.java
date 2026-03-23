package bean;

import entities.Client;
import entities.Personne;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import services.ClientService;

@Named("clientBean")
@ViewScoped
public class ClientBean implements Serializable {
    
    @Inject
    private ClientService service;

    private Client client = new Client();
    private List<Client> listeClients;
   
    private String motCle;
    
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
        listeClients = service.rechercher(motCle);
    }
    
    public void ajouter() {
        try {
            if (client.getId() == null) {
                service.ajouter(client);
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Succès", "Client enregistré avec succès."));
            } else {
                service.modifier(client);
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Succès", "Informations mises à jour avec succès."));
            }
            preparerNouveau();
            this.motCle = null; 
            chargerClients();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Une erreur est survenue."));
        }
    }

    public void supprimer(int id) {
        try {
            service.supprimer(id);
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Succès", "Le client a été supprimé."));
            rechercherClients(); 
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Impossible de supprimer ce client."));
        }
    }
    
    public void modifier(Client c) {
       this.client = c;
       if (this.client.getPersonne() == null) {
           this.client.setPersonne(new Personne());
       }
    }

    // Getters et Setters
    public List<Client> getListe() { return listeClients; }
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    public String getMotCle() { return motCle; }
    public void setMotCle(String motCle) { this.motCle = motCle; }
}