/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import entities.Organisateur;
import java.util.List;

/**
 *
 * @author Chari
 */
public interface OrganisateurDAO {
    void ajouter(Organisateur o);
    void modifier(Organisateur o);
    void supprimer(Integer id);
    Organisateur trouver(Integer id);
    List<Organisateur> listerTout();
    List<Organisateur> rechercher(String motCle);
}
