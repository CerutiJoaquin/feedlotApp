package gestor.feedlotapp.dto.animal;

import gestor.feedlotapp.enums.animal.EstadoAnimal;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AnimalResponseDto(
        Long animalId,
        String  caravana,
        String  raza,
        Boolean sexo,
        Integer edadMeses,
        BigDecimal pesoInicial,
        BigDecimal pesoActual,
        String  estadoSalud,
        EstadoAnimal estado,
        Integer corralId,
        LocalDate fechaIngreso,
        LocalDate fechaNacimiento,
        LocalDate proxTratamiento
) {}
