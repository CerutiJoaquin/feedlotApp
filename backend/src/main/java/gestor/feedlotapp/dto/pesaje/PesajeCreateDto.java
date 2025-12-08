package gestor.feedlotapp.dto.pesaje;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record PesajeCreateDto(
        LocalDate fecha,
        @Positive float peso,
        Long animalId,
        String caravana

) {}
