package gestor.feedlotapp.controller;

import gestor.feedlotapp.dto.planillacomedero.*;
import gestor.feedlotapp.service.PlanillaComederoService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/planillacomedero")
@Validated
public class PlanillaComederoController {

    private final PlanillaComederoService service;

    public PlanillaComederoController(PlanillaComederoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<PlanillaComederoResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanillaComederoResponseDto> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<PlanillaComederoResponseDto> create(@Valid @RequestBody PlanillaComederoCreateDto dto) {
        var creado = service.create(dto);
        URI location = URI.create("/api/planillacomedero/" + creado.planillaComederoId());
        return ResponseEntity.created(location).body(creado);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PlanillaComederoResponseDto> update(
            @PathVariable Integer id,
            @Valid @RequestBody PlanillaComederoUpdateDto dto
    ) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
