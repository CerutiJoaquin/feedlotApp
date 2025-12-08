package gestor.feedlotapp.dto.planillacomedero;

import java.time.LocalDate;

public record PlanillaComederoResponseDto(
        Integer planillaComederoId,
        LocalDate fecha,
        String encargado,
        String observaciones,
        Integer cantidadRegistros
) {}
