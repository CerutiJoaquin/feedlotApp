package gestor.feedlotapp.dto.registrotratamiento;

import java.time.LocalDate;

public record RegistroTratamientoResponseDto(
        Integer registroTratamientoId,
        LocalDate fecha,
        Float dosis,
        String nombreInsumo,
        Long animalId,
        String caravana,
        String responsable,
        String descripcion,
        Integer planillaId) {}
