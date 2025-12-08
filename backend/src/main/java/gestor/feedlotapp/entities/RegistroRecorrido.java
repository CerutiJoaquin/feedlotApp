package gestor.feedlotapp.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.ZoneId;

@Entity
@Table(name = "registro_recorrido")
public class RegistroRecorrido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "registro_recorrido_id")
    private Integer registroRecorridoId;

    @Column(name = "observaciones")
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "corral_id")
    private Corral corralId;

    @Column(name = "fecha")
    @JsonIgnoreProperties
    private LocalDate fecha;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "planilla_recorrido_id")
    @JsonIgnoreProperties
    private PlanillaRecorrido planillaRecorrido;

    @PrePersist
    public void prePersist(){
        if(fecha == null){
            this.fecha = LocalDate.now(ZoneId.of("America/Argentina/Cordoba"));
        }
    }

    public RegistroRecorrido(){}

    public RegistroRecorrido(int registroRecorridoId, String observaciones, Corral corralId, PlanillaRecorrido planillaRecorrido) {
        this.registroRecorridoId = registroRecorridoId;
        this.observaciones = observaciones;
        this.corralId = corralId;
        this.planillaRecorrido = planillaRecorrido;
    }

    public Integer getRegistroRecorridoId() { return registroRecorridoId; }
    public void setRegistroRecorridoId(Integer registroRecorridoId) { this.registroRecorridoId = registroRecorridoId; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Corral getCorralId() { return corralId; }
    public void setCorralId(Corral corral) { this.corralId = corral; }

    public PlanillaRecorrido getPlanillaRecorrido() { return planillaRecorrido; }
    public void setPlanillaRecorrido(PlanillaRecorrido planillaRecorrido) { this.planillaRecorrido = planillaRecorrido; }

    public LocalDate getFecha() {
        return fecha;
    }
}
