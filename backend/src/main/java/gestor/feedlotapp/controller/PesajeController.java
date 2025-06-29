package gestor.feedlotapp.controller;

import gestor.feedlotapp.entities.Pesaje;
import gestor.feedlotapp.service.PesajeService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping("/api/pesajes")
@Validated
public class PesajeController {

    private final PesajeService pesajeService;

    public PesajeController(PesajeService pesajeService) {
        this.pesajeService = pesajeService;
    }

    @GetMapping
    public ResponseEntity<List<Pesaje>> getAll() {
        return ResponseEntity.ok(pesajeService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pesaje> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(pesajeService.getById(id));
    }

    @PostMapping
    public ResponseEntity<Pesaje> create(@Valid @RequestBody Pesaje pesaje) {
        Pesaje creado = pesajeService.create(pesaje);
        URI location = URI.create("/api/pesajes/" + creado.getPesajeId());
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pesaje> update(
            @PathVariable Integer id,
            @Valid @RequestBody Pesaje pesaje
    ) {
        return ResponseEntity.ok(pesajeService.update(id, pesaje));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        pesajeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/animal/{animalId}")
    public ResponseEntity<List<Pesaje>> getByAnimalId(@PathVariable Integer animalId) {
        return ResponseEntity.ok(
                pesajeService.getPesajesByAnimalIdOrdered(animalId)
        );
    }

    @GetMapping("/caravana/{caravana}")
    public ResponseEntity<List<Pesaje>> getByCaravana(@PathVariable String caravana) {
        return ResponseEntity.ok(
                pesajeService.getPesajesByCaravanaOrdered(caravana)
        );
    }

    @GetMapping("/fecha")
    public ResponseEntity<List<Pesaje>> getByFechaBetween(
            @RequestParam Date desde,
            @RequestParam Date hasta
    ) {
        return ResponseEntity.ok(
                pesajeService.getPesajesBetweenDates(desde, hasta)
        );
    }
}
