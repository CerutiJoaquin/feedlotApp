package gestor.feedlotapp.dto.animal;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AnimalCreateDto(
        @NotBlank String caravana,
        @NotBlank String raza,
        @NotNull  Boolean sexo,
        @NotNull  @Positive BigDecimal pesoActual,
        @NotBlank String  estadoSalud,
        @NotNull  @Positive Integer corralId,
        LocalDate fechaNacimiento,
        LocalDate proxTratamiento
) {}
