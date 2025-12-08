package gestor.feedlotapp.dto.insumo;

import gestor.feedlotapp.enums.insumo.Categoria;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record InsumoResponseDto(
        Integer insumoId,
        String nombre,
        Categoria categoria,
        String tipo,
        float cantidad,
        float cantidadMinima,
        String unidadMedida,
        LocalDate fechaIngreso
) {}
