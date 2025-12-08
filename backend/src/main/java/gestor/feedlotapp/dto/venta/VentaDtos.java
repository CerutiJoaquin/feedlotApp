package gestor.feedlotapp.dto.venta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class VentaDtos {
    public record CotizarRequest(
            List<Long> animalIds,
            List<String> caravanas,
            LocalDate fecha
    ) {}

    public record ItemCotizado(
            Long animalId,
            String caravana,
            String categoria,
            BigDecimal pesoKg,
            BigDecimal precioKg,
            BigDecimal importe,
            String fuente,
            LocalDate fechaPrecio
    ) {}

    public record CotizarResponse(
            List<ItemCotizado> items,
            BigDecimal total
    ) {}

    public record CrearVentaRequest(
            String descripcion,
            Long clienteId,
            List<Long> animalIds,
            List<String> caravanas,
            LocalDate fecha
    ) {}

    public record VentaDetalleDto(
            Long ventaDetalleId,
            Long animalId,
            String caravana,
            String categoria,
            BigDecimal pesoKg,
            BigDecimal precioKg,
            BigDecimal importe,
            String fuente,
            LocalDate fechaPrecio
    ) {}

    public record VentaResponse(
            Long ventaId,
            LocalDate fecha,
            BigDecimal total,
            Integer clienteId,
            String descripcion,
            List<VentaDetalleDto> detalles,
            String clienteNombre
    ) {}
}
