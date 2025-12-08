package gestor.feedlotapp.dto.planillacomedero;

import java.time.LocalDate;

public record PlanillaComederoUpdateDto(
        LocalDate fecha,
        String encargado,
        String observaciones
) {}
