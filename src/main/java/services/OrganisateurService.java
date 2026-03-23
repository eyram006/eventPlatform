/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package services;

import entities.Organisateur;
import java.util.List;

/**
 *
 * @author Chari
 */
public interface OrganisateurService {
    void enregistrer(Organisateur o);
    void actualiser(Organisateur o);
    void effacer(Integer id);
    List<Organisateur> toutAfficher();
    List<Organisateur> rechercher(String motCle);
}
