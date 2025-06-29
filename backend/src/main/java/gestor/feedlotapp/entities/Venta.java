package gestor.feedlotapp.entities;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "venta")
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "venta_id")
    private Integer ventaId;
    @Temporal(TemporalType.DATE)
    private Date fecha;
    private String lote;
    private float monto;
    @ManyToOne
    @JoinColumn(name = "remate_id")
    private Remate remate;
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL)
    private List<VentaLote> lotes;

    // Constructores
    public Venta(){}

    public Venta(int ventaId, Date fecha, String lote, float monto, Remate remate, Cliente cliente, List<VentaLote> lotes) {
        this.ventaId = ventaId;
        this.fecha = fecha;
        this.lote = lote;
        this.monto = monto;
        this.remate = remate;
        this.cliente = cliente;
        this.lotes = lotes;
    }

    // Setters y Getters

    public Integer getVentaId() {
        return ventaId;
    }

    public void setVentaId(Integer ventaId) {
        this.ventaId = ventaId;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public float getMonto() {
        return monto;
    }

    public void setMonto(float monto) {
        this.monto = monto;
    }

    public Remate getRemate() {
        return remate;
    }

    public void setRemate(Remate remate) {
        this.remate = remate;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<VentaLote> getLotes() {
        return lotes;
    }

    public void setLotes(List<VentaLote> lotes) {
        this.lotes = lotes;
    }
}
