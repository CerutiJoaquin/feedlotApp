package gestor.feedlotapp.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "registro_comedero")
public class RegistroComedero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "registro_comedero_id")
    private Integer registroComederoId;

    @OneToMany(
            mappedBy = "registroComedero",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<RegistroComederoDetalle> detalles = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "corral_id")
    @JsonIgnoreProperties({"registroComederos", "registroRecorridos", "animales"})
    private Corral corral;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "insumo_id", nullable = false)
    private Insumo insumo;

    @Column(name = "cantidad", precision = 12, scale = 3, nullable = false)
    private BigDecimal cantidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planilla_comedero_id")
    private PlanillaComedero planillaComedero;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @PrePersist
    public void prePersist(){
        if (fecha == null) {
            this.fecha = LocalDate.now(ZoneId.of("America/Argentina/Cordoba"));
        }
    }

    public void addDetalle(RegistroComederoDetalle d) {
        d.setRegistroComedero(this);
        this.detalles.add(d);
    }
    public void removeDetalle(RegistroComederoDetalle d) {
        this.detalles.remove(d);
        d.setRegistroComedero(null);
    }

    public Integer getRegistroComederoId() {
        return registroComederoId;
    }
    public void setRegistroComederoId(Integer registroComederoId) {
        this.registroComederoId = registroComederoId;
    }

    public List<RegistroComederoDetalle> getDetalles() {
        return detalles;
    }
    public void setDetalles(List<RegistroComederoDetalle> detalles) {
        this.detalles = detalles;
    }

    public Corral getCorral() {
        return corral;
    }
    public void setCorral(Corral corral) {
        this.corral = corral;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }
    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public PlanillaComedero getPlanillaComedero() {
        return planillaComedero;
    }
    public void setPlanillaComedero(PlanillaComedero planillaComedero) {
        this.planillaComedero = planillaComedero;
    }

    public Insumo getInsumo() {
        return insumo;
    }

    public void setInsumo(Insumo insumo) {
        this.insumo = insumo;
    }

    public LocalDate getFecha() {
        return fecha;
    }
}
