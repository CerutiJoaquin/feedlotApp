package gestor.feedlotapp.venta.entities;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "mag_precios",
        uniqueConstraints = @UniqueConstraint(columnNames = {"fecha","categoria"}))
public class MagPrecio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;
    private String categoria;
    @Column(name = "precio_prom")
    private BigDecimal precioProm;

    @Column(name = "kgs_prom")
    private BigDecimal kgsProm;
    private String fuente = "MAG";

    @Column(name = "created_at", columnDefinition = "timestamp with time zone")
    private OffsetDateTime createdAt = OffsetDateTime.now();

    // Setters y Getters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getPrecioProm() {
        return precioProm;
    }

    public void setPrecioProm(BigDecimal promedio) {
        this.precioProm = promedio;
    }

    public BigDecimal getKgsProm() {
        return kgsProm;
    }

    public void setKgsProm(BigDecimal kgsProm) {
        this.kgsProm = kgsProm;
    }

    public String getFuente() {
        return fuente;
    }

    public void setFuente(String fuente) {
        this.fuente = fuente;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}