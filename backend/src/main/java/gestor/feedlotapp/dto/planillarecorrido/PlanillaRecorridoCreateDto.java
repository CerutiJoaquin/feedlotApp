package gestor.feedlotapp.dto.planillarecorrido;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record PlanillaRecorridoCreateDto(
        @NotNull LocalDate fecha,
        @NotBlank String responsable,
        String observaciones
) {}
