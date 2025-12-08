package gestor.feedlotapp.dto.animal;

import java.time.LocalDate;

public record AnimalUpdateDto(
        String  estadoSalud,
        Integer corralId,
        LocalDate proxTratamiento
) {}