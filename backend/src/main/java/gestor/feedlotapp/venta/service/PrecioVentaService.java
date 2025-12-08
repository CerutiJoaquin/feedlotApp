package gestor.feedlotapp.venta.service;

import gestor.feedlotapp.venta.entities.MagPrecio;
import gestor.feedlotapp.venta.repository.MagPrecioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PrecioVentaService {

    private final MagPrecioRepository repo;

    public PrecioEnFecha getPrecioKg(String categoria, LocalDate fecha) {
        LocalDate f = (fecha == null) ? LocalDate.now() : fecha;

        var exacto = repo.precioDeFecha(categoria, f);
        if (exacto.isPresent()) return new PrecioEnFecha(exacto.get(), "MAG", f);

        MagPrecio ult = repo.findTopByCategoriaAndFechaLessThanEqualOrderByFechaDescIdDesc(categoria, f)
                .orElseThrow(() -> new IllegalStateException(
                        "Sin precio para '" + categoria + "' en o antes de " + f));

        return new PrecioEnFecha(ult.getPrecioProm(), ult.getFuente(), ult.getFecha());
    }

    public record PrecioEnFecha(BigDecimal precioKg, String fuente, LocalDate fecha) {}
}
