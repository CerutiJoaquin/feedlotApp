package gestor.feedlotapp.controller;

import gestor.feedlotapp.dto.remate.RemateDto;
import gestor.feedlotapp.service.CacgRematesClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/remates")
public class RematesController {

    private final CacgRematesClient client;

    @GetMapping
    public List<RemateDto> listar(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String provincia,
            @RequestParam(required = false) String modo) {

        List<RemateDto> all = client.fetchAll();
        return all.stream()
                .filter(r -> q == null || (
                        (r.socio()!=null && r.socio().toLowerCase().contains(q.toLowerCase())) ||
                                (r.descripcion()!=null && r.descripcion().toLowerCase().contains(q.toLowerCase()))))
                .filter(r -> provincia == null || (r.provincia()!=null &&
                        r.provincia().toLowerCase().contains(provincia.toLowerCase())))
                .filter(r -> modo == null || (r.modo()!=null &&
                        r.modo().toLowerCase().contains(modo.toLowerCase())))
                .toList();
    }
}
