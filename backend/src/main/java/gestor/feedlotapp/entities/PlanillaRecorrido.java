package gestor.feedlotapp.entities;

import jakarta.persistence.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "planilla_recorrido")
public class PlanillaRecorrido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "planilla_recorrido_id")
    private Integer planillaRecorridoId;
    @Temporal(TemporalType.DATE)
    private Date fecha;
    private String observaciones;
    private String responsable;
    @OneToMany(mappedBy = "planillaRecorrido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegistroRecorrido> registrosRecorridos = new ArrayList<>();

    // Constructores
    public PlanillaRecorrido(){}

    public PlanillaRecorrido(int planillaRecorridoId, Date fecha, String observaciones, String responsable, List<RegistroRecorrido> registrosRecorridos) {
        this.planillaRecorridoId = planillaRecorridoId;
        this.fecha = fecha;
        this.observaciones = observaciones;
        this.responsable = responsable;
        this.registrosRecorridos = registrosRecorridos;
    }

    // Getters y Setters

    public Integer getPlanillaRecorridoId() {
        return planillaRecorridoId;
    }

    public void setPlanillaRecorridoId(int planillaRecorridoId) {
        planillaRecorridoId = planillaRecorridoId;
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

    public List<RegistroRecorrido> getRegistrosRecorridos() {
        return registrosRecorridos;
    }

    public void setRegistrosRecorridos(List<RegistroRecorrido> registrosRecorridos) {
        this.registrosRecorridos = registrosRecorridos;
    }
}
