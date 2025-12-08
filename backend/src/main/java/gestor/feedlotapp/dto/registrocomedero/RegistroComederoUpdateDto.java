package gestor.feedlotapp.dto.registrocomedero;


import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RegistroComederoUpdateDto(
        Integer corralId,
        Integer planillaComederoId,
        @PositiveOrZero BigDecimal cantidadConsumida,
        String observaciones
) {}
