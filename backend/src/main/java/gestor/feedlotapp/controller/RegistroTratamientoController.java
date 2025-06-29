package gestor.feedlotapp.controller;

import gestor.feedlotapp.entities.RegistroTratamiento;
import gestor.feedlotapp.service.RegistroTratamientoService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping("/api/registrotratamiento")
@Validated
public class RegistroTratamientoController {

    private final RegistroTratamientoService registroTratamientoService;

    public RegistroTratamientoController(RegistroTratamientoService registroTratamientoService) {
        this.registroTratamientoService = registroTratamientoService;
    }

    @GetMapping
    public ResponseEntity<List<RegistroTratamiento>> getAll() {
        return ResponseEntity.ok(registroTratamientoService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroTratamiento> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(registroTratamientoService.getById(id));
    }

    @PostMapping
    public ResponseEntity<RegistroTratamiento> create(
            @Valid @RequestBody RegistroTratamiento registroTratamiento) {
        RegistroTratamiento creado = registroTratamientoService.create(registroTratamiento);
        URI location = URI.create("/api/registrotratamiento/" + creado.getRegistroTratamientoId());
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegistroTratamiento> update(
            @PathVariable Integer id,
            @Valid @RequestBody RegistroTratamiento registroTratamiento) {
        return ResponseEntity.ok(registroTratamientoService.update(id, registroTratamiento));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        registroTratamientoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/animal/{animalId}")
    public ResponseEntity<List<RegistroTratamiento>> getByAnimalId(
            @PathVariable Integer animalId) {
        return ResponseEntity.ok(
                registroTratamientoService.findByAnimalId(animalId)
        );
    }

    @GetMapping("/caravana/{caravana}")
    public ResponseEntity<List<RegistroTratamiento>> getByCaravana(
            @PathVariable String caravana) {
        return ResponseEntity.ok(
                registroTratamientoService.findByAnimalCaravana(caravana)
        );
    }

    @GetMapping("/fecha")
    public ResponseEntity<List<RegistroTratamiento>> getByFechaBetween(
            @RequestParam Date desde,
            @RequestParam Date hasta) {
        return ResponseEntity.ok(
                registroTratamientoService.findByFechaBetween(desde, hasta)
        );
    }

    @GetMapping("/animal/{animalId}/fecha")
    public ResponseEntity<List<RegistroTratamiento>> getByAnimalIdAndFechaBetween(
            @PathVariable Integer animalId,
            @RequestParam Date desde,
            @RequestParam Date hasta) {
        return ResponseEntity.ok(
                registroTratamientoService.findByAnimalIdAndFechaBetween(animalId, desde, hasta)
        );
    }
}
