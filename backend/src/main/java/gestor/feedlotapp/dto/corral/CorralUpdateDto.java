package gestor.feedlotapp.dto.corral;


public record CorralUpdateDto(
        Integer capacidadMax,
        String tipoSuperficie,
        String estado
) {}
