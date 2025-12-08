package gestor.feedlotapp.controller;

import gestor.feedlotapp.dto.registrorecorrido.*;
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

    private final RegistroRecorridoService service;

    public RegistroRecorridoController(RegistroRecorridoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<RegistroRecorridoResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroRecorridoResponseDto> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<RegistroRecorridoResponseDto> create(@Valid @RequestBody RegistroRecorridoCreateDto dto) {
        var creado = service.create(dto);
        URI location = URI.create("/api/registrorecorrido/" + creado.registroRecorridoId());
        return ResponseEntity.created(location).body(creado);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RegistroRecorridoResponseDto> update(
            @PathVariable Integer id,
            @Valid @RequestBody RegistroRecorridoUpdateDto dto
    ) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
