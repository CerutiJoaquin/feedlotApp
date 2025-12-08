package gestor.feedlotapp.venta.controller;

import gestor.feedlotapp.venta.dto.MagSyncResult;
import gestor.feedlotapp.venta.service.MagScraperService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/mag")
@RequiredArgsConstructor
public class MagScraperController {

    private final MagScraperService magService;


    @PostMapping("/sync")
    public ResponseEntity<MagSyncResult> syncDefault() {
        MagSyncResult result = magService.fetchAndUpsertDefault();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/sync/{fecha}")
    public ResponseEntity<MagSyncResult> syncByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        MagSyncResult result = magService.fetchAndUpsertFor(fecha);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/sync/range")
    public ResponseEntity<MagSyncResult> syncByRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        MagSyncResult result = magService.fetchAndUpsertRange(desde, hasta);
        return ResponseEntity.ok(result);
    }
}
