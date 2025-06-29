package gestor.feedlotapp.entities;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "planilla_comedero")
public class PlanillaComedero {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "planilla_comedero_id")
    private Integer planillaComederoId;
    @Temporal(TemporalType.DATE)
    private Date fecha;
    private String encargado;
    private String observaciones;
    @OneToMany(mappedBy = "planillaComedero", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegistroComedero> registrosComederos = new ArrayList<>();

    // Constructres
    public PlanillaComedero(){}

    public PlanillaComedero(int planillaComederoId, Date fecha, String encargado, String observaciones, List<RegistroComedero> registrosComederos) {
        this.planillaComederoId = planillaComederoId;
        this.fecha = fecha;
        this.encargado = encargado;
        this.observaciones = observaciones;
        this.registrosComederos = registrosComederos;
    }

    // Setters y Getters


    public Integer getPlanillaComederoId() {
        return planillaComederoId;
    }

    public void setPlanillaComederoId(Integer planillaComederoId) {
        this.planillaComederoId = planillaComederoId;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getEncargado() {
        return encargado;
    }

    public void setEncargado(String encargado) {
        this.encargado = encargado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<RegistroComedero> getRegistrosComederos() {
        return registrosComederos;
    }

    public void setRegistrosComederos(List<RegistroComedero> registrosComederos) {
        this.registrosComederos = registrosComederos;
    }
}
