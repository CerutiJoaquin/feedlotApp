package gestor.feedlotapp.dto.remate;

import java.time.LocalDate;

public record RemateResponseDto(
        Integer remateId,
        LocalDate fecha,
        String ubicacion,
        String consignatario,
        String detalles
) {}
