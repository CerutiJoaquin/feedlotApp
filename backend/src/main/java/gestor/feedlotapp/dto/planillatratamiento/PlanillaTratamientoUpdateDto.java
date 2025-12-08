package gestor.feedlotapp.dto.planillatratamiento;

import java.time.LocalDate;

public record PlanillaTratamientoUpdateDto(
        LocalDate fecha,
        String responsable,
        String observaciones
) {}
