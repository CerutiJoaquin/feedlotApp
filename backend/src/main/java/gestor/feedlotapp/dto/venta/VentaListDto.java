package gestor.feedlotapp.dto.venta;

import java.math.BigDecimal;
import java.time.LocalDate;

public record VentaListDto(
        Long ventaId,
        LocalDate fecha,
        BigDecimal total,
        String cliente,
        String caravanas
) {}
