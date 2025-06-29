package gestor.feedlotapp.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "registro_comedero")
public class RegistroComedero {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "registro_comedero_id")
    private Integer registroComederoId;
    private float cantidad_consumida;
    private String observaciones;
    @ManyToOne
    @JoinColumn(name = "corral_id")
    private Corral corral;
    @ManyToOne
    @JoinColumn(name = "planilla_comedero_id")
    private PlanillaComedero planillaComedero;


    // Constructores

    public RegistroComedero() {}

    public RegistroComedero(int registroComederoId, float cantidad_consumida, String observaciones, Corral corral, PlanillaComedero planillaComedero) {
        this.registroComederoId = registroComederoId;
        this.cantidad_consumida = cantidad_consumida;
        this.observaciones = observaciones;
        this.corral = corral;
        this.planillaComedero = planillaComedero;
    }

    // Getters y Setters

    public Integer getRegistroComederoId() {
        return registroComederoId;
    }

    public void setRegistroComederoId(Integer registroComederoId) {
        this.registroComederoId = registroComederoId;
    }

    public float getCantidad_consumida() {
        return cantidad_consumida;
    }

    public void setCantidad_consumida(float cantidad_consumida) {
        this.cantidad_consumida = cantidad_consumida;
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

    public PlanillaComedero getPlanillaComedero() {
        return planillaComedero;
    }

    public void setPlanillaComedero(PlanillaComedero planillaComedero) {
        this.planillaComedero = planillaComedero;
    }
}
