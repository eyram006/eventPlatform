package dao;

import entities.Evenement;
import java.util.List;

public interface EvenementDAO {
    void ajouter(Evenement e);
    void modifier(Evenement e);
    void supprimer(Integer id);
    Evenement trouver(Integer id);
    List<Evenement> listerTout();
    List<Evenement> listerPublies();
    List<Evenement> listerParOrganisateur(Integer organisateurId);
    List<Evenement> rechercher(String motCle);
}
