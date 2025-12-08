package gestor.feedlotapp.dto.cliente;


public record ClienteResponseDto(
        Integer clienteId,
        String nombre,
        String apellido,
        String email,
        String cuit,
        String telefono
) {}
