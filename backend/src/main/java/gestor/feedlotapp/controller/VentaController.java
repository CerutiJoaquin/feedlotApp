package gestor.feedlotapp.controller;


import gestor.feedlotapp.dto.venta.VentaDtos.*;
import gestor.feedlotapp.dto.venta.VentaListDto;
import gestor.feedlotapp.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/venta")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService service;

    @PostMapping("/cotizar")
    public ResponseEntity<CotizarResponse> cotizar(@RequestBody CotizarRequest req) {
        return ResponseEntity.ok(service.cotizar(req));
    }

    @PostMapping
    public ResponseEntity<VentaResponse> crear(@RequestBody CrearVentaRequest req) {
        return ResponseEntity.ok(service.crearVenta(req));
    }

    @GetMapping("/{ventaId}")
    public ResponseEntity<VentaResponse> getById(@PathVariable Long ventaId) {
        return ResponseEntity.ok(service.getVenta(ventaId));
    }

    @GetMapping("/historial")
    public List<VentaListDto> historial(@RequestParam(required = false) Long clienteId) {
        return service.historial(clienteId);
    }
}

