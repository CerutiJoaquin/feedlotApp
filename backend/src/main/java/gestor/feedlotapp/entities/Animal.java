package gestor.feedlotapp.entities;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gestor.feedlotapp.enums.animal.EstadoAnimal;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "animal")
public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "animal_id")
    private Long animalId;
    @Column(nullable = false, unique = true)
    private String caravana;
    @Column(nullable = false)
    private String raza;
    @Column(nullable = false)
    private Boolean sexo;
    @Column(name = "peso_actual", nullable = false, precision = 14, scale = 3)
    private BigDecimal pesoActual;

    @JsonIgnoreProperties
    @Column(name = "peso_inicial", precision = 14, scale = 3)
    private BigDecimal pesoInicial;
    @Column(name = "estado_salud", nullable = false)
    private String estadoSalud;
    @Column(name= "edad_meses")
    private Integer edadMeses;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "fecha_ingreso", nullable = false, updatable = false)
    private LocalDate fechaIngreso;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "corral_id", nullable = false)
    private Corral corral;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoAnimal estado = EstadoAnimal.ACTIVO;

    @Column(name = "fecha_baja")
    private LocalDate fechaBaja;
    @Column(name = "motivo_baja")
    private String motivoBaja;


    @OneToOne
    @JoinColumn(name = "venta_detalle_id")
    private VentaDetalle ventaDetalleBaja;


    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegistroTratamiento> tratamientos = new ArrayList<>();

    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL)
    private List<Pesaje> pesajes = new ArrayList<>();

    @Column(name = "prox_tratamiento")
    private LocalDate proxTratamiento;

    // Pre insert

    @PrePersist
    public void prePersist(){
        if(fechaIngreso == null && pesoInicial == null ){
            fechaIngreso = LocalDate.now(ZoneId.of("America/Argentina/Cordoba"));
            pesoInicial = pesoActual;
        }
    }

    // Setters y Getters

    public Long getAnimalId() {
        return animalId;
    }

    public void setAnimalId(Long animalId) {
        this.animalId = animalId;
    }

    public String getCaravana() {
        return caravana;
    }

    public void setCaravana(String caravana) {
        this.caravana = caravana;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public Boolean getSexo() {
        return sexo;
    }

    public void setSexo(Boolean sexo) {
        this.sexo = sexo;
    }

    public BigDecimal getPesoActual() {
        return pesoActual;
    }

    public void setPesoActual(BigDecimal pesoActual) {
        this.pesoActual = pesoActual;
    }

    public String getEstadoSalud() {
        return estadoSalud;
    }

    public void setEstadoSalud(String estadoSalud) {
        this.estadoSalud = estadoSalud;
    }


    public void setEdadMeses(Integer edadMeses) {
        this.edadMeses = edadMeses;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public Corral getCorral() {
        return corral;
    }

    public void setCorral(Corral corral) {
        this.corral = corral;
    }


    public List<RegistroTratamiento> getTratamientos() {
        return tratamientos;
    }

    public void setTratamientos(List<RegistroTratamiento> tratamientos) {
        this.tratamientos = tratamientos;
    }

    public List<Pesaje> getPesajes() {
        return pesajes;
    }

    public void setPesajes(List<Pesaje> pesajes) {
        this.pesajes = pesajes;
    }

    public LocalDate getProxTratamiento() {
        return proxTratamiento;
    }

    public void setProxTratamiento(LocalDate proxTratamiento) {
        this.proxTratamiento = proxTratamiento;
    }

    public Integer getEdadMeses() {
        return edadMeses;
    }

    public BigDecimal getPesoInicial() {
        return pesoInicial;
    }

    public void setPesoInicial(BigDecimal pesoInicial) {
        this.pesoInicial = pesoInicial;
    }

    public EstadoAnimal getEstado() {
        return estado;
    }

    public void setEstado(EstadoAnimal estado) {
        this.estado = estado;
    }

    public LocalDate getFechaBaja() {
        return fechaBaja;
    }

    public void setFechaBaja(LocalDate fechaBaja) {
        this.fechaBaja = fechaBaja;
    }

    public String getMotivoBaja() {
        return motivoBaja;
    }

    public void setMotivoBaja(String motivoBaja) {
        this.motivoBaja = motivoBaja;
    }

    public VentaDetalle getVentaDetalleBaja() {
        return ventaDetalleBaja;
    }

    public void setVentaDetalleBaja(VentaDetalle ventaDetalleBaja) {
        this.ventaDetalleBaja = ventaDetalleBaja;
    }
}
