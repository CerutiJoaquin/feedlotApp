package gestor.feedlotapp.controller;

import gestor.feedlotapp.entities.Corral;
import gestor.feedlotapp.service.CorralService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/corral")
@Validated
public class CorralController {

    private final CorralService corralService;

    public CorralController(CorralService corralService) {
        this.corralService = corralService;
    }

    @GetMapping
    public ResponseEntity<List<Corral>> getAll() {
        return ResponseEntity.ok(corralService.findAllByOrderByCorralIdAsc());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Corral> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(corralService.getById(id));
    }

    @PostMapping
    public ResponseEntity<Corral> create(@Valid @RequestBody Corral corral) {
        Corral creado = corralService.create(corral);
        URI location = URI.create("/api/corral/" + creado.getCorralId());
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Corral> update(
            @PathVariable Integer id,
            @Valid @RequestBody Corral corral
    ) {
        Corral actualizado = corralService.corral(id, corral);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        corralService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
