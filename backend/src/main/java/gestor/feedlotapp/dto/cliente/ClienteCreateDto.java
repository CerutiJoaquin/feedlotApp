package gestor.feedlotapp.dto.cliente;

import jakarta.validation.constraints.*;


public record ClienteCreateDto(
        @NotBlank String nombre,
        @NotBlank String apellido,
        @Email(message = "Formato de email inválido") @NotBlank String email,
        @NotBlank String cuit,
        @NotBlank String telefono
) {}
