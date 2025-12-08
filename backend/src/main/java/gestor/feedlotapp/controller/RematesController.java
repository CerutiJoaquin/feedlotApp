package gestor.feedlotapp.controller;

import gestor.feedlotapp.dto.remate.*;
import gestor.feedlotapp.service.CacgRematesScraper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/remate")
@RequiredArgsConstructor
public class RemateController {
    private final CacgRematesScraper scraper;

    @GetMapping
    public List<RemateDto> list(
            @RequestParam(required = false) String provincia,
            @RequestParam(required = false) String modo,
            @RequestParam(required = false) String q
    ) {
        List<RemateDto> all = scraper.fetchAllWithFallback();

        return all.stream()
                .filter(r -> provincia == null || (r.provincia()!=null && r.provincia().equalsIgnoreCase(provincia)))
                .filter(r -> modo == null || (r.modo()!=null && r.modo().toLowerCase().contains(modo.toLowerCase())))
                .filter(r -> q == null || matches(r, q))
                .toList();
    }

    private boolean matches(RemateDto r, String q) {
        String k = q.toLowerCase();
        return (r.socio()!=null && r.socio().toLowerCase().contains(k)) ||
                (r.descripcion()!=null && r.descripcion().toLowerCase().contains(k)) ||
                (r.localidad()!=null && r.localidad().toLowerCase().contains(k)) ||
                (r.provincia()!=null && r.provincia().toLowerCase().contains(k));
    }
}

