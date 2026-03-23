/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package services;

import entities.Gerant;
import jakarta.ejb.Local;
import java.util.List;

/**
 *
 * @author Chari
 */
@Local
public interface GerantService {
    void creer(Gerant gerant);
    void actualiser(Gerant gerant);
    void effacer(Integer id);
    List<Gerant> toutAfficher();
    List<Gerant> rechercher(String motCle);
}
