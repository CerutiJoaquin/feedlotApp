package gestor.feedlotapp.controller;

import gestor.feedlotapp.dto.corral.CorralCreateDto;
import gestor.feedlotapp.dto.corral.CorralUpdateDto;
import gestor.feedlotapp.dto.corral.CorralResponseDto;
import gestor.feedlotapp.service.CorralService;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/corral")
@Validated
public class CorralController {

    private final CorralService service;

    public CorralController(CorralService service) {
        this.service = service;
    }


    @GetMapping
    public ResponseEntity<List<CorralResponseDto>> getAll() {
        return ResponseEntity.ok(service.findAllByOrderByCorralIdAsc());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CorralResponseDto> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<CorralResponseDto> create(@Valid @RequestBody CorralCreateDto dto) {
        var creado = service.create(dto);
        URI location = URI.create("/api/corral/" + creado.corralId());
        return ResponseEntity.created(location).body(creado);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<CorralResponseDto> update(
            @PathVariable Integer id,
            @Valid @RequestBody CorralUpdateDto dto
    ) {
        var actualizado = service.update(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
