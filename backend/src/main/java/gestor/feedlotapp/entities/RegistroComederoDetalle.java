package gestor.feedlotapp.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "registro_comedero_detalle")
public class RegistroComederoDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detalle_id")
    private Integer comederoDetalleId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "registro_comedero_id")
    private RegistroComedero registroComedero;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "insumo_id")
    private Insumo insumo;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal cantidad;


    public Integer getComederoDetalleId() {
        return comederoDetalleId;
    }

    public void setComederoDetalleId(Integer id) {
        this.comederoDetalleId = id;
    }

    public RegistroComedero getRegistroComedero() {
        return registroComedero;
    }

    public void setRegistroComedero(RegistroComedero registroComedero) {
        this.registroComedero = registroComedero;
    }

    public Insumo getInsumo() {
        return insumo;
    }

    public void setInsumo(Insumo insumo) {
        this.insumo = insumo;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }
}
