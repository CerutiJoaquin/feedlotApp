package gestor.feedlotapp.controller;

import gestor.feedlotapp.entities.Insumo;
import gestor.feedlotapp.service.InsumoService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/insumo")
@Validated
public class InsumoController {

    private final InsumoService insumoService;

    public InsumoController(InsumoService insumoService) {
        this.insumoService = insumoService;
    }

    @GetMapping
    public ResponseEntity<List<Insumo>> getAll() {
        return ResponseEntity.ok(insumoService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Insumo> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(insumoService.getById(id));
    }

    @PostMapping
    public ResponseEntity<Insumo> create(@Valid @RequestBody Insumo insumo) {
        Insumo creado = insumoService.create(insumo);
        URI location = URI.create("/api/insumo/" + creado.getInsumoId());
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Insumo> update(
            @PathVariable Integer id,
            @Valid @RequestBody Insumo insumo) {
        return ResponseEntity.ok(insumoService.update(id, insumo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        insumoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
