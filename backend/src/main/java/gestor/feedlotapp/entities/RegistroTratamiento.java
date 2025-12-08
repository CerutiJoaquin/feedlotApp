package gestor.feedlotapp.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.ZoneId;


@Entity
@Table(name = "registro_tratamiento")
public class RegistroTratamiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "registro_tratamiento_id")
    private Integer registroTratamientoId;

    @Column(nullable = false)
    private LocalDate fecha;

    private float dosis;
    private String responsable;
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "insumo_id")
    private Insumo insumo;

    @ManyToOne
    @JoinColumn(name = "animal_id")
    private Animal animal;

    @ManyToOne
    @JoinColumn(name = "planilla_tratamiento_id")
    private PlanillaTratamiento planillaTratamiento;

    public RegistroTratamiento(){}

    public RegistroTratamiento(int registroTratamientoId, LocalDate fecha, float dosis, Insumo insumo, String responsable,
                               String descripcion, Animal animal, PlanillaTratamiento planillaTratamiento) {
        this.registroTratamientoId = registroTratamientoId;
        this.fecha = fecha;
        this.dosis = dosis;
        this.insumo = insumo;
        this.responsable = responsable;
        this.descripcion = descripcion;
        this.animal = animal;
        this.planillaTratamiento = planillaTratamiento;
    }

    public Integer getRegistroTratamientoId() { return registroTratamientoId; }
    public void setRegistroTratamientoId(Integer registroTratamientoId) { this.registroTratamientoId = registroTratamientoId; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public float getDosis() { return dosis; }
    public void setDosis(float dosis) { this.dosis = dosis; }

    public Insumo getInsumo() { return insumo; }
    public void setInsumo(Insumo medicamento) { this.insumo = medicamento; }

    public Animal getAnimal() { return animal; }
    public void setAnimal(Animal animal) { this.animal = animal; }

    public PlanillaTratamiento getPlanillaTratamiento() { return planillaTratamiento; }
    public void setPlanillaTratamiento(PlanillaTratamiento planillaTratamiento) { this.planillaTratamiento = planillaTratamiento; }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @PrePersist
    public void prePersist(){
        if(fecha == null){
            fecha = LocalDate.now(ZoneId.of("America/Argentina/Cordoba"));
        }
    }
}
