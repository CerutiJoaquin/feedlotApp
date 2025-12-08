package gestor.feedlotapp.dto.insumo;

public record AplicarTratamientoReq(
        Long animalId,
        Integer insumoId,
        Integer dosis,
        String observaciones
) {}
