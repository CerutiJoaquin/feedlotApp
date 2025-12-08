package gestor.feedlotapp.dto.planillatratamiento;

import java.time.LocalDate;

public record PlanillaTratamientoResponseDto(
        Integer planillaTratamientoId,
        LocalDate fecha,
        String responsable,
        String observaciones,
        Integer cantidadRegistros
) {}
