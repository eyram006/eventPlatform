/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import entities.Gerant;
import java.util.List;

/**
 *
 * @author Chari
 */
public interface GerantDAO {
    void ajouter(Gerant gerant);
    void modifier(Gerant gerant);
    void supprimer(Integer id);
    Gerant trouver(Integer id);
    List<Gerant> lister();
    List<Gerant> rechercher(String motCle);
}
