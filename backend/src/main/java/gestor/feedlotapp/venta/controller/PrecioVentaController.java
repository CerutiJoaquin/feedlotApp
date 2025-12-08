package gestor.feedlotapp.venta.controller;

import gestor.feedlotapp.venta.service.PrecioVentaService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/precio-venta")
@Validated
public class PrecioVentaController {

    private final PrecioVentaService service;

    public PrecioVentaController(PrecioVentaService service) {
        this.service = service;
    }


    @GetMapping
    public ResponseEntity<PrecioVentaService.PrecioEnFecha> getPrecio(
            @RequestParam @NotBlank String categoria,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        var dto = service.getPrecioKg(categoria, fecha);
        return ResponseEntity.ok(dto);
    }
    
    @GetMapping("/{categoria}")
    public ResponseEntity<PrecioVentaService.PrecioEnFecha> getPrecioPath(
            @PathVariable("categoria") String categoria,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        var dto = service.getPrecioKg(categoria, fecha);
        return ResponseEntity.ok(dto);
    }
}
