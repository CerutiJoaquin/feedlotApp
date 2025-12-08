package gestor.feedlotapp.controller;

import gestor.feedlotapp.dto.insumo.*;
import gestor.feedlotapp.dto.registrotratamiento.RegistroTratamientoResponseDto;
import gestor.feedlotapp.entities.RegistroTratamiento;
import gestor.feedlotapp.enums.insumo.Categoria;
import gestor.feedlotapp.service.InsumoService;
import gestor.feedlotapp.service.RegistroTratamientoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/insumo")
@CrossOrigin(origins = "*")
public class InsumoController {

    private final InsumoService insumoService;
    private final RegistroTratamientoService registroTratamientoService;

    public InsumoController(InsumoService insumoService, RegistroTratamientoService registroTratamientoService) {
        this.insumoService = insumoService;
        this.registroTratamientoService = registroTratamientoService;
    }

    @GetMapping
    public ResponseEntity<List<InsumoResponseDto>> getAllInsumos() {
        return ResponseEntity.ok(insumoService.getAllInsumos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InsumoResponseDto> getInsumoById(@PathVariable Integer id) {
        return ResponseEntity.ok(insumoService.getInsumoById(id));
    }

    @PostMapping
    public ResponseEntity<InsumoResponseDto> createInsumo(@Valid @RequestBody InsumoCreateDto dto) {
        var creado = insumoService.createInsumo(dto);
        URI location = URI.create("/api/insumo/" + creado.insumoId());
        return ResponseEntity.created(location).body(creado);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<InsumoResponseDto> updateInsumo(
            @PathVariable Integer id,
            @Valid @RequestBody InsumoUpdateDto dto) {
        return ResponseEntity.ok(insumoService.updateInsumo(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInsumo(@PathVariable Integer id) {
        insumoService.deleteInsumo(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(params = "categoria")
    public ResponseEntity<List<InsumoResponseDto>> getInsumoByCategoria(@RequestParam String categoria){
        var cat = Categoria.valueOf(categoria.trim().toUpperCase());
        return ResponseEntity.ok(insumoService.getInsumoByCategoria(cat));
    }

    @GetMapping("/alerts/low-stock")
    public List<InsumoAlertDto> getLowStockAlerts() {
        return insumoService.getLowStockAlerts();
    }

    @GetMapping("/medicamentos")
    public ResponseEntity<Page<InsumoOptionDto>> listMedicamentos(
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "nombre,asc") String sort
    ) {
        String[] s = sort.split(",", 2);
        Sort by = (s.length == 2)
                ? Sort.by(Sort.Direction.fromString(s[1]), s[0])
                : Sort.by("nombre").ascending();

        PageRequest pr = PageRequest.of(page, size, by);
        return ResponseEntity.ok(insumoService.listMedicamentos(q, pr));
    }

}
