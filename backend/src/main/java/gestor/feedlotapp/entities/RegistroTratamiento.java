package gestor.feedlotapp.entities;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "registro_tratamiento")
public class RegistroTratamiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "registro_tratamiento_id")
    private Integer registroTratamientoId;
    @Temporal(TemporalType.DATE)
    @Column(nullable = false, updatable = false)
    private Date fecha;
    private float dosis;

    @ManyToOne
    @JoinColumn(name = "insumo_id")
    private Insumo medicamento;

    @ManyToOne
    @JoinColumn(name = "animal_id")
    private Animal animal;

    @ManyToOne
    @JoinColumn(name = "planilla_tratamiento_id")
    private PlanillaTratamiento planillaTratamiento;

    // Constructores
    public RegistroTratamiento(){}

    public RegistroTratamiento(int registroTratamientoId, Date fecha, float dosis, Insumo medicamento,
                               Animal animal, PlanillaTratamiento planillaTratamiento) {
        this.registroTratamientoId = registroTratamientoId;
        this.fecha = fecha;
        this.dosis = dosis;
        this.medicamento = medicamento;
        this.animal = animal;
        this.planillaTratamiento = planillaTratamiento;
    }

    // Setters y Getters

    public Integer getRegistroTratamientoId() {
        return registroTratamientoId;
    }

    public void setRegistroTratamientoId(Integer registroTratamientoId) {
        this.registroTratamientoId = registroTratamientoId;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public float getDosis() {
        return dosis;
    }

    public void setDosis(float dosis) {
        this.dosis = dosis;
    }

    public Insumo getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(Insumo medicamento) {
        this.medicamento = medicamento;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal_id) {
        this.animal = animal_id;
    }

    public PlanillaTratamiento getPlanillaTratamiento() {
        return planillaTratamiento;
    }

    public void setPlanillaTratamiento(PlanillaTratamiento planillaTratamiento) {
        this.planillaTratamiento = planillaTratamiento;
    }
    @PrePersist
    protected void onCreate() {
        this.fecha = new Date();
    }
}
