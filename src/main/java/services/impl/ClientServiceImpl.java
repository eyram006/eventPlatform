/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services.impl;

import dao.ClientDAO;
import entities.Client;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.List;
import services.ClientService;

/**
 *
 * @author Chari
 */
@Stateless
public class ClientServiceImpl implements ClientService {

    @Inject
    private ClientDAO dao;

    @Override
    public void ajouter(Client c) {
        dao.ajouter(c);
    }

    @Override
    public void modifier(Client c) {
        dao.modifier(c);
    }

    @Override
    public void supprimer(int id) {
        dao.supprimer(id);
    }

    @Override
    public List<Client> lister() {
        return dao.lister();
    }
    @Override
    public List<Client> rechercher(String motCle) {
        return dao.rechercher(motCle);
    }
}
