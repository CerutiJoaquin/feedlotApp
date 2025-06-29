package gestor.feedlotapp.controller;

import gestor.feedlotapp.entities.VentaLote;
import gestor.feedlotapp.service.VentaLoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/ventalote")
@Validated
public class VentaLoteController {

    private final VentaLoteService ventaLoteService;

    public VentaLoteController(VentaLoteService ventaLoteService) {
        this.ventaLoteService = ventaLoteService;
    }

    @GetMapping
    public ResponseEntity<List<VentaLote>> getAll() {
        return ResponseEntity.ok(ventaLoteService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaLote> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ventaLoteService.getById(id));
    }

    @PostMapping
    public ResponseEntity<VentaLote> create(@Valid @RequestBody VentaLote ventaLote) {
        VentaLote creado = ventaLoteService.create(ventaLote);
        URI location = URI.create("/api/ventalote/" + creado.getVentaLoteId());
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VentaLote> update(
            @PathVariable Integer id,
            @Valid @RequestBody VentaLote ventaLote) {
        return ResponseEntity.ok(ventaLoteService.update(id, ventaLote));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        ventaLoteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
