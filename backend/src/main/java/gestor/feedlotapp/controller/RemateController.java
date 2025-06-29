package gestor.feedlotapp.controller;

import gestor.feedlotapp.entities.Remate;
import gestor.feedlotapp.service.RemateService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/remate")
@Validated
public class RemateController {

    private final RemateService remateService;

    public RemateController(RemateService remateService) {
        this.remateService = remateService;
    }

    @GetMapping
    public ResponseEntity<List<Remate>> getAll() {
        return ResponseEntity.ok(remateService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Remate> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(remateService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Remate> create(@Valid @RequestBody Remate remate) {
        Remate creado = remateService.create(remate);
        URI location = URI.create("/api/remate/" + creado.getRemateId());
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Remate> update(
            @PathVariable Integer id,
            @Valid @RequestBody Remate remate) {
        return ResponseEntity.ok(remateService.update(id, remate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        remateService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
