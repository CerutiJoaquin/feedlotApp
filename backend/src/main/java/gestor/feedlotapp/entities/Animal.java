package gestor.feedlotapp.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "animal")
@JsonIgnoreProperties({"pesajes", "tratamientos"})
public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "animal_id")
    private int animalId;
    private String caravana;
    private String raza;
    private int edad;

    @Positive
    @Column(name="peso_actual")
    private float pesoActual;
    private boolean sexo;
    @Column(name = "estado_salud")
    private String estadoSalud;
    @ManyToOne
    @JoinColumn(name = "corral_id") 
    @JsonIgnore
    private Corral corral;

    @ManyToOne
    @JoinColumn(name = "venta_lote_id")
    private VentaLote ventaLote;

    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL)
     @JsonIgnoreProperties("animal")
    private List<Pesaje> pesajes;


    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, orphanRemoval = true)
     @JsonIgnoreProperties("animal")
    private List<RegistroTratamiento> tratamientos = new ArrayList<>();

    // Constructores
    public Animal(){}

    public Animal(int animalId, String caravana, String raza, int edad, float pesoActual,
                  boolean sexo, String estadoSalud, Corral corral) {
        this.animalId = animalId;
        this.caravana = caravana;
        this.raza = raza;
        this.edad = edad;
        this.pesoActual = pesoActual;
        this.sexo = sexo;
        this.estadoSalud = estadoSalud;
        this.corral = corral;
    }

    public Animal(int animalId, String caravana, String raza, int edad, float pesoActual,
                  boolean sexo, String estadoSalud, Corral corral, List<Pesaje> pesajes,
                  List<RegistroTratamiento> tratamientos) {
        this.animalId = animalId;
        this.caravana = caravana;
        this.raza = raza;
        this.edad = edad;
        this.pesoActual = pesoActual;
        this.sexo = sexo;
        this.estadoSalud = estadoSalud;
        this.corral = corral;
        this.pesajes = pesajes;
        this.tratamientos = tratamientos;
    }


    // Setters y Getters
    @JsonProperty("corralId")
    public Integer getCorralId() {
        return corral != null
                ? corral.getCorralId()
                : null;
    }

    public void setCorralId(Integer id) {
        if (this.corral == null) {
            this.corral = new Corral();
        }
        this.corral.setCorralId(id);
    }

    public int getAnimalId() {
        return animalId;
    }

    public void setAnimalId(int animalId) {
        this.animalId = animalId;
    }

    public String getCaravana() {
        return caravana;
    }

    public void setCaravana(String caravana) {
        this.caravana = caravana;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public float getPesoActual() {
        return pesoActual;
    }

    public void setPesoActual(float pesoActual) {
        this.pesoActual = pesoActual;
    }

    public boolean getSexo() {
        return sexo;
    }

    public void setSexo(boolean sexo) {
        this.sexo = sexo;
    }

    public String getEstadoSalud() {
        return estadoSalud;
    }

    public void setEstadoSalud(String estado_salud) {
        this.estadoSalud = estado_salud;
    }

    public Corral getCorral() {
        return corral;
    }

    public void setCorral(Corral corral) {
        this.corral = corral;
    }

    public List<Pesaje> getPesajes() {
        return pesajes;
    }

    public void setPesajes(List<Pesaje> pesajes) {
        this.pesajes = pesajes;
    }


    public List<RegistroTratamiento> getTratamientos() {
        return tratamientos;
    }

    public void setTratamientos(List<RegistroTratamiento> tratamientos) {
        this.tratamientos = tratamientos;
    }

    public VentaLote getVentaLote() {
        return ventaLote;
    }

    public void setVentaLote(VentaLote ventaLote) {
        this.ventaLote = ventaLote;
    }
}
