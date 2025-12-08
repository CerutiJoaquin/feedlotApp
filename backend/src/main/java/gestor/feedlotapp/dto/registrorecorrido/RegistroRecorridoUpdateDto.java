package gestor.feedlotapp.dto.registrorecorrido;

import java.time.LocalDate;

public record RegistroRecorridoUpdateDto(
        String observaciones,
        Integer corralId,
        LocalDate fecha,
        Integer planillaRecorridoId
) {}
