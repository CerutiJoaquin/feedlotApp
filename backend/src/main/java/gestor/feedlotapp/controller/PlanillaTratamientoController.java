package gestor.feedlotapp.controller;

import gestor.feedlotapp.dto.planillatratamiento.*;
import gestor.feedlotapp.service.PlanillaTratamientoService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/planillatratamiento")
@Validated
public class PlanillaTratamientoController {

    private final PlanillaTratamientoService service;

    public PlanillaTratamientoController(PlanillaTratamientoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<PlanillaTratamientoResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanillaTratamientoResponseDto> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<PlanillaTratamientoResponseDto> create(@Valid @RequestBody PlanillaTratamientoCreateDto dto) {
        var creado = service.create(dto);
        URI location = URI.create("/api/planillatratamiento/" + creado.planillaTratamientoId());
        return ResponseEntity.created(location).body(creado);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PlanillaTratamientoResponseDto> update(
            @PathVariable Integer id,
            @Valid @RequestBody PlanillaTratamientoUpdateDto dto
    ) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/fecha")
    public ResponseEntity<PlanillaTratamientoResponseDto> getByFecha(@RequestParam LocalDate fecha) {
        return ResponseEntity.ok(service.findByFecha(fecha));
    }

    @GetMapping("/fechas")
    public ResponseEntity<List<PlanillaTratamientoResponseDto>> getByFechaBetween(
            @RequestParam LocalDate desde,
            @RequestParam LocalDate hasta
    ) {
        return ResponseEntity.ok(service.findByFechaBetween(desde, hasta));
    }
}
