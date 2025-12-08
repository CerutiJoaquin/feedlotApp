package gestor.feedlotapp.dto.planillarecorrido;

import java.time.LocalDate;

public record PlanillaRecorridoUpdateDto(
        LocalDate fecha,
        String responsable,
        String observaciones
) {}
