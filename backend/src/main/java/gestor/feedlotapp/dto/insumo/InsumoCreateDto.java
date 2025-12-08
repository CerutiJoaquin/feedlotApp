package gestor.feedlotapp.dto.insumo;

import gestor.feedlotapp.enums.insumo.Categoria;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record InsumoCreateDto(
        @NotBlank String nombre,
        @NotNull Categoria categoria,
        @NotNull String tipo,
        @PositiveOrZero float cantidad,
        @PositiveOrZero float cantidadMinima,
        @NotBlank String unidadMedida,
        LocalDate fechaIngreso,
        String codigo
) {}
