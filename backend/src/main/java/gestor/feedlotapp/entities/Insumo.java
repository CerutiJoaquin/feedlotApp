package gestor.feedlotapp.entities;

import gestor.feedlotapp.enums.insumo.Categoria;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "insumo")
public class Insumo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "insumo_id")
    private int insumoId;
    private String nombre;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Categoria categoria;

    private String tipo;
    private float cantidad;
    @Column(name = "cantidad_minima")
    private float cantidadMinima;
    @Column(name = "unidad_medida")
    private String unidadMedida;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    @Column(length=50)
    private String codigo;

    @Version
    private Long version;

    @PrePersist
    public void prePersist(){
        if(fechaIngreso == null){
            fechaIngreso = LocalDate.now(ZoneId.of("America/Argentina/Cordoba"));
        }
    }


    @OneToMany(
            mappedBy = "insumo",
            fetch = FetchType.LAZY
    )
    private List<RegistroComederoDetalle> usosEnComederos = new ArrayList<>();


    public Insumo(){}

    public Insumo(int insumoId, String nombre, Categoria categoria, String tipo, float cantidad, float cantidadMinima, String unidadMedida, LocalDate fechaIngreso, String codigo) {
        this.insumoId = insumoId;
        this.nombre = nombre;
        this.categoria = categoria;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.cantidadMinima = cantidadMinima;
        this.unidadMedida = unidadMedida;
        this.fechaIngreso = fechaIngreso;
        this.codigo = codigo;
    }

    public int getInsumoId() { return insumoId; }
    public void setInsumoId(int insumoId) { this.insumoId = insumoId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public float getCantidad() { return cantidad; }
    public void setCantidad(float cantidadActual) { this.cantidad = cantidadActual; }
    public float getCantidadMinima() { return cantidadMinima; }
    public void setCantidadMinima(float cantidadMinima) { this.cantidadMinima = cantidadMinima; }
    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getTipo() {
        return tipo;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public boolean esMedicamento() { return categoria == Categoria.MEDICAMENTO; }
}
