package gestor.feedlotapp.dto.pesaje;

import java.time.LocalDate;

public record PesajeUpdateDto(
        LocalDate fecha,
        Float peso,
        Long animalId
) {}
