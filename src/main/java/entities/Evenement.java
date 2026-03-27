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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "evenement")
@NamedQueries({
    @NamedQuery(name = "Evenement.findAll", query = "SELECT e FROM Evenement e ORDER BY e.dateEvenement DESC"),
    @NamedQuery(name = "Evenement.findById", query = "SELECT e FROM Evenement e WHERE e.id = :id"),
    @NamedQuery(name = "Evenement.findPublies", query = "SELECT e FROM Evenement e WHERE e.statut = entities.StatutEvenement.PUBLIE ORDER BY e.dateEvenement ASC"),
    @NamedQuery(name = "Evenement.findByOrganisateur", query = "SELECT e FROM Evenement e WHERE e.organisateur.id = :organisateurId ORDER BY e.dateEvenement DESC")
})
public class Evenement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Basic(optional = false)
    @NotNull
    @Size(min = 2, max = 150)
    @Column(name = "titre")
    private String titre;

    @Size(max = 1000)
    @Column(name = "description")
    private String description;

    @Basic(optional = false)
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_evenement")
    private Date dateEvenement;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "lieu")
    private String lieu;

    @Basic(optional = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private StatutEvenement statut = StatutEvenement.BROUILLON;

    @Basic(optional = false)
    @NotNull
    @Min(1)
    @Column(name = "capacite")
    private Integer capacite;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "organisateur_id", referencedColumnName = "id")
    private Organisateur organisateur;

    @OneToMany(mappedBy = "evenement", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Billet> billets = new ArrayList<>();

    public Evenement() {
    }

    public Evenement(Integer id) {
        this.id = id;
    }

    // --- Getters & Setters ---

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getDateEvenement() { return dateEvenement; }
    public void setDateEvenement(Date dateEvenement) { this.dateEvenement = dateEvenement; }

    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }

    public StatutEvenement getStatut() { return statut; }
    public void setStatut(StatutEvenement statut) { this.statut = statut; }

    public Integer getCapacite() { return capacite; }
    public void setCapacite(Integer capacite) { this.capacite = capacite; }

    public Organisateur getOrganisateur() { return organisateur; }
    public void setOrganisateur(Organisateur organisateur) { this.organisateur = organisateur; }

    public List<Billet> getBillets() { return billets; }
    public void setBillets(List<Billet> billets) { this.billets = billets; }

    public int getPlacesAttribuees() {
        return billets.stream().mapToInt(Billet::getNbPlaces).sum();
    }

    public int getPlacesRestantes() {
        return billets.stream().mapToInt(Billet::getNbPlacesRestantes).sum();
    }

    public int getPlacesDisponiblesAAttribuer() {
        return (capacite != null ? capacite : 0) - getPlacesAttribuees();
    }

    // Helpers pour comparaisons fiables dans JSF/EL
    public boolean isBrouillon() { return statut == StatutEvenement.BROUILLON; }
    public boolean isPublie() { return statut == StatutEvenement.PUBLIE; }
    public boolean isAnnule() { return statut == StatutEvenement.ANNULE; }
    public boolean isTermine() { return statut == StatutEvenement.TERMINE; }
    public String getStatutLabel() { return statut != null ? statut.name() : ""; }

    @Override
    public int hashCode() {
        return (id != null ? id.hashCode() : 0);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Evenement)) return false;
        Evenement other = (Evenement) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "entities.Evenement[ id=" + id + " ]";
    }
}
