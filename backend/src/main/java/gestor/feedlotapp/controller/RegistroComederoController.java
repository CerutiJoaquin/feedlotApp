package gestor.feedlotapp.controller;

import gestor.feedlotapp.dto.registrocomedero.*;
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

    private final RegistroComederoService service;

    public RegistroComederoController(RegistroComederoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<RegistroComederoResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroComederoResponseDto> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<RegistroComederoResponseDto> create(@Valid @RequestBody RegistroComederoCreateDto dto) {
        var creado = service.create(dto);
        URI location = URI.create("/api/registrocomedero/" + creado.registroComederoId());
        return ResponseEntity.created(location).body(creado);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RegistroComederoResponseDto> update(
            @PathVariable Integer id,
            @Valid @RequestBody RegistroComederoUpdateDto dto
    ) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
