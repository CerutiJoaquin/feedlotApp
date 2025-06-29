package gestor.feedlotapp.entities;


import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "pesaje")
public class Pesaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pesaje_id")
    private int pesajeId;
    @Temporal(TemporalType.DATE)
    private Date fecha;
    private float peso;
    private String observaciones;
    @ManyToOne
    @JoinColumn(name = "animal_id")
    private Animal animal;


    // Constructores
    public Pesaje(){}

    public Pesaje(int pesajeId, Date fecha, float peso, String observaciones, Animal animal) {
        pesajeId = pesajeId;
        this.fecha = fecha;
        this.peso = peso;
        this.observaciones = observaciones;
        this.animal = animal;
    }

    // Getters y Setters


    public int getPesajeId() {
        return pesajeId;
    }

    public void setPesajeId(int pesajeId) {
        pesajeId = pesajeId;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public float getPeso() {
        return peso;
    }

    public void setPeso(float peso) {
        this.peso = peso;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal_id) {
        this.animal = animal_id;
    }
}
