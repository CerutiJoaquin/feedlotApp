package gestor.feedlotapp.dto.insumo;


public record InsumoAlertDto(
        Integer insumoId,
        String nombre,
        float cantidad,
        float cantidadMinima,
        float faltante // max(0, cantidadMinima - cantidad)
) {}
