package gestor.feedlotapp.entities;



import jakarta.persistence.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "planilla_tratamiento")
public class PlanillaTratamiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "planilla_tratamiento_id")
    private Integer planillaTratamientoId;
    @Temporal(TemporalType.DATE)
    private Date fecha;
    private String observaciones;
    private String responsable;

    @OneToMany(mappedBy = "planillaTratamiento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegistroTratamiento> registrosTratamientos = new ArrayList<>();

    // Constructores

    public PlanillaTratamiento() {}

    public PlanillaTratamiento(int planillaTratamientoId, Date fecha, String observaciones, String responsable, List<RegistroTratamiento> registrosTratamientos) {
        this.planillaTratamientoId = planillaTratamientoId;
        this.fecha = fecha;
        this.observaciones = observaciones;
        this.responsable = responsable;
        this.registrosTratamientos = registrosTratamientos;
    }
    // Setters y Getters

    public Integer getPlanillaTratamientoId() {
        return planillaTratamientoId;
    }

    public void setPlanillaTratamientoId(Integer planillaTratamientoId) {
        this.planillaTratamientoId = planillaTratamientoId;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public List<RegistroTratamiento> getRegistrosTratamientos() {
        return registrosTratamientos;
    }

    public void setRegistrosTratamientos(List<RegistroTratamiento> registrosTratamientos) {
        this.registrosTratamientos = registrosTratamientos;
    }
}
