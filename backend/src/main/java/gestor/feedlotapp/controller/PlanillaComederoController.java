package gestor.feedlotapp.controller;

import gestor.feedlotapp.entities.PlanillaComedero;
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

    private final PlanillaComederoService planillaComederoService;

    public PlanillaComederoController(PlanillaComederoService planillaComederoService) {
        this.planillaComederoService = planillaComederoService;
    }

    @GetMapping
    public ResponseEntity<List<PlanillaComedero>> getAll() {
        return ResponseEntity.ok(planillaComederoService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanillaComedero> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(planillaComederoService.getById(id));
    }

    @PostMapping
    public ResponseEntity<PlanillaComedero> create(@Valid @RequestBody PlanillaComedero planillaComedero) {
        PlanillaComedero creado = planillaComederoService.create(planillaComedero);
        URI location = URI.create("/api/planillacomedero/" + creado.getPlanillaComederoId());
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlanillaComedero> update(
            @PathVariable Integer id,
            @Valid @RequestBody PlanillaComedero planillaComedero
    ) {
        return ResponseEntity.ok(planillaComederoService.update(id, planillaComedero));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        planillaComederoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
