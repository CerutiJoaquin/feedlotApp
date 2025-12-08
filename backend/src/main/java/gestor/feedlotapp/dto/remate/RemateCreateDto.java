package gestor.feedlotapp.dto.remate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record RemateCreateDto(
        @NotNull LocalDate fecha,
        @NotBlank String ubicacion,
        @NotBlank String consignatario,
        String detalles
) {}
