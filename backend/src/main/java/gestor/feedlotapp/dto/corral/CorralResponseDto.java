package gestor.feedlotapp.dto.corral;

// Respuesta compacta para listar/detallar.
public record CorralResponseDto(
        Integer corralId,
        int capacidadMax,
        String tipoSuperficie,
        String estado,
        int cabezas
) {}
