package gestor.feedlotapp.controller;

import gestor.feedlotapp.entities.Venta;
import gestor.feedlotapp.service.VentaService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/venta")
@Validated
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping
    public ResponseEntity<List<Venta>> getAll() {
        return ResponseEntity.ok(ventaService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venta> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ventaService.getById(id));
    }

    @PostMapping
    public ResponseEntity<Venta> create(@Valid @RequestBody Venta venta) {
        Venta creado = ventaService.create(venta);
        URI location = URI.create("/api/venta/" + creado.getVentaId());
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Venta> update(
            @PathVariable Integer id,
            @Valid @RequestBody Venta venta) {
        return ResponseEntity.ok(ventaService.update(id, venta));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        ventaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
