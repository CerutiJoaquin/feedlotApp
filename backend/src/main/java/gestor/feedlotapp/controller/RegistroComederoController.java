package gestor.feedlotapp.controller;

import gestor.feedlotapp.entities.RegistroComedero;
import gestor.feedlotapp.service.RegistroComederoService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/registrocomedero")
@Validated
public class RegistroComederoController {

    private final RegistroComederoService registroComederoService;

    public RegistroComederoController(RegistroComederoService registroComederoService) {
        this.registroComederoService = registroComederoService;
    }

    @GetMapping
    public ResponseEntity<List<RegistroComedero>> getAll() {
        return ResponseEntity.ok(registroComederoService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroComedero> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(registroComederoService.getById(id));
    }

    @PostMapping
    public ResponseEntity<RegistroComedero> create(@Valid @RequestBody RegistroComedero registroComedero) {
        RegistroComedero creado = registroComederoService.create(registroComedero);
        URI location = URI.create("/api/registrocomedero/" + creado.getRegistroComederoId());
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegistroComedero> update(
            @PathVariable Integer id,
            @Valid @RequestBody RegistroComedero registroComedero
    ) {
        return ResponseEntity.ok(registroComederoService.update(id, registroComedero));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        registroComederoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
