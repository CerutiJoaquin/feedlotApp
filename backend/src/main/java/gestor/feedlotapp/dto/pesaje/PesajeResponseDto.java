package gestor.feedlotapp.dto.pesaje;

import java.time.LocalDate;

public record PesajeResponseDto(
        Long pesajeId,
        LocalDate fecha,
        float peso,
        Long animalId
) {}
