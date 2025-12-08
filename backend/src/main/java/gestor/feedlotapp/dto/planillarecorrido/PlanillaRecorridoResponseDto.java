package gestor.feedlotapp.dto.planillarecorrido;

import java.time.LocalDate;

public record PlanillaRecorridoResponseDto(
        Integer planillaRecorridoId,
        LocalDate fecha,
        String responsable,
        String observaciones,
        Integer cantidadRegistros
) {}
