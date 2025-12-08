package gestor.feedlotapp.dto.registrotratamiento;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RegistroTratamientoCreateDto(

        LocalDate fecha,
        @NotNull Float dosis,
        @NotNull Integer medicamentoId,
        Long animalId,
        String caravana,
        String responsable,
        String descripcion,
        Integer planillaTratamientoId
) {}
