/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 *
 * @author Chari
 */
@Entity
@Table(name = "organisateur")
@NamedQueries({
    @NamedQuery(name = "Organisateur.findAll", query = "SELECT o FROM Organisateur o"),
    @NamedQuery(name = "Organisateur.findById", query = "SELECT o FROM Organisateur o WHERE o.id = :id"),
    @NamedQuery(name = "Organisateur.findByOrganisation", query = "SELECT o FROM Organisateur o WHERE o.organisation = :organisation")})
public class Organisateur implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id")
    private Integer id;
    @Size(max = 100)
    @Column(name = "organisation")
    private String organisation;
    @MapsId 
    @JoinColumn(name = "id", referencedColumnName = "id")
    @OneToOne(optional = false, cascade = CascadeType.ALL)
    private Personne personne;

    public Organisateur() {
    }

    public Organisateur(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public Personne getPersonne() {
        return personne;
    }

    public void setPersonne(Personne personne) {
        this.personne = personne;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Organisateur)) {
            return false;
        }
        Organisateur other = (Organisateur) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Organisateur[ id=" + id + " ]";
    }
    
}
