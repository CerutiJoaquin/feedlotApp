package gestor.feedlotapp.controller;

import gestor.feedlotapp.dto.insumo.AplicarTratamientoReq;
import gestor.feedlotapp.dto.registrotratamiento.*;
import gestor.feedlotapp.service.RegistroTratamientoService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/registrotratamiento")
@Validated
public class RegistroTratamientoController {

    private final RegistroTratamientoService registroTratamientoService;

    public RegistroTratamientoController(RegistroTratamientoService registroTratamientoService) {
        this.registroTratamientoService = registroTratamientoService;
    }

    @GetMapping
    public ResponseEntity<List<RegistroTratamientoResponseDto>> getAll() {
        return ResponseEntity.ok(registroTratamientoService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroTratamientoResponseDto> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(registroTratamientoService.getById(id));
    }

    @PostMapping
    public ResponseEntity<RegistroTratamientoResponseDto> create(
            @Valid @RequestBody RegistroTratamientoCreateDto dto) {
        var creado = registroTratamientoService.create(dto);
        URI location = URI.create("/api/registrotratamiento/" + creado.registroTratamientoId());
        return ResponseEntity.created(location).body(creado);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<RegistroTratamientoResponseDto> update(
            @PathVariable Integer id,
            @Valid @RequestBody RegistroTratamientoUpdateDto dto) {
        return ResponseEntity.ok(registroTratamientoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        registroTratamientoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/animal/{animalId}")
    public ResponseEntity<List<RegistroTratamientoResponseDto>> getByAnimalId(
            @PathVariable Long animalId) {
        return ResponseEntity.ok(registroTratamientoService.findByAnimalId(animalId));
    }

    @GetMapping("/caravana/{caravana}")
    public ResponseEntity<List<RegistroTratamientoResponseDto>> getByCaravana(
            @PathVariable String caravana) {
        return ResponseEntity.ok(registroTratamientoService.findByAnimalCaravana(caravana));
    }

    @GetMapping("/fecha")
    public ResponseEntity<List<RegistroTratamientoResponseDto>> getByFechaBetween(
            @RequestParam LocalDate desde,
            @RequestParam LocalDate hasta) {
        return ResponseEntity.ok(registroTratamientoService.findByFechaBetween(desde, hasta));
    }

    @GetMapping("/animal/{animalId}/fecha")
    public ResponseEntity<List<RegistroTratamientoResponseDto>> getByAnimalIdAndFechaBetween(
            @PathVariable Long animalId,
            @RequestParam LocalDate desde,
            @RequestParam LocalDate hasta) {
        return ResponseEntity.ok(registroTratamientoService.findByAnimalIdAndFechaBetween(animalId, desde, hasta));
    }
}
