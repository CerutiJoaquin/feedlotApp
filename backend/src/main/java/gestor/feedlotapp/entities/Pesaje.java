package gestor.feedlotapp.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.ZoneId;

@Entity
@Table(name = "pesaje")
public class Pesaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pesaje_id")
    private Long pesajeId;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private float peso;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;

    @PrePersist
    public void prePersist(){
        if(fecha == null){
            fecha = LocalDate.now(ZoneId.of("America/Argentina/Cordoba"));
        }
    }

    public Pesaje(){}

    public Pesaje(Long pesajeId, float peso, LocalDate fecha, Animal animal) {
        this.pesajeId = pesajeId;
        this.fecha = fecha;
        this.peso = peso;
        this.animal = animal;
    }

    public Long getPesajeId() { return pesajeId; }
    public void setPesajeId(Long pesajeId) { this.pesajeId = pesajeId; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public float getPeso() { return peso; }
    public void setPeso(float peso) { this.peso = peso; }

    public Animal getAnimal() { return animal; }
    public void setAnimal(Animal animal) { this.animal = animal; }
}
