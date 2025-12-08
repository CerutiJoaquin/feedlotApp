package gestor.feedlotapp.dto.registrorecorrido;

import jakarta.validation.constraints.NotNull;

public record RegistroRecorridoCreateDto(
        String observaciones,
        @NotNull Integer corralId,
        Integer planillaRecorridoId
) {}
