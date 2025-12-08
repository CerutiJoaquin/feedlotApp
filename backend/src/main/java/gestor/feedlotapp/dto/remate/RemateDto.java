package gestor.feedlotapp.dto.remate;

import java.time.LocalDateTime;

public record RemateDto(
        String socio,
        String descripcion,
        String localidad,
        String provincia,
        String modo,
        Integer cabezas,
        LocalDateTime fechaHora,
        String fuenteUrl
) {
}
