package gestor.feedlotapp.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "venta_lote")
public class VentaLote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "venta_lote_id")
    private Integer ventaLoteId;
    private String descripcion;
    @ManyToOne
    @JoinColumn(name = "venta_id")
    private Venta venta;

    @OneToMany(mappedBy = "ventaLote", cascade = CascadeType.ALL)
    private List<Animal> animales;

    //Constructores
    public VentaLote(){}

    public VentaLote(int ventaLoteId, String descripcion, Venta venta, List<Animal> animales) {
        this.ventaLoteId = ventaLoteId;
        this.descripcion = descripcion;
        this.venta = venta;
        this.animales = animales;
    }

    // Setters y Getters


    public Integer getVentaLoteId() {
        return ventaLoteId;
    }

    public void setVentaLoteId(Integer ventaLoteId) {
        this.ventaLoteId = ventaLoteId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public List<Animal> getAnimales() {
        return animales;
    }

    public void setAnimales(List<Animal> animales) {
        this.animales = animales;
    }
}
