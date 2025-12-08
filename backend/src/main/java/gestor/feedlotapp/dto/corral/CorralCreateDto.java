package gestor.feedlotapp.dto.corral;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

// Defino lo mínimo que acepto al crear (contrato de entrada).
public record CorralCreateDto(
        @Positive int capacidadMax,
        @NotBlank String tipoSuperficie,
        @NotBlank String estado
) {}
