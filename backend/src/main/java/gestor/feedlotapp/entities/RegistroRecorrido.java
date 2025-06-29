package gestor.feedlotapp.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "registro_recorrido")
public class RegistroRecorrido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "registro_recorrido_id")
    private Integer registroRecorridoId;
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "corral_id")
    private Corral corral;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "planilla_recorrido_id")        
    private PlanillaRecorrido planillaRecorrido;

    // Constructores

    public RegistroRecorrido(){}

    public RegistroRecorrido(int registroRecorridoId, String observaciones, Corral corral, PlanillaRecorrido planillaRecorrido) {
        this.registroRecorridoId = registroRecorridoId;
        this.observaciones = observaciones;
        this.corral = corral;
        this.planillaRecorrido = planillaRecorrido;
    }

    // Getters y Setters

    public Integer getRegistroRecorridoId() {
        return registroRecorridoId;
    }

    public void setRegistroRecorridoId(Integer registroRecorridoId) {
        this.registroRecorridoId = registroRecorridoId;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Corral getCorral() {
        return corral;
    }

    public void setCorral(Corral corral) {
        this.corral = corral;
    }

    public PlanillaRecorrido getPlanillaRecorrido() {
        return planillaRecorrido;
    }

    public void setPlanillaRecorrido(PlanillaRecorrido planillaRecorrido) {
        this.planillaRecorrido = planillaRecorrido;
    }
}
