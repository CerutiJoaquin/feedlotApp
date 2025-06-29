package gestor.feedlotapp.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "corral")
public class Corral {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "corral_id")
    private Integer corralId;
    private String nombre;
    private String descripcion;
    @Positive
    @Column(name = "capacidad_min")
    private int capacidadMin;

    @Positive
    @Column(name = "capacidad_max")
    private int capacidadMax;
    @Column(name = "tipo_superficie")
    private String tipoSuperficie;
    private String estado;

    @OneToMany(mappedBy = "corral", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("corral")
    private List<Animal> animales = new ArrayList<>();

    @OneToMany(mappedBy = "corral")
    private List<RegistroRecorrido> registroRecorridos = new ArrayList<>();

    @OneToMany(mappedBy = "corral")
    private List<RegistroComedero> registroComederos = new ArrayList<>();


    // Constructores
    public Corral(){}

    public Corral(int corralId, String nombre, String descripcion, int capacidadMin, int capacidadMax,
                  String tipoSuperficie, String estado, List<Animal> animales) {
        this.corralId = corralId;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.capacidadMin = capacidadMin;
        this.capacidadMax = capacidadMax;
        this.tipoSuperficie = tipoSuperficie;
        this.estado = estado;
        this.animales = animales;
    }

    public Corral(int corralId, String nombre, String descripcion, int capacidadMin, int capacidadMax,
                  String tipoSuperficie, String estado, List<Animal> animales,
                  List<RegistroRecorrido> registroRecorridos, List<RegistroComedero> registroComederos) {
        this.corralId = corralId;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.capacidadMin = capacidadMin;
        this.capacidadMax = capacidadMax;
        this.tipoSuperficie = tipoSuperficie;
        this.estado = estado;
        this.animales = animales;
        this.registroRecorridos = registroRecorridos;
        this.registroComederos = registroComederos;
    }

    // Getters y Setters

    public int getCorralId() {
        return corralId;
    }

    public void setCorralId(int corralId) {
        this.corralId = corralId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getCapacidadMin() {
        return capacidadMin;
    }

    public void setCapacidadMin(int capacidad_min) {
        this.capacidadMin = capacidad_min;
    }

    public int getCapacidadMax() {
        return capacidadMax;
    }

    public void setCapacidadMax(int capacidad_max) {
        this.capacidadMax = capacidad_max;
    }

    public String getTipoSuperficie() {
        return tipoSuperficie;
    }

    public void setTipoSuperficie(String tipo_superficie) {
        this.tipoSuperficie = tipo_superficie;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<Animal> getAnimales() {
        return animales;
    }

    public void setAnimales(List<Animal> animales) {
        this.animales = animales;
    }

    public List<RegistroRecorrido> getRegistroRecorridos() {
        return registroRecorridos;
    }

    public void setRegistroRecorridos(List<RegistroRecorrido> registroRecorridos) {
        this.registroRecorridos = registroRecorridos;
    }

    public List<RegistroComedero> getRegistroComederos() {
        return registroComederos;
    }

    public void setRegistroComederos(List<RegistroComedero> registroComederos) {
        this.registroComederos = registroComederos;
    }
}
