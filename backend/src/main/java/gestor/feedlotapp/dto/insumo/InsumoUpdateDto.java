package gestor.feedlotapp.dto.insumo;


import gestor.feedlotapp.enums.insumo.Categoria;
import jakarta.validation.constraints.NotNull;

public record InsumoUpdateDto(
        String nombre,
        Categoria categoria,
         String tipo,
        Float cantidad,
        Float cantidadMinima,
        String unidadMedida
) {}
