package gestor.feedlotapp.controller;

import gestor.feedlotapp.dto.pesaje.PesajeCreateDto;
import gestor.feedlotapp.dto.pesaje.PesajeUpdateDto;
import gestor.feedlotapp.dto.pesaje.PesajeResponseDto;
import gestor.feedlotapp.service.PesajeService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/pesaje")
public class PesajeController {

    private final PesajeService pesajeService;

    public PesajeController(PesajeService pesajeService) {
        this.pesajeService = pesajeService;
    }

    @GetMapping
    public ResponseEntity<List<PesajeResponseDto>> getAllPesajes() {
        return ResponseEntity.ok(pesajeService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PesajeResponseDto> getPesajeById(@PathVariable Long id) {
        return ResponseEntity.ok(pesajeService.getById(id));
    }

    @PostMapping
    public ResponseEntity<PesajeResponseDto> createPesaje(@Valid @RequestBody PesajeCreateDto dto) {
        var creado = pesajeService.create(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.pesajeId())
                .toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PesajeResponseDto> updatePesaje(
            @PathVariable Long id,
            @Valid @RequestBody PesajeUpdateDto dto
    ) {
        return ResponseEntity.ok(pesajeService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pesajeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/animal/{animalId}")
    public ResponseEntity<List<PesajeResponseDto>> getByAnimalId(@PathVariable Long animalId) {
        return ResponseEntity.ok(pesajeService.getPesajesByAnimalIdOrdered(animalId));
    }

    @GetMapping("/caravana/{caravana}")
    public ResponseEntity<List<PesajeResponseDto>> getByCaravana(@PathVariable String caravana) {
        return ResponseEntity.ok(pesajeService.getPesajesByCaravanaOrdered(caravana));
    }

    @GetMapping("/fecha")
    public ResponseEntity<List<PesajeResponseDto>> getByFechaBetween(
            @RequestParam LocalDate desde,
            @RequestParam LocalDate hasta
    ) {
        return ResponseEntity.ok(pesajeService.getPesajesBetweenDates(desde, hasta));
    }
}
