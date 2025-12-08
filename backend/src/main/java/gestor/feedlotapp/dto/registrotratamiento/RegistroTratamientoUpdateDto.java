package gestor.feedlotapp.dto.registrotratamiento;

public record RegistroTratamientoUpdateDto(
        Float dosis,
        Integer medicamentoId,
        Long animalId,
        Integer planillaTratamientoId
) {}
