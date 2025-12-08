package gestor.feedlotapp.dto.remate;

import java.time.LocalDate;

public record RemateUpdateDto(
        LocalDate fecha,
        String ubicacion,
        String consignatario,
        String detalles
) {}
