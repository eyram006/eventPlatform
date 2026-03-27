package services;

import entities.Billet;
import java.util.List;

public interface BilletService {
    void ajouter(Billet b);
    void modifier(Billet b);
    void supprimer(Integer id);
    Billet trouver(Integer id);
    List<Billet> listerParEvenement(Integer evenementId);
    List<Billet> listerDisponibles(Integer evenementId);
}
