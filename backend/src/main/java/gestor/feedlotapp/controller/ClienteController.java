package gestor.feedlotapp.controller;

import gestor.feedlotapp.entities.Cliente;
import gestor.feedlotapp.service.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/cliente")
@Validated
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> getAll() {
        return ResponseEntity.ok(clienteService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(clienteService.getById(id));
    }

    @PostMapping
    public ResponseEntity<Cliente> create(@Valid @RequestBody Cliente cliente) {
        Cliente creado = clienteService.create(cliente);
        URI location = URI.create("/api/cliente/" + creado.getClienteId());
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> update(
            @PathVariable Integer id,
            @Valid @RequestBody Cliente cliente) {
        return ResponseEntity.ok(clienteService.update(id, cliente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        clienteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

