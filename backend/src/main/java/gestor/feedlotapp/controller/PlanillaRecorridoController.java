package gestor.feedlotapp.controller;

import gestor.feedlotapp.entities.PlanillaRecorrido;
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

    private final PlanillaRecorridoService planillaRecorridoService;

    public PlanillaRecorridoController(PlanillaRecorridoService planillaRecorridoService) {
        this.planillaRecorridoService = planillaRecorridoService;
    }

    @GetMapping
    public ResponseEntity<List<PlanillaRecorrido>> getAll() {
        return ResponseEntity.ok(planillaRecorridoService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanillaRecorrido> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(planillaRecorridoService.getById(id));
    }

    @PostMapping
    public ResponseEntity<PlanillaRecorrido> create(@Valid @RequestBody PlanillaRecorrido planillaRecorrido) {
        PlanillaRecorrido creado = planillaRecorridoService.create(planillaRecorrido);
        URI location = URI.create("/api/planillarecorrido/" + creado.getPlanillaRecorridoId());
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlanillaRecorrido> update(
            @PathVariable Integer id,
            @Valid @RequestBody PlanillaRecorrido planillaRecorrido
    ) {
        return ResponseEntity.ok(planillaRecorridoService.update(id, planillaRecorrido));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        planillaRecorridoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
