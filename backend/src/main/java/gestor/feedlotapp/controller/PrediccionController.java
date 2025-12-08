package gestor.feedlotapp.controller;

import gestor.feedlotapp.dto.predicciones.ConsumoMensualDTO;
import gestor.feedlotapp.dto.predicciones.ConsumoPrediccionDTO;
import gestor.feedlotapp.dto.predicciones.PesoPrediccionDTO;
import gestor.feedlotapp.service.PrediccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/predicciones")
@RequiredArgsConstructor
public class PrediccionController {
    private final PrediccionService service;

    @GetMapping("/peso")
    public ResponseEntity<List<PesoPrediccionDTO>> prediccionPeso(@RequestParam("q") String query) {
        return ResponseEntity.ok(service.predecirPesoAnimal(query));
    }

    @GetMapping("/consumo/{corralId}")
    public ResponseEntity<List<ConsumoPrediccionDTO>> prediccionConsumo(@PathVariable Integer corralId) {
        return ResponseEntity.ok(service.predecirConsumoCorral(corralId));
    }

    @GetMapping("/dashboard/consumo-mensual")
    public ResponseEntity<List<ConsumoMensualDTO>> consumoMensualDashboard(
            @RequestParam(name = "meses", defaultValue = "6") int meses) {

        return ResponseEntity.ok(service.obtenerConsumoMensualAlimento(meses));
    }


}
