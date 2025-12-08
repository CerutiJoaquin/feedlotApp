package gestor.feedlotapp.dto.planillacomedero;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record PlanillaComederoCreateDto(
        @NotNull LocalDate fecha,
        @NotBlank String encargado,
        String observaciones
) {}
