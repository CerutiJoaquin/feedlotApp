package gestor.feedlotapp.controller;

import gestor.feedlotapp.dto.predicciones.*;
import gestor.feedlotapp.service.PrediccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/predicciones")
@RequiredArgsConstructor
public class PrediccionController {

    private final PrediccionService service;

    @GetMapping("/peso")
    public ResponseEntity<List<PesoPrediccionDTO>> prediccionPeso(
            @RequestParam Long animalId,
            @RequestParam(defaultValue = "6") int meses
    ) {
        return ResponseEntity.ok(
                service.predecirPesoAnimal(animalId, meses)
        );
    }

    @PostMapping("/consumo")
    public ResponseEntity<List<ConsumoPrediccionDTO>> predecirConsumo(
            @RequestBody ConsumoPrediccionRequestDTO req
    ) {
        return ResponseEntity.ok(
                service.predecirConsumoCorral(
                        req.corralId(),
                        req.dias()
                )
        );
    }

    @GetMapping("/consumo-mensual")
    public ResponseEntity<List<ConsumoMensualDTO>> consumoMensualDashboard(
            @RequestParam(defaultValue = "6") int meses
    ) {
        return ResponseEntity.ok(
                service.obtenerConsumoMensualAlimento(meses)
        );
    }
}
