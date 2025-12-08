package gestor.feedlotapp.dto.registrocomedero;


import java.math.BigDecimal;
import java.time.LocalDate;

public record RegistroComederoResponseDto(
        Integer registroComederoId,
        BigDecimal cantidad,
        LocalDate fecha,
        Integer corral,
        Integer insumo,
        Integer planillaComedero
) {}
