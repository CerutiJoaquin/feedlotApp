package gestor.feedlotapp.entities;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "remate")
public class Remate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "remate_id")
    private Integer remateId;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date fecha;

    @Column(nullable = false)
    private String ubicacion;

    @Column(nullable = false)
    private String consignatario;

    private String detalles;


    public Remate(){}

    public Remate(int remateId, Date fecha, String ubicacion, String consignatario, String detalles) {
        this.remateId = remateId;
        this.fecha = fecha;
        this.ubicacion = ubicacion;
        this.consignatario = consignatario;
        this.detalles = detalles;
    }


    public Integer getRemateId() { return remateId; }
    public void setRemateId(Integer remateId) { this.remateId = remateId; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public String getConsignatario() { return consignatario; }
    public void setConsignatario(String consignatario) { this.consignatario = consignatario; }

    public String getDetalles() { return detalles; }
    public void setDetalles(String detalles) { this.detalles = detalles; }

}
