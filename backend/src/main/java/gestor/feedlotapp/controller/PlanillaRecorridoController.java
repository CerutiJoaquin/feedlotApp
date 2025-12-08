package gestor.feedlotapp.controller;

import gestor.feedlotapp.dto.planillarecorrido.*;
import gestor.feedlotapp.service.PlanillaRecorridoService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/planillarecorrido")
@Validated
public class PlanillaRecorridoController {

    private final PlanillaRecorridoService service;

    public PlanillaRecorridoController(PlanillaRecorridoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<PlanillaRecorridoResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanillaRecorridoResponseDto> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<PlanillaRecorridoResponseDto> create(@Valid @RequestBody PlanillaRecorridoCreateDto dto) {
        var creado = service.create(dto);
        URI location = URI.create("/api/planillarecorrido/" + creado.planillaRecorridoId());
        return ResponseEntity.created(location).body(creado);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PlanillaRecorridoResponseDto> update(
            @PathVariable Integer id,
            @Valid @RequestBody PlanillaRecorridoUpdateDto dto
    ) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
