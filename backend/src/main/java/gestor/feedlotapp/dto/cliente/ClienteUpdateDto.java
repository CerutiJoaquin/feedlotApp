package gestor.feedlotapp.dto.cliente;


public record ClienteUpdateDto(
        String nombre,
        String apellido,
        String email,
        String cuit,
        String telefono
) {}
