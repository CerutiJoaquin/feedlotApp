package gestor.feedlotapp.controller;

import gestor.feedlotapp.entities.RegistroRecorrido;
import gestor.feedlotapp.service.RegistroRecorridoService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/registrorecorrido")
@Validated
public class RegistroRecorridoController {

    private final RegistroRecorridoService registroRecorridoService;

    public RegistroRecorridoController(RegistroRecorridoService registroRecorridoService) {
        this.registroRecorridoService = registroRecorridoService;
    }

    @GetMapping
    public ResponseEntity<List<RegistroRecorrido>> getAll() {
        return ResponseEntity.ok(registroRecorridoService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroRecorrido> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(registroRecorridoService.getById(id));
    }

    @PostMapping
    public ResponseEntity<RegistroRecorrido> create(@Valid @RequestBody RegistroRecorrido registroRecorrido) {
        RegistroRecorrido creado = registroRecorridoService.create(registroRecorrido);
        URI location = URI.create("/api/registrorecorrido/" + creado.getRegistroRecorridoId());
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegistroRecorrido> update(
            @PathVariable Integer id,
            @Valid @RequestBody RegistroRecorrido registroRecorrido
    ) {
        return ResponseEntity.ok(registroRecorridoService.update(id, registroRecorrido));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        registroRecorridoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
