package gestor.feedlotapp.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "venta")
public class Venta {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "venta_id")
    private Long ventaId;

    private LocalDate fecha;

    @Column(precision = 14, scale = 2)
    private BigDecimal total;

    @ManyToOne(optional = true)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VentaDetalle> detalles = new ArrayList<>();

    // getters y setters
    public Long getVentaId() { return ventaId; }
    public void setVentaId(Long ventaId) { this.ventaId = ventaId; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public void addDetalle(VentaDetalle d) {
        detalles.add(d);
        d.setVenta(this);
    }

    public void removeDetalle(VentaDetalle d) {
        detalles.remove(d);
        d.setVenta(null);
    }

    public List<VentaDetalle> getDetalles() {
        return detalles;
    }

    public void recalcularTotal() {
        this.total = detalles.stream()
                .map(VentaDetalle::getImporte)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
