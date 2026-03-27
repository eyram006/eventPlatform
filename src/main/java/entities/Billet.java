package entities;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "billet")
@NamedQueries({
    @NamedQuery(name = "Billet.findAll", query = "SELECT b FROM Billet b"),
    @NamedQuery(name = "Billet.findById", query = "SELECT b FROM Billet b WHERE b.id = :id"),
    @NamedQuery(name = "Billet.findByEvenement", query = "SELECT b FROM Billet b WHERE b.evenement.id = :evenementId"),
    @NamedQuery(name = "Billet.findDisponibles", query = "SELECT b FROM Billet b WHERE b.evenement.id = :evenementId AND b.nbPlacesRestantes > 0")
})
public class Billet implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Basic(optional = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type_billet")
    private TypeBillet typeBillet;

    @Basic(optional = false)
    @NotNull
    @Min(0)
    @Column(name = "prix")
    private Double prix;

    @Basic(optional = false)
    @NotNull
    @Min(1)
    @Column(name = "nb_places")
    private Integer nbPlaces;

    @Basic(optional = false)
    @NotNull
    @Min(0)
    @Column(name = "nb_places_restantes")
    private Integer nbPlacesRestantes;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "evenement_id", referencedColumnName = "id")
    private Evenement evenement;

    @OneToMany(mappedBy = "billet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Reservation> reservations = new ArrayList<>();

    public Billet() {
    }

    public Billet(Integer id) {
        this.id = id;
    }

    // --- Getters & Setters ---

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public TypeBillet getTypeBillet() { return typeBillet; }
    public void setTypeBillet(TypeBillet typeBillet) { this.typeBillet = typeBillet; }

    public Double getPrix() { return prix; }
    public void setPrix(Double prix) { this.prix = prix; }

    public Integer getNbPlaces() { return nbPlaces; }
    public void setNbPlaces(Integer nbPlaces) { this.nbPlaces = nbPlaces; }

    public Integer getNbPlacesRestantes() { return nbPlacesRestantes; }
    public void setNbPlacesRestantes(Integer nbPlacesRestantes) { this.nbPlacesRestantes = nbPlacesRestantes; }

    public Evenement getEvenement() { return evenement; }
    public void setEvenement(Evenement evenement) { this.evenement = evenement; }

    public List<Reservation> getReservations() { return reservations; }
    public void setReservations(List<Reservation> reservations) { this.reservations = reservations; }

    public boolean isDisponible() {
        return nbPlacesRestantes != null && nbPlacesRestantes > 0;
    }

    @Override
    public int hashCode() {
        return (id != null ? id.hashCode() : 0);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Billet)) return false;
        Billet other = (Billet) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "entities.Billet[ id=" + id + " ]";
    }
}
