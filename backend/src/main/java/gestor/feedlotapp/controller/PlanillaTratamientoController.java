package gestor.feedlotapp.controller;

import gestor.feedlotapp.entities.PlanillaTratamiento;
import gestor.feedlotapp.service.PlanillaTratamientoService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping("/api/planillatratamiento")
@Validated
public class PlanillaTratamientoController {

    private final PlanillaTratamientoService planillaTratamientoService;

    public PlanillaTratamientoController(PlanillaTratamientoService planillaTratamientoService) {
        this.planillaTratamientoService = planillaTratamientoService;
    }

    @GetMapping
    public ResponseEntity<List<PlanillaTratamiento>> getAll() {
        return ResponseEntity.ok(planillaTratamientoService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanillaTratamiento> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(planillaTratamientoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PlanillaTratamiento> create(@Valid @RequestBody PlanillaTratamiento planillaTratamiento) {
        PlanillaTratamiento creado = planillaTratamientoService.create(planillaTratamiento);
        URI location = URI.create("/api/planillatratamiento/" + creado.getPlanillaTratamientoId());
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlanillaTratamiento> update(
            @PathVariable Integer id,
            @Valid @RequestBody PlanillaTratamiento planillaTratamiento
    ) {
        return ResponseEntity.ok(planillaTratamientoService.update(id, planillaTratamiento));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        planillaTratamientoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/fecha")
    public ResponseEntity<PlanillaTratamiento> getByFecha(@RequestParam Date fecha) {
        return planillaTratamientoService.findByFecha(fecha)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/fechas")
    public ResponseEntity<List<PlanillaTratamiento>> getByFechaBetween(
            @RequestParam Date desde,
            @RequestParam Date hasta
    ) {
        return ResponseEntity.ok(
                planillaTratamientoService.findByFechaBetween(desde, hasta)
        );
    }
}
