package gestor.feedlotapp.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "venta_detalle",
        uniqueConstraints = @UniqueConstraint(name = "uk_venta_detalle_animal", columnNames = "animal_id"))
public class VentaDetalle {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "venta_detalle_id")
    private Long ventaDetalleId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id")
    private Venta venta;

    @ManyToOne(optional = false) @JoinColumn(name = "animal_id")
    private Animal animal;

    private String categoria;

    @Column(name = "peso_kg", precision = 14, scale = 3)
    private BigDecimal pesoKg;

    @Column(name = "precio_kg", precision = 14, scale = 2)
    private BigDecimal precioKg;

    @Column(precision = 14, scale = 2)
    private BigDecimal importe;

    private String fuente;
    private LocalDate fechaPrecio;

    // getters y setters
    public Long getVentaDetalleId() { return ventaDetalleId; }
    public void setVentaDetalleId(Long id) { this.ventaDetalleId = id; }
    public Venta getVenta() { return venta; }
    public void setVenta(Venta venta) { this.venta = venta; }
    public Animal getAnimal() { return animal; }
    public void setAnimal(Animal animal) { this.animal = animal; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public BigDecimal getPesoKg() { return pesoKg; }
    public void setPesoKg(BigDecimal pesoKg) { this.pesoKg = pesoKg; }
    public BigDecimal getPrecioKg() { return precioKg; }
    public void setPrecioKg(BigDecimal precioKg) { this.precioKg = precioKg; }
    public BigDecimal getImporte() { return importe; }
    public void setImporte(BigDecimal importe) { this.importe = importe; }
    public String getFuente() { return fuente; }
    public void setFuente(String fuente) { this.fuente = fuente; }
    public LocalDate getFechaPrecio() { return fechaPrecio; }
    public void setFechaPrecio(LocalDate fechaPrecio) { this.fechaPrecio = fechaPrecio; }

}
