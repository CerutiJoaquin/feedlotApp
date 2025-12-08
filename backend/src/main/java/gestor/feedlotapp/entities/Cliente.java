package gestor.feedlotapp.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cliente_id")
    private int clienteId;

    private String nombre;
    private String apellido;
    private String email;
    private String cuit;
    private String telefono;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Venta> ventas;

    // --- Constructores ---
    public Cliente() {}

    public Cliente(int clienteId, String nombre, String apellido, String email, String cuit, String telefono) {
        this.clienteId = clienteId;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.cuit = cuit;
        this.telefono = telefono;
    }

    public Cliente(int clienteId, String nombre, String apellido, String email, String cuit, String telefono, List<Venta> ventas) {
        this.clienteId = clienteId;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.cuit = cuit;
        this.telefono = telefono;
        this.ventas = ventas;
    }

    // --- Getters / Setters ---
    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCuit() { return cuit; }
    public void setCuit(String cuit) { this.cuit = cuit; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public List<Venta> getVentas() { return ventas; }
    public void setVentas(List<Venta> ventas) { this.ventas = ventas; }
}

