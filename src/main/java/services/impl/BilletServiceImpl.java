package services.impl;

import dao.BilletDAO;
import entities.Billet;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.List;
import services.BilletService;

@Stateless
public class BilletServiceImpl implements BilletService {

    @Inject
    private BilletDAO dao;

    @Override
    public void ajouter(Billet b) {
        b.setNbPlacesRestantes(b.getNbPlaces());
        dao.ajouter(b);
    }

    @Override
    public void modifier(Billet b) {
        dao.modifier(b);
    }

    @Override
    public void supprimer(Integer id) {
        dao.supprimer(id);
    }

    @Override
    public Billet trouver(Integer id) {
        return dao.trouver(id);
    }

    @Override
    public List<Billet> listerParEvenement(Integer evenementId) {
        return dao.listerParEvenement(evenementId);
    }

    @Override
    public List<Billet> listerDisponibles(Integer evenementId) {
        return dao.listerDisponibles(evenementId);
    }
}
