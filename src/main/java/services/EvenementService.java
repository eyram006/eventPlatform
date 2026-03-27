package services;

import entities.Evenement;
import java.util.List;

public interface EvenementService {
    void creer(Evenement e);
    void modifier(Evenement e);
    void supprimer(Integer id);
    void publier(Integer id);
    void annuler(Integer id);
    void retirerEvenement(Integer id);
    Evenement trouver(Integer id);
    List<Evenement> listerTout();
    List<Evenement> listerPublies();
    List<Evenement> listerParOrganisateur(Integer organisateurId);
    List<Evenement> rechercher(String motCle);
}
