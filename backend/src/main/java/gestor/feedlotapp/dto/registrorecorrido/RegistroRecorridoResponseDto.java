package gestor.feedlotapp.dto.registrorecorrido;

import java.time.LocalDate;

public record RegistroRecorridoResponseDto(
        Integer registroRecorridoId,
        String observaciones,
        LocalDate fecha,
        Integer corralId,
        Integer planillaRecorridoId
) {}
