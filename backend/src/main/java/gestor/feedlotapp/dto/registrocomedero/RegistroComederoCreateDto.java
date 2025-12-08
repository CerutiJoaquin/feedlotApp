package gestor.feedlotapp.dto.registrocomedero;

import gestor.feedlotapp.entities.PlanillaComedero;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RegistroComederoCreateDto(
        @NotNull Integer corralId,
        @NotNull Integer insumoId,
        @NotNull @PositiveOrZero BigDecimal cantidad,
        Integer planillaComederoId
) {}
