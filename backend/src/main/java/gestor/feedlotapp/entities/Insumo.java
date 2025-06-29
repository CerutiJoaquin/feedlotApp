package gestor.feedlotapp.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "insumo")
public class Insumo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "insumo_id")
    private int insumoId;
    private String nombre;
    private String tipo;
    @Column(name = "cantidad_actual")
    private float cantidadActual;
    @Column(name = "cantidad_minima")
    private float cantidadMinima;

    @Column(name = "unidad_medida")
    private String unidadMedida;


    // Constructores
    public Insumo(){}

    public Insumo(int insumoId, String nombre, String tipo, float cantidadActual, float cantidadMinima, String unidadMedida) {
        insumoId = insumoId;
        this.nombre = nombre;
        this.tipo = tipo;
        this.cantidadActual = cantidadActual;
        this.cantidadMinima = cantidadMinima;
        this.unidadMedida = unidadMedida;
    }
    //Getters y Setters

    public int getInsumoId() {
        return insumoId;
    }

    public void setInsumoId(int insumoId) {
        insumoId = insumoId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public float getCantidadActual() {
        return cantidadActual;
    }

    public void setCantidadActual(float cantidadActual) {
        this.cantidadActual = cantidadActual;
    }

    public float getCantidadMinima() {
        return cantidadMinima;
    }

    public void setCantidadMinima(float cantidadMinima) {
        this.cantidadMinima = cantidadMinima;
    }
    public String getUnidadMedida() {
        return unidadMedida;
    }
    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }
}
