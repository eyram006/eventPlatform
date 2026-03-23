/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package services;

import entities.Client;
import java.util.List;

/**
 *
 * @author Chari
 */
public interface ClientService {
    void ajouter(Client c);
    void modifier(Client c);
    void supprimer(int id);
    List<Client> lister();
    List<Client> rechercher(String motCle);
}
