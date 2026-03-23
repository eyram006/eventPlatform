/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services.impl;

import dao.OrganisateurDAO;
import entities.Organisateur;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.List;
import services.OrganisateurService;

/**
 *
 * @author Chari
 */
@Stateless
public class OrganisateurServiceImpl implements OrganisateurService {
    @Inject
    private OrganisateurDAO dao;

    @Override
    public void enregistrer(Organisateur o) { dao.ajouter(o); }

    @Override
    public void actualiser(Organisateur o) { dao.modifier(o); }

    @Override
    public void effacer(Integer id) { dao.supprimer(id); }

    @Override
    public List<Organisateur> toutAfficher() { return dao.listerTout(); }
    
    @Override
    public List<Organisateur> rechercher(String motCle) {
        return dao.rechercher(motCle);
    }
}
