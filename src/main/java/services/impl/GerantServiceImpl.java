/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services.impl;

import dao.GerantDAO;
import entities.Gerant;
import jakarta.ejb.*;
import java.util.List;
import services.GerantService;

/**
 *
 * @author Chari
 */

@Stateless
public class GerantServiceImpl implements GerantService {

    @EJB
    private GerantDAO gerantDao;

    @Override
    public void creer(Gerant gerant) {
        gerantDao.ajouter(gerant);
    }

    @Override
    public void actualiser(Gerant gerant) {
        gerantDao.modifier(gerant);
    }

    @Override
    public void effacer(Integer id) {
        gerantDao.supprimer(id);
    }

    @Override
    public List<Gerant> toutAfficher() {
        return gerantDao.lister();
    }    
    
    @Override
    public List<Gerant> rechercher(String motCle) {
        return gerantDao.rechercher(motCle);
    }
}